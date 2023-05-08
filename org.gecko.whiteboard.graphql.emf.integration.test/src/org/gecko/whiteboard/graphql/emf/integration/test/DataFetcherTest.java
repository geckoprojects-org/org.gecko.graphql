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

import static org.assertj.core.api.Assertions.assertThat;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
//import org.gecko.core.tests.AbstractOSGiTest;
//import org.gecko.core.tests.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestFactory;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.SKU;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.commons.annotation.Testable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataFetcherTest {
	
	@InjectBundleContext
	BundleContext bundleContext;
	
//	private HttpClient client;	
	
	@Disabled
	@Order(value = -1)
	@Test
	public void testServices(
			@InjectService(cardinality = 1, 
				timeout = 5000,
				filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware, 
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder)") ServiceAware<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderAware, 
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter)") ServiceAware<GeckoGraphQLValueConverter> graphQLValueConverterAware) {
				
		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);	
		ServiceReference<GraphqlServiceRuntime> graphqlServiceRuntimeRef = graphqlServiceRuntimeAware.getServiceReference();
		assertThat(graphqlServiceRuntimeRef).isNotNull();	
		
		assertThat(graphqlSchemaTypeBuilderAware.getServices()).hasSize(1);	
		ServiceReference<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderRef = graphqlSchemaTypeBuilderAware.getServiceReference();
		assertThat(graphqlSchemaTypeBuilderRef).isNotNull();
		
		assertThat(graphQLValueConverterAware.getServices()).hasSize(1);	
		ServiceReference<GeckoGraphQLValueConverter> graphQLValueConverterRef = graphQLValueConverterAware.getServiceReference();
		assertThat(graphQLValueConverterRef).isNotNull();
	}

	/**
	 * Creates a new instance.
	 * @param bundleContext
	 */
	/*
	public DataFetcherTest() {
		// 2023/05/05: temporarily disabled;
	//	super(FrameworkUtil.getBundle(DataFetcherTest.class).getBundleContext());
	}
	*/
	
	/*
	public static interface Service {
		
		public org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.DataFetcherTest doSomething();
	}
	*/

	// 2023/05/05: temporarily disabled;
	/*
	@Test
	public void testDataFetcher() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.DataFetcherTest testObject = GraphQLTestFactory.eINSTANCE.createDataFetcherTest();
		testObject.setTest("test");
		SKU sku1 = GraphQLTestFactory.eINSTANCE.createSKU();
		sku1.setId("1");
		SKU sku2 = GraphQLTestFactory.eINSTANCE.createSKU();
		sku2.setId("2");
		SKU sku3 = GraphQLTestFactory.eINSTANCE.createSKU();
		sku3.setId("3");
		
		testObject.getSkus().add(sku1);
		
		Dictionary<String, Object> testProps = new Hashtable<>();
		testProps.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		Service testServiceImpl = new Service() {

			@Override
			public org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.DataFetcherTest doSomething() {
				return testObject;
			}

			
		};
	
		CountDownLatch latch = new CountDownLatch(1);
		CountDownLatch objectEquaksLatch = new CountDownLatch(1);
		
		Dictionary<String, Object> dataFetcherProps = new Hashtable<>();
		dataFetcherProps.put("test", "1");

		DataFetcher<Object> fetcher = new DataFetcher<Object>() {

			@Override
			public Object get(DataFetchingEnvironment environment) throws Exception {
				latch.countDown();
				if(testObject.equals(environment.getSource())) {
					objectEquaksLatch.countDown();
				}
				return Arrays.asList(sku2, sku3);
			}
		}; 
		
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(fetcher, dataFetcherProps, DataFetcher.class);

		
		registerServiceForCleanup(testServiceImpl, testProps, Service.class);
		assertTrue(serviceChecker.awaitModification());

		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  Service{\\n    doSomething{\\n      test\\n      skus{\\n        id\\n      }\\n    }\\n  }\\n}\",\n" + 
				"  \"variables\": {\n" + 
				"    }\n" + 
				"  }\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		assertTrue(latch.await(10, TimeUnit.MILLISECONDS));
		assertTrue(objectEquaksLatch.await(10, TimeUnit.MILLISECONDS));
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);

		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);

		JsonNode responseNode = dataNode.get("Service").get("doSomething");
		assertNotNull(responseNode);
		assertEquals(testObject.getTest(), responseNode.get("test").asText());

		ArrayNode arrayNode = (ArrayNode) responseNode.get("skus");
		assertNotEquals(testObject.getSkus().size(), arrayNode.size());
		assertEquals(2, arrayNode.size());
		
	}
	*/

	// 2023/05/05: temporarily disabled;
	// Helper method to parse JSON.
	/*
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	*/
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.AbstractOSGiTest#doBefore()
	 */
	
	// 2023/05/05: temporarily disabled;
	/*
	@Override
	public void doBefore() {
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			assertNull("There should be no exception while starting the jetty client", e);
		}
	}
	*/

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.AbstractOSGiTest#doAfter()
	 */
	// 2023/05/05: temporarily disabled
	/*
	@Override
	public void doAfter() {
		try {
			client.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/

}