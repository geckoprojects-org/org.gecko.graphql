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

//2023/05/05: temporarily disabled
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
//import org.gecko.core.tests.AbstractOSGiTest;
//import org.gecko.core.tests.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;

//2023/05/05: temporarily disabled
//@RunWith(MockitoJUnitRunner.class)
public class MethodParameterTests {
//public class MethodParameterTests extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public MethodParameterTests() {
		// 2023/05/05: temporarily disabled
		//super(FrameworkUtil.getBundle(MethodParameterTests.class).getBundleContext());
	}

	/**
	 * Look if the service with the marker annotation will be picked up
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	// 2023/05/05: temporarily disabled
	/*
	@Test
	public void testListParameters() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		
		TestService testServiceImpl = new TestService() {

			@Override
			public String testMethod(List<String> fizz) {
				return "Size " + fizz.size();
			}
		};
	
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(100, TimeUnit.MILLISECONDS);
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService{\\n    testMethod(fizz : [\\\"test\\\", \\\"test2\\\"])\\n  }\\n}\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		JsonNode reply = parseJSON(response.getContentAsString());
		
		JsonNode data = reply.path("data");
		assertNotNull(data);
		JsonNode method = data.path("TestService").path("testMethod");
		assertNotNull(method);
		assertEquals("Size 2", method.asText());
	}
	*/

	/**
	 * Look if the service with the marker annotation will be picked up
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	// 2023/05/05: temporarily disabled
	/*
	@Test
	public void testListReturn() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		
		ListReturnTestService testServiceImpl = new ListReturnTestService() {
			
			@Override
			public List<String> testMethod(List<String> fizz) {
				return fizz;
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, ListReturnTestService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(200, TimeUnit.MILLISECONDS);
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  ListReturnTestService{\\n    testMethod(fizz : [\\\"test\\\", \\\"test2\\\"])\\n  }\\n}\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		JsonNode reply = parseJSON(response.getContentAsString());
		
		JsonNode data = reply.path("data");
		assertNotNull(data);
		JsonNode method = data.path("ListReturnTestService").path("testMethod");
		assertTrue(method instanceof ArrayNode);
		assertEquals(2, ((ArrayNode) method).size());
	}
	*/

	// 2023/05/05: temporarily disabled
	/*
	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	*/
	
	public static interface TestService{
		public String testMethod(@GraphqlArgument("fizz") List<String> fizz);
	}

	public static interface ListReturnTestService{
		public List<String> testMethod(@GraphqlArgument("fizz") List<String> fizz);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.util.test.common.test.AbstractOSGiTest#doBefore()
	 */
	// 2023/05/05: temporarily disabled
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
	 * @see org.gecko.util.test.common.test.AbstractOSGiTest#doAfter()
	 */
	// 2023/05/05: temporarily disabled
	/*
	@Override
	public void doAfter() {
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			fail();
		}
	}
	*/

}