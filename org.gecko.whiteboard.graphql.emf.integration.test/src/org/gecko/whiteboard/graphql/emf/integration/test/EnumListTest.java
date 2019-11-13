/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.emf.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.core.tests.AbstractOSGiTest;
import org.gecko.core.tests.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Currency;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestFactory;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.SKU;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@RunWith(MockitoJUnitRunner.class)
public class EnumListTest extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 * @param bundleContext
	 */
	public EnumListTest() {
		super(FrameworkUtil.getBundle(EnumListTest.class).getBundleContext());
	}
	
	public static interface Service {
		
		public SKU doSomething(List<Currency> currencies);
	}

	@Test
	public void testEnumList() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		
		Dictionary<String, Object> testProps = new Hashtable<>();
		testProps.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");

		final CountDownLatch latch = new CountDownLatch(2);
		
		Service testServiceImpl = new Service() {

			/* 
			 * (non-Javadoc)
			 * @see org.gecko.whiteboard.graphql.emf.integration.test.EnumListTest.Service#doSomething(java.util.List)
			 */
			@Override
			public SKU doSomething(List<Currency> currencies) {
				if(currencies.contains(Currency.DOLLAR)) {
					latch.countDown();
				}
				if(currencies.contains(Currency.EUR)) {
					latch.countDown();
				}
				SKU sku = GraphQLTestFactory.eINSTANCE.createSKU();
				sku.setId("passt");
				return sku;
			}

			
		};
	
//		CountDownLatch objectEquaksLatch = new CountDownLatch(1);
		
		
		registerServiceForCleanup(testServiceImpl, testProps, Service.class);
		assertTrue(serviceChecker.awaitModification());

		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n Service{\\n  doSomething(arg0 : [EUR,DOLLAR]){\\n    id\\n  }\\n}\\n}\"\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);

		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);

		JsonNode responseNode = dataNode.get("Service").get("doSomething");
		assertNotNull(responseNode);
		assertEquals("passt", responseNode.get("id").asText());

		
	}

	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.AbstractOSGiTest#doBefore()
	 */
	@Override
	public void doBefore() {
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			assertNull("There should be no exception while starting the jetty client", e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.AbstractOSGiTest#doAfter()
	 */
	@Override
	public void doAfter() {
		try {
			client.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}