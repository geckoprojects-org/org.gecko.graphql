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
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
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
		Request post = client.POST("http://localhost:8081/graphql");
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
		
		post = client.POST("http://localhost:8081/graphql");
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
	
	
	
	/**
	 * Test Service with Integer and multiple String arguments.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService2() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
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
			public String testMethodWithDataFetchingEnvironmentAndMultipleParams(String inputOne, String inputTwo, String inputThree, DataFetchingEnvironment env) {
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
		Request post = client.POST("http://localhost:8081/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService2 {\\n    testMethodWithDataFetchingEnvironmentAndIntegerParam(arg0 : 30)\\n  }\\n}\"\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		assertTrue(envWithIntParamLatch.await(1, TimeUnit.SECONDS));
		
		post = client.POST("http://localhost:8081/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService2 {\\n    testMethodWithDataFetchingEnvironmentAndMultipleParams(arg0: \\\"test0\\\", arg1: \\\"test1\\\", arg2: \\\"test2\\\")\\n  }\\n}\"\n" + 
				"}"), "application/json");
		response = post.send();
		assertEquals(200, response.getStatus());
		assertTrue(envWithMultipleParamLatch.await(1, TimeUnit.SECONDS));
	}
	
	
	
	
	/**
	 * Test Service with Enum.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService3() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
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
			public Continent testMethodWithDataFetchingEnvironmentContinentEnum(String continentName, DataFetchingEnvironment env) {
				Continent continent = Continent.valueOf(continentName);
				continent.getContinent(continentName);
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
		Request post = client.POST("http://localhost:8081/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService3 {\\n    testMethodWithDataFetchingEnvironmentContinentEnum(arg0 : EUROPE)\\n  }\\n}\"\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	
	/**
	 * Test Service with Custom Object.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLTestService4() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
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
			public Country testMethodWithDataFetchingEnvironmentCountryObject(String continentName, String countryName, String language, int sizeInSqurKm, int inhabitants) {
				Country country = new Country();
				Continent continent = Continent.valueOf(continentName);
				continent.getContinent(continentName);
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
		Request post = client.POST("http://localhost:8081/graphql");
		post.content(new StringContentProvider("{\n" + 
				"  \"query\": \"query {\\n  TestService4 {\\n\\t\\ttestMethodWithDataFetchingEnvironmentCountryObject(arg0: \\\"Sweden\\\", arg1: \\\"swedish\\\", arg2: 480000, arg3: 8500000) {\\n\\t\\t  name\\n\\t\\t  language\\n\\t\\t  sizeInSqurKm\\n\\t\\t  inhabitants\\n\\t\\t  continent\\n\\t\\t}\\n  }\\n}\",\n" + 
				"  \"variables\": {}\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
	}
	
	
	
	// creates TestService for Test 1.
	public static interface TestService{
		public String testMethodWithDataFetchingEnvironment(DataFetchingEnvironment env);

		public String testMethodWithDataFetchingEnvironmentWithParam(String input, DataFetchingEnvironment env);
	}
	
	// creates TestService for Test 2.
	public static interface TestService2{
		public int testMethodWithDataFetchingEnvironmentAndIntegerParam(int input, DataFetchingEnvironment env);
		
		public String testMethodWithDataFetchingEnvironmentAndMultipleParams(String inputOne, String inputTwo, String inputThree, DataFetchingEnvironment env);
	}
	
	// creates TestService for Test 3.
	public static interface TestService3{
		public Continent testMethodWithDataFetchingEnvironmentContinentEnum(String continent, DataFetchingEnvironment env);
		
	}
	
	// creates TestService for Test 4.
	public static interface TestService4{
		public Country testMethodWithDataFetchingEnvironmentCountryObject(String continent, String countryName, String language, int sizeInSqurKm, int inhabitants);
		
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