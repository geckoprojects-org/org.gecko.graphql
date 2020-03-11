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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.core.tests.AbstractOSGiTest;
import org.gecko.core.tests.ServiceChecker;
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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;

import graphql.schema.DataFetchingEnvironment;

@RunWith(MockitoJUnitRunner.class)
public class InvokationTests extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public InvokationTests() {
		super(FrameworkUtil.getBundle(InvokationTests.class).getBundleContext());
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
	public void testGraphQLPureOSGiService() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch envWithParamLatch = new CountDownLatch(1);
		CountDownLatch envLatch = new CountDownLatch(1);
		
		TestService testServiceImpl = new TestService() {
			
			@Override
			public String testMethodWithDataFetchingEnvironmentWithParam(String input, DataFetchingEnvironment env) {
				assertNotNull(input);
				assertNotNull(env);
				envWithParamLatch.countDown();
				return "Response";
			}
			
			@Override
			public String testMethodWithDataFetchingEnvironment(DataFetchingEnvironment env) {
				assertNotNull(env);
				envLatch.countDown();
				return "Response";
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
		latch.await(200, TimeUnit.MILLISECONDS);
		
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
		
		assertTrue(envLatch.await(1, TimeUnit.SECONDS));
		
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService{\\n    testMethodWithDataFetchingEnvironmentWithParam(arg0 : \\\"test\\\")\\n  }\\n}\"\n" + 
				"}"), "application/json");
		response = post.send();
		
		assertEquals(200, response.getStatus());
		
		//TODO we will need to parse the content and look if the response conatins our expected response String
		
//		assertEquals("{" + 
//				"  \"data\": {" + 
//				"    \"TestService\": {\n" + 
//				"      \"testMethodWithDataFetchingEnvironment\": \"Response\"\n" + 
//				"    }\n" + 
//				"  }\n" + 
//				"}", response.getContentAsString());
		
		assertTrue(envWithParamLatch.await(1, TimeUnit.SECONDS));
	}

	@Test
	public void testDate() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch envWithParamLatch = new CountDownLatch(1);
		CountDownLatch envLatch = new CountDownLatch(1);
		
		DateTestService testServiceImpl = new DateTestService() {

			@Override
			public String testDate(Date date) {
				return date.toString();
			}
			
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, DateTestService.class);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date date = new Date();
		
		assertTrue(serviceChecker.awaitModification());
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(200, TimeUnit.MILLISECONDS);
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  DateTestService{\\n    testDate(arg0 : \\\"" + dateFormat.format(date) + "\\\")\\n  }\\n}\\n\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		JsonNode serviceNode = dataNode.get("DateTestService");
		assertNotNull(serviceNode);
		
		JsonNode resultNode = serviceNode.get("testDate");
		assertNotNull(resultNode);
		assertEquals(date.toString(), resultNode.asText());
		
	}

	@Test
	public void testNativeTypeWrapper() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch envWithParamLatch = new CountDownLatch(1);
		CountDownLatch envLatch = new CountDownLatch(1);
		
		IntegerService testServiceImpl = new IntegerService() {
			
			@Override
			public String testInt(int test) {
				return Integer.toString(test);
			}

			@Override
			public String testInteger(Integer test) {
				return test == null ? "empty" : test.toString();
			}
			
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, IntegerService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			fail();
		}
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  IntegerService{\\n    testInteger\\n  }\\n}\\n\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		JsonNode serviceNode = dataNode.get("IntegerService");
		assertNotNull(serviceNode);
		
		JsonNode resultNode = serviceNode.get("testInteger");
		assertNotNull(resultNode);
		assertEquals("empty", resultNode.asText());
		
	}
	
	@Test
	public void testExceptionHandling() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch envWithParamLatch = new CountDownLatch(1);
		CountDownLatch envLatch = new CountDownLatch(1);
		
		IntegerService testServiceImpl = new IntegerService() {
			
			@Override
			public String testInt(int test) {
				return Integer.toString(test);
			}
			
			@Override
			public String testInteger(Integer test) {
				throw new NullPointerException("TestException");
			}
			
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, IntegerService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(200, TimeUnit.MILLISECONDS);
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  IntegerService{\\n    testInteger\\n  }\\n}\\n\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		JsonNode serviceNode = dataNode.get("IntegerService");
		assertNotNull(serviceNode);
		
		JsonNode resultNode = serviceNode.get("testInteger");
		assertTrue(resultNode instanceof NullNode);
		
		JsonNode errorNode = json.get("error");
		
		assertFalse(errorNode instanceof MissingNode);
		
	}
	
	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	public static interface TestService{
		public String testMethodWithDataFetchingEnvironment(DataFetchingEnvironment env);

		public String testMethodWithDataFetchingEnvironmentWithParam(String input, DataFetchingEnvironment env);
	}

	public static interface IntegerService{
		public String testInt(@GraphqlArgument("test") int test);

		public String testInteger(@GraphqlArgument(value = "test", optional = true ) Integer test);
	}

	public static interface DateTestService{
		public String testDate(Date date);
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
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await(200, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			fail();
		}
	}

}