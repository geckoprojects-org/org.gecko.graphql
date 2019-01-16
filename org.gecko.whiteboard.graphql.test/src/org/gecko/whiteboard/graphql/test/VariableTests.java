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
package org.gecko.whiteboard.graphql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class VariableTests extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public VariableTests() {
		super(FrameworkUtil.getBundle(VariableTests.class).getBundleContext());
	}

	/**
	 * Look if the service with the marker annotation will be picked up
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testVariableSubstitution() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch envWithParamLatch = new CountDownLatch(1);
		
		TestService testServiceImpl = new TestService() {

			@Override
			public String testMethod(String fizz, String buzz) {
				return fizz + "_" + buzz;
			}
		};
	
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestService.class);
		
		assertTrue(serviceChecker.awaitModification());
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService{\\n    testMethodWithDataFetchingEnvironment\\n  }\\n}\"\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		//TODO we will need to parse the content and look if the response conatins our expected response String
		
//		assertEquals("{" + 
//				"  \"data\": {" + 
//				"    \"TestService\": {\n" + 
//				"      \"testMethodWithDataFetchingEnvironment\": \"Response\"\n" + 
//				"    }\n" + 
//				"  }\n" + 
//				"}", response.getContentAsString());
		
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query _($fizz:String!, $buzz:String!){\\n TestService{\\n  testMethod(fizz : $fizz, buzz : $buzz)\\n}\\n}\",\n" + 
				"  \"variables\": {\n" + 
				"    \"fizz\": \"foo\",\n" + 
				"    \"buzz\": \"bar\"\n" + 
				"  }\n" + 
				"}"), "application/json");
		response = post.send();
		
		assertEquals(200, response.getStatus());
		
		JsonNode reply = parseJSON(response.getContentAsString());
		
		JsonNode data = reply.path("data");
		assertNotNull(data);
		JsonNode method = data.path("TestService").path("testMethod");
		assertNotNull(method);
		assertEquals("foo_bar", method.asText());
	}

	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	public static interface TestService{
		public String testMethod(@GraphqlArgument("fizz") String fizz, @GraphqlArgument("buzz") String buzz);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.common.test.AbstractOSGiTest#doBefore()
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
	 * @see org.gecko.util.test.common.test.AbstractOSGiTest#doAfter()
	 */
	@Override
	public void doAfter() {
		// TODO Auto-generated method stub
		
	}

}