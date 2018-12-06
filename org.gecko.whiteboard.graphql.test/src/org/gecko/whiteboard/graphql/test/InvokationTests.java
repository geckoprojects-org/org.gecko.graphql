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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.test.objects.Continent;
import org.gecko.whiteboard.graphql.test.objects.Country;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

@RunWith(MockitoJUnitRunner.class)
public class InvokationTests extends AbstractOSGiTest {

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public InvokationTests() {
		super(FrameworkUtil.getBundle(InvokationTests.class).getBundleContext());
	}

	/**
	 * Look if the service with the marker annotation will be picked up
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLPureOSGiService()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		options.put(GeckoGraphQLConstants.TRACING_ENABLED, "true");
		Configuration configuration = createConfigForCleanup(
				GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, "?", options);

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

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestService.class);

		assertTrue(serviceChecker.awaitModification());
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService{\\n    testMethodWithDataFetchingEnvironment\\n  }\\n}\"\n"
				+ "}"), "application/json");
		ContentResponse response = post.send();

		assertEquals(200, response.getStatus());

		// TODO we will need to parse the content and look if the response conatins our
		// expected response String

//		assertEquals("{" + 
//				"  \"data\": {" + 
//				"    \"TestService\": {\n" + 
//				"      \"testMethodWithDataFetchingEnvironment\": \"Response\"\n" + 
//				"    }\n" + 
//				"  }\n" + 
//				"}", response.getContentAsString());

		assertTrue(envLatch.await(1, TimeUnit.SECONDS));

		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService{\\n    testMethodWithDataFetchingEnvironmentWithParam(arg0 : \\\"test\\\")\\n  }\\n}\"\n"
				+ "}"), "application/json");
		response = post.send();

		assertEquals(200, response.getStatus());

		// TODO we will need to parse the content and look if the response conatins our
		// expected response String

//		assertEquals("{" + 
//				"  \"data\": {" + 
//				"    \"TestService\": {\n" + 
//				"      \"testMethodWithDataFetchingEnvironment\": \"Response\"\n" + 
//				"    }\n" + 
//				"  }\n" + 
//				"}", response.getContentAsString());

		assertTrue(envWithParamLatch.await(1, TimeUnit.SECONDS));
	}

	/**
	 * Test Service with Integer and multiple String arguments.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService2()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		CountDownLatch envWithIntParamLatch = new CountDownLatch(1);
		CountDownLatch envWithMultipleParamLatch = new CountDownLatch(1);

		TestService2 testService2Impl = new TestService2() {

			@Override
			public int testMethodWithDataFetchingEnvironmentAndIntegerParam(int input, DataFetchingEnvironment env) {
				assertNotNull(input);
				assertNotNull(env);
				envWithIntParamLatch.countDown();
				return input;
			}

			@Override
			public String testMethodWithDataFetchingEnvironmentAndMultipleParams(String inputOne, String inputTwo,
					String inputThree, DataFetchingEnvironment env) {
				assertNotNull(inputOne);
				assertNotNull(inputTwo);
				assertNotNull(inputThree);
				assertNotNull(env);
				envWithMultipleParamLatch.countDown();
				return "Response";
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testService2Impl, properties, TestService2.class);

		assertTrue(serviceChecker.awaitModification());
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService2 {\\n    testMethodWithDataFetchingEnvironmentAndIntegerParam(arg0 : 30)\\n  }\\n}\"\n"
				+ "}"), "application/json");
		ContentResponse response = post.send();

		assertEquals(200, response.getStatus());

		assertTrue(envWithIntParamLatch.await(1, TimeUnit.SECONDS));

		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService2 {\\n    testMethodWithDataFetchingEnvironmentAndMultipleParams(arg0: \\\"test0\\\", arg1: \\\"test1\\\", arg2: \\\"test2\\\")\\n  }\\n}\"\n"
				+ "}"), "application/json");
		response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(envWithMultipleParamLatch.await(1, TimeUnit.SECONDS));
	}

	/**
	 * Test Service with Enum.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService3()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		CountDownLatch latch = new CountDownLatch(1);

		TestService3 testService3Impl = new TestService3() {

			@Override
			public Continent testMethodWithDataFetchingEnvironmentContinentEnum(String continentName,
					DataFetchingEnvironment env) {
				Continent continent = Continent.getContinent(continentName);
				assertNotNull(continent);
				assertNotNull(env);
				latch.countDown();
				return continent;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testService3Impl, properties, TestService3.class);

		assertTrue(serviceChecker.awaitModification());
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService3 {\\n    testMethodWithDataFetchingEnvironmentContinentEnum(arg0: \\\"input\\\")\\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		ContentResponse response = post.send();

		assertEquals(200, response.getStatus());

		assertTrue(latch.await(1, TimeUnit.SECONDS));

	}

	/**
	 * Test Service with Custom Object.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService4()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		CountDownLatch latch = new CountDownLatch(1);

		TestService4 testService4Impl = new TestService4() {

			@Override
			public Country testMethodWithCountryObject(String continentName, String countryName, String language,
					int sizeInSqurKm, int inhabitants) {
				Country country = new Country();
				Continent continent = Continent.getContinent(continentName);
				country.setContinent(continent);
				country.setName(countryName);
				country.setLanguage(language);
				country.setSizeInSqurKm(sizeInSqurKm);
				country.setInhabitants(inhabitants);
				assertNotNull(country);
				latch.countDown();
				return country;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testService4Impl, properties, TestService4.class);

		assertTrue(serviceChecker.awaitModification());
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService4 {\\n    testMethodWithCountryObject(arg0: \\\"europe\\\", arg1: \\\"norway\\\", arg2: \\\"norwegian\\\", arg3: 300000, arg4: 4000000) {\\n      continent\\n      name\\n      language\\n      sizeInSqurKm\\n      inhabitants\\n    }\\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		ContentResponse response = post.send();

		assertEquals(200, response.getStatus());

		assertTrue(latch.await(1, TimeUnit.SECONDS));

	}

	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalar to test is int.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryInt()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {

		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		CountDownLatch latch = new CountDownLatch(1);

		TestServiceScalarTypesInt testServiceImpl = new TestServiceScalarTypesInt() {

			@Override
			public int testMethodForInt(int input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesInt.class);

		assertTrue(serviceChecker.awaitModification());

		// query int
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesInt {\\n    testMethodForInt(arg0: 2)\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));

		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesInt {\\n    testMethodForInt()\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));

	}

	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is long.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryLong()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {

		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		CountDownLatch latch = new CountDownLatch(1);

		TestServiceScalarTypesLong testServiceImpl = new TestServiceScalarTypesLong() {

			@Override
			public long testMethodForLong(long input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}

		};

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");

		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesLong.class);

		assertTrue(serviceChecker.awaitModification());

		// query long
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestServiceScalarTypesLong {\\n    \\n    testMethodForLong(arg0: 234234)\\n  }\\n}\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestServiceScalarTypesLong {\\n    \\n    testMethodForLong()\\n  }\\n}\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));

	}
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is boolean.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryBoolean()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesBoolean testServiceImpl = new TestServiceScalarTypesBoolean() {
			
			@Override
			public boolean testMethodForBoolean(boolean input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesBoolean.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query boolean
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestServiceScalarTypesBoolean {\\n    testMethodForBoolean(arg0: true)\\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestServiceScalarTypesBoolean {\\n    testMethodForBoolean()\\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is double.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryDouble()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesDouble testServiceImpl = new TestServiceScalarTypesDouble() {
			
			@Override
			public double testMethodForDouble(double input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesDouble.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query double
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesDouble {\\n    testMethodForDouble(arg0: 25.5)\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesDouble {\\n    testMethodForDouble()\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is byte.
	 * Doesn't work yet, because GraphQL has problems with byte-values.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryByte()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesByte testServiceImpl = new TestServiceScalarTypesByte() {
			
			@Override
			public byte testMethodForByte(byte input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesByte.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query byte
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypeByte {\\n    testMethodForByte(arg0: 5)\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypeByte {\\n    testMethodForByte()\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is short.
	 * Doesn't work yet, because GraphQL has problems with short-values.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryShort()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesShort testServiceImpl = new TestServiceScalarTypesShort() {
			
			@Override
			public short testMethodForShort(short input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesShort.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query short
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesShort {\\n    testMethodForShort(arg0: 25)\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesShort {\\n    testMethodForShort()\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is char.
	 * Doesn't work yet, because GraphQL has problems with char-values.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryChar()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesChar testServiceImpl = new TestServiceScalarTypesChar() {
			
			@Override
			public char testMethodForChar(char input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesChar.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query char
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesChar {\\n    testMethodForChar(arg0: 'c')\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider(
				"{\n" + "  \"query\": \"query {\\n  TestServiceScalarTypesChar {\\n    testMethodForChar()\\n  }\\n}\",\n"
						+ "  \"variables\": {}\n" + "}"),
				"application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked
	 * as such in the schema. Additionally it must produce a comprehensive error
	 * response, when an invalid query is fired. The scalars to test is float.
	 * Doesn't work yet, because GraphQL has problems with float-values.
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testScalarTypesAsMethodParametersAreMandatoryFloat()
			throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceScalarTypesFloat testServiceImpl = new TestServiceScalarTypesFloat() {
			
			@Override
			public float testMethodForFloat(float input) {
				assertNotNull("Input must not be null", input);
				latch.countDown();
				return input;
			}
			
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(testServiceImpl, properties, TestServiceScalarTypesFloat.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		// query float
		Request post = client.POST("http://localhost:8181/graphql");
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestServiceScalarTypesFloat {\\n    testMethodForFloat(arg0 : 12.232424)\\n    \\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		// check that "Invalid Syntax" error occurs if param is missing.
		post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestServiceScalarTypesFloat {\\n    testMethodForFloat()\\n    \\n  }\\n}\",\n"
				+ "  \"variables\": {}\n" + "}"), "application/json");
		response = post.send();
		String responseContent = response.getContentAsString();
		assertNotNull(responseContent);
		JsonNode json = parseJSON(responseContent);
		assertTrue(paramIsMissing(json));
		assertEquals(200, response.getStatus());
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}

	// creates TestService for Test 1.
	public static interface TestService {
		public String testMethodWithDataFetchingEnvironment(DataFetchingEnvironment env);

		public String testMethodWithDataFetchingEnvironmentWithParam(String input, DataFetchingEnvironment env);
	}

	// creates TestService for Test 2.
	public static interface TestService2 {
		public int testMethodWithDataFetchingEnvironmentAndIntegerParam(int input, DataFetchingEnvironment env);

		public String testMethodWithDataFetchingEnvironmentAndMultipleParams(String inputOne, String inputTwo,
				String inputThree, DataFetchingEnvironment env);
	}

	// creates TestService for Test 3.
	public static interface TestService3 {
		public Continent testMethodWithDataFetchingEnvironmentContinentEnum(String continent,
				DataFetchingEnvironment env);

	}

	// creates TestService for Test 4.
	public static interface TestService4 {
		public Country testMethodWithCountryObject(String continent, String countryName, String language,
				int sizeInSqurKm, int inhabitants);

	}

	// creates TestService to test that int is not null.
	public static interface TestServiceScalarTypesInt {
		public int testMethodForInt(int input);
	}

	// creates TestService to test that float is not null.
	public static interface TestServiceScalarTypesFloat {
		public float testMethodForFloat(float input);
	}

	// creates TestService to test that short is not null.
	public static interface TestServiceScalarTypesShort {
		public short testMethodForShort(short input);
	}

	// creates TestService to test that long is not null.
	public static interface TestServiceScalarTypesLong {
		public long testMethodForLong(long input);
	}

	// creates TestService to test that boolean is not null.
	public static interface TestServiceScalarTypesBoolean {
		public boolean testMethodForBoolean(boolean input);
	}

	// creates TestService to test that byte is not null.
	public static interface TestServiceScalarTypesByte {
		public byte testMethodForByte(byte input);
	}

	// creates TestService to test that char is not null.
	public static interface TestServiceScalarTypesChar {
		public char testMethodForChar(char input);
	}

	// creates TestService to test that double is not null.
	public static interface TestServiceScalarTypesDouble {
		public double testMethodForDouble(double input);
	}
	
	
	
	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	// helper method to check if "Invalid Syntax" error occurs when param is missing.
	public boolean paramIsMissing (JsonNode json) {
		boolean isNull = false;
		JsonNode error = json.path("errors").path(0).path("message");
		if (error.asText().equals("Invalid Syntax")) {
			isNull = true;
		}
		return isNull;
	}
	
	
	

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see org.gecko.util.test.common.test.AbstractOSGiTest#doAfter()
	 */
	@Override
	public void doAfter() {
		// TODO Auto-generated method stub

	}

}