/**
 * Copyright (c) 2012 - 2019 Data In Motion and others.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.jetty.client.util.InputStreamContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.test.InvokationTests.TestService4;
import org.gecko.whiteboard.graphql.test.objects.City;
import org.gecko.whiteboard.graphql.test.objects.Continent;
import org.gecko.whiteboard.graphql.test.objects.Country;
import org.gecko.whiteboard.graphql.test.objects.Inhabitant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.schema.DataFetchingEnvironment;

/**
 * 
 * @author ChristophDockhorn
 * @since 06.03.2019
 */
@RunWith(MockitoJUnitRunner.class)
public class PojoParserTest extends AbstractOSGiTest {

	private HttpClient client;
	
	/**
	 * Creates a new instance.
	 * @param bundleContext
	 */
	public PojoParserTest() {
		super(FrameworkUtil.getBundle(PojoParserTest.class).getBundleContext());
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		
	}
	
	
	
	// The following tests are not yet fully implemented and thus commented out.
	
//	/**
//	 * 
//	 * @throws IOException
//	 * @throws InvalidSyntaxException
//	 * @throws InterruptedException
//	 */
//	@Test
//	public void testParseCountry() throws IOException, InvalidSyntaxException, InterruptedException {
//		Dictionary<String, Object> options = new Hashtable<String, Object>();
//		options.put("id", "my.graphql.servlet");
//		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
//		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
//		serviceChecker.setCreateExpectationCount(1);
//		serviceChecker.setCreateTimeout(10);
//		serviceChecker.start();
//		assertTrue(serviceChecker.awaitCreation());
//		CountDownLatch latch = new CountDownLatch(1);
//		
//		CountryTest countryTestImpl = new CountryTest() {
//			
//			@Override
//			public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
//					boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
//					short embassiesWorldwide, float avgAge, double avgWageInDollars, Date nationalHoliday, DataFetchingEnvironment env) {
//				
//				Country country = new Country();
//				country.setName(name);
//				country.setLanguage(language);
//				country.setSizeInSqurKm(sizeInSqurKm);
//				country.setInhabitants(inhabitants);
//				country.setMemberOfUnSecurityCouncil(isMemberOfUNSecurityCouncil);
//				country.setNumberOfOfficialLanguages(numberOfOfficialLanguages);
//				country.setEmbassiesWorldwide(embassiesWorldwide);
//				country.setAvgAge(avgAge);
//				country.setAvgWageInDollars(avgWageInDollars);
//				country.setNationalHoliday(nationalHoliday);
//				
//				latch.countDown();
//				
//				return country;
//			}
//			
//			
//		};
//		
//		
//		
//		Dictionary<String, Object> properties = new Hashtable<>();
//		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
//		serviceChecker.stop();
//		serviceChecker.setModifyExpectationCount(1);
//		serviceChecker.start();
//		registerServiceForCleanup(countryTestImpl, properties, CountryTest.class);
//		assertTrue(serviceChecker.awaitModification());
//
////		Request post = client.POST("http://localhost:8181/graphql");
//		
//		
//	}
//	
//	
//	
//	@Test
//	public void testJustScalarsCountry() throws IOException, InvalidSyntaxException, InterruptedException {
//		Dictionary<String, Object> options = new Hashtable<String, Object>();
//		options.put("id", "my.graphql.servlet");
//		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
//		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
//		serviceChecker.setCreateExpectationCount(1);
//		serviceChecker.setCreateTimeout(10);
//		serviceChecker.start();
//		assertTrue(serviceChecker.awaitCreation());
//		CountDownLatch latch = new CountDownLatch(1);
//		
//		CountryTestJustScalarTypes countryScalarTestImpl = new CountryTestJustScalarTypes() {
//			
//			@Override
//			public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
//					boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
//					short embassiesWorldwide, float avgAge, double avgWageInDollars) {
//				
//				Country country = new Country();
//				country.setName(name);
//				country.setLanguage(language);
//				country.setSizeInSqurKm(sizeInSqurKm);
//				country.setInhabitants(inhabitants);
//				country.setMemberOfUnSecurityCouncil(isMemberOfUNSecurityCouncil);
//				country.setNumberOfOfficialLanguages(numberOfOfficialLanguages);
//				country.setEmbassiesWorldwide(embassiesWorldwide);
//				country.setAvgAge(avgAge);
//				country.setAvgWageInDollars(avgWageInDollars);
//				
//				latch.countDown();
//				
//				return country;
//			}
//			
//			
//		};
//		
//		
//		
//		Dictionary<String, Object> properties = new Hashtable<>();
//		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
//		serviceChecker.stop();
//		serviceChecker.setModifyExpectationCount(1);
//		serviceChecker.start();
//		registerServiceForCleanup(countryScalarTestImpl, properties, CountryTestJustScalarTypes.class);
//		assertTrue(serviceChecker.awaitModification());
//		
////		Request post = client.POST("http://localhost:8181/graphql");
//		
//		
//	}
//	
//	
//	/**
//	 * Checks parsing an object with scalar types.
//	 * @throws IOException
//	 * @throws InvalidSyntaxException
//	 * @throws InterruptedException
//	 * @throws ExecutionException 
//	 * @throws TimeoutException 
//	 */
//	@Test
//	public void testParseCity() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
//		Dictionary<String, Object> options = new Hashtable<String, Object>();
//		options.put("id", "my.graphql.servlet");
//		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
//		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
//		serviceChecker.setCreateExpectationCount(1);
//		serviceChecker.setCreateTimeout(10);
//		serviceChecker.start();
//		assertTrue(serviceChecker.awaitCreation());
//		CountDownLatch latch = new CountDownLatch(1);
//		
//		CityTest cityTestImpl = new CityTest() {
//			
//			@Override
//			public City testCitySchema(String name, int inhabitants,
//					byte famousSights, short districts, boolean isCapital,  
//					float avgAge, double avgWageInDollars, long touristsInTotal) {
//				
//				City city = new City();
//				city.setName(name);
//				city.setInhabitants(inhabitants);
//				city.setFamousSights(famousSights);
//				city.setCapitalCity(isCapital);
//				city.setAvgAge(avgAge);
//				city.setAvgWageInDollars(avgWageInDollars);
//				city.setDistricts(districts);
//				city.setTouristsInTotal(touristsInTotal);
//				
//				latch.countDown();
//				
//				return city;
//			}
//			
//			
//		};
//		
//		
//		
//		Dictionary<String, Object> properties = new Hashtable<>();
//		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
//		serviceChecker.stop();
//		serviceChecker.setModifyExpectationCount(1);
//		serviceChecker.start();
//		registerServiceForCleanup(cityTestImpl, properties, CityTest.class);
//		assertTrue(serviceChecker.awaitModification());
//		
//
//		Request post = client.POST("http://localhost:8181/graphql/schema.json");
//		post.content(new StringContentProvider( "  {\r\n" + 
//				" \"query\": \"query {\\n   CityTest {\\n    testCitySchema(arg0: \\\"Stockholm\\\",\\n  arg1: 600000,\\n    arg2: 12,\\n    arg3: 35,\\n    arg4: true,\\n    arg5: 37.6,\\n    arg6: 44000.324,\\n    arg7: 15000000) {name\\n      inhabitants\\n      famousSights\\n      districts\\n      isCapitalCity\\n      avgAge\\n      avgWageInDollars\\n      touristsInTotal\\n}\\n }\\n }\"\r\n" + "}" + "}"), "application/json");
//		ContentResponse response = post.send();
//		assertEquals(200, response.getStatus());
//		
//		assertTrue(latch.await(1, TimeUnit.SECONDS));
//		
//		JsonNode json = parseJSON(response.getContentAsString());
//		assertNotNull(json);
//
//		
//		
//	}
	
	
	
	/**
	 * Test to check parsing an object with Byte Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testParseInhabitantsByte() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsByteArrayTest inhabitantsTestImpl = new InhabitantsByteArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(byte[] numberOfChildren) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setNumberOfChildren(numberOfChildren);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsByteArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsByteArrayTest {\\n    testInhabitantsSchema(arg0: [12, 13, 14, 15, 16, 17, 18, 19, 20, 21]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertEquals((byte)12, Byte.parseByte(json.path("data").path("InhabitantsByteArrayTest").path("testInhabitantsSchema").path("numberOfChildren").path(0).toString()));
		assertEquals((byte)13, Byte.parseByte(json.path("data").path("InhabitantsByteArrayTest").path("testInhabitantsSchema").path("numberOfChildren").path(1).toString()));

	}
	
	
	/**
	 * Test to check parsing an object with Short Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsShort() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsShortArrayTest inhabitantsTestImpl = new InhabitantsShortArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(short[] iq) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setIq(iq);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsShortArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsShortArrayTest {\\n    testInhabitantsSchema(arg0: [334, 335, 336]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertEquals((short)334, Short.parseShort(json.path("data").path("InhabitantsShortArrayTest").path("testInhabitantsSchema").path("iq").path(0).toString()));
		assertEquals((short)335, Short.parseShort(json.path("data").path("InhabitantsShortArrayTest").path("testInhabitantsSchema").path("iq").path(1).toString()));

		
	}
	
	
	/**
	 * Test to check parsing an object with Int Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsInt() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsIntArrayTest inhabitantsTestImpl = new InhabitantsIntArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(int[] catsOwned) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setCatsOwned(catsOwned);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsIntArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsIntArrayTest {\\n    testInhabitantsSchema(arg0: [25, 2500, 250000]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
			
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		
		assertEquals(25, json.path("data").path("InhabitantsIntArrayTest").path("testInhabitantsSchema").path("catsOwned").path(0).asInt());
		assertEquals(2500, json.path("data").path("InhabitantsIntArrayTest").path("testInhabitantsSchema").path("catsOwned").path(1).asInt());

		
		
	}
	
	
	/**
	 * Test to check parsing an object with Long Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsLong() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsLongArrayTest inhabitantsTestImpl = new InhabitantsLongArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(long[] pizzasEaten) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setPizzasEaten(pizzasEaten);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsLongArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsLongArrayTest {\\n    testInhabitantsSchema(arg0: [5555, 5556, 555799999]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		assertTrue(latch.await(1, TimeUnit.SECONDS));
			
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		
		
		assertEquals(5555L, json.path("data").path("InhabitantsLongArrayTest").path("testInhabitantsSchema").path("pizzasEaten").path(0).asLong());
		assertEquals(5556L, json.path("data").path("InhabitantsLongArrayTest").path("testInhabitantsSchema").path("pizzasEaten").path(1).asLong());

		
	}
	
	
	/**
	 * Test to check parsing an object with Float Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsFloat() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsFloatArrayTest inhabitantsTestImpl = new InhabitantsFloatArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(float[] weight) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setWeight(weight);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsFloatArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		

		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsFloatArrayTest {\\n    testInhabitantsSchema(arg0: [3.45, 3.46, 3.47]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
			
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		assertEquals(3.45F, Float.parseFloat(json.path("data").path("InhabitantsFloatArrayTest").path("testInhabitantsSchema").path("weight").path(0).toString()), 0.1);
		assertEquals(3.46F, Float.parseFloat(json.path("data").path("InhabitantsFloatArrayTest").path("testInhabitantsSchema").path("weight").path(1).toString()), 0.1);

		
		
	}
	
	
	/**
	 * Test to check parsing an object with Double Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsDouble() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsDoubleArrayTest inhabitantsTestImpl = new InhabitantsDoubleArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(double[] wages) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setWages(wages);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsDoubleArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		// check if http request delivers status 200.
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsDoubleArrayTest {\\n    testInhabitantsSchema(arg0: [5.555, 5.556, 5.557]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
					
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		assertEquals(5.555, json.path("data").path("InhabitantsDoubleArrayTest").path("testInhabitantsSchema").path("wages").path(0).asDouble(), 0.1);
		assertEquals(5.556, json.path("data").path("InhabitantsDoubleArrayTest").path("testInhabitantsSchema").path("wages").path(1).asDouble(), 0.1);

		
		
	}
	
	
	/**
	 * Test to check parsing an object with String Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsString() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsStringArrayTest inhabitantsTestImpl = new InhabitantsStringArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(String[] nickNames) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setNickNames(nickNames);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsStringArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsStringArrayTest {\\n    testInhabitantsSchema(arg0: [\\\"Alpha\\\", \\\"bravo\\\", \\\"charlie\\\"]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
					
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		assertEquals("\"Alpha\"", json.path("data").path("InhabitantsStringArrayTest").path("testInhabitantsSchema").path("nickNames").path(0).toString());
		assertEquals("\"bravo\"", json.path("data").path("InhabitantsStringArrayTest").path("testInhabitantsSchema").path("nickNames").path(1).toString());
		assertEquals("\"charlie\"", json.path("data").path("InhabitantsStringArrayTest").path("testInhabitantsSchema").path("nickNames").path(2).toString());

		
	}
	
	
	/**
	 * Test to check parsing an object with Boolean Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsBoolean() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsBooleanArrayTest inhabitantsTestImpl = new InhabitantsBooleanArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(boolean[] isCitizen) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setIsCitizen(isCitizen);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsBooleanArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsBooleanArrayTest {\\n    testInhabitantsSchema(arg0: [false, true, false]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				 + "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
					
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		assertEquals(false, json.path("data").path("InhabitantsBooleanArrayTest").path("testInhabitantsSchema").path("isCitizen").path(0).asBoolean());
		assertEquals(true, json.path("data").path("InhabitantsBooleanArrayTest").path("testInhabitantsSchema").path("isCitizen").path(1).asBoolean());

		
	}
	
	
	
	/**
	 * Test to check parsing an object with Boolean Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseInhabitantsChar() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsCharArrayTest inhabitantsTestImpl = new InhabitantsCharArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(char[] firstLetter) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setFirstLetter(firstLetter);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsCharArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsCharArrayTest {\\n    testInhabitantsSchema(arg0: [\\\"a\\\", \\\"b\\\"]) {\\n    \\tnumberOfChildren\\n      iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n      firstLetter\\n    }\\n  }\\n}\"\r\n" + 
				"}"
				+ "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		assertEquals('a', json.path("data").path("InhabitantsCharArrayTest").path("testInhabitantsSchema").path("firstLetter").path(0).toString().charAt(1));
		assertEquals('b', json.path("data").path("InhabitantsCharArrayTest").path("testInhabitantsSchema").path("firstLetter").path(1).toString().charAt(1));
		
		
	}

	
	/**
	 * Test to check parsing an object with Boolean Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseBooleanEmptyList() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsBooleanArrayTest inhabitantsTestImpl = new InhabitantsBooleanArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(boolean[] isCitizen) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setIsCitizen(isCitizen);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsBooleanArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsBooleanArrayTest {\\n    testInhabitantsSchema(arg0: []) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				+ "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		
		assertNotNull(json.path("data").path("InhabitantsBooleanArrayTest").path("testInhabitantsSchema").path("isCitizen").asBoolean());
		assertEquals(false, json.path("data").path("InhabitantsBooleanArrayTest").path("testInhabitantsSchema").path("isCitizen").path(0).asBoolean());
		
		
		
	}
	
	
	
	/**
	 * Test to check parsing an object with Int Array Input.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseIntegerEmptyList() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsIntArrayTest inhabitantsTestImpl = new InhabitantsIntArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(int[] catsOwned) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setCatsOwned(catsOwned);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsIntArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsIntArrayTest {\\n    testInhabitantsSchema(arg0: []) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				+ "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		assertTrue(latch.await(1, TimeUnit.SECONDS));
		
		
		assertNotNull(json.path("data").path("InhabitantsIntArrayTest").path("testInhabitantsSchema").path("catsOwned").asInt());
		assertEquals(0, json.path("data").path("InhabitantsIntArrayTest").path("testInhabitantsSchema").path("catsOwned").path(0).asInt());
		
		
		
	}

	
	
	
	
	/**
	 * Check if a ValidationError gets thrown when int values are entered, instead of a String.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testParseIntegerInputInStringMethod() throws IOException, InvalidSyntaxException, InterruptedException, TimeoutException, ExecutionException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsStringArrayTest inhabitantsTestImpl = new InhabitantsStringArrayTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(String[] nicknames) {
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setNickNames(nicknames);
				latch.countDown();
				return inhabitant;
			}
			
		};
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsStringArrayTest.class);
		assertTrue(serviceChecker.awaitModification());
		
		
		Request post = client.POST("http://localhost:8181/graphql/schema.json");
		post.content(new StringContentProvider( "  {\r\n" + 
				"  \"query\": \"query {\\n  InhabitantsStringArrayTest {\\n    testInhabitantsSchema(arg0: [12, 14, 24657]) {\\n      numberOfChildren\\n    iq\\n      catsOwned\\n      pizzasEaten\\n      weight\\n      wages\\n      nickNames\\n      isCitizen\\n}\\n  }\\n}\"\r\n" + 
				"}"
				+ "}"), "application/json");
		ContentResponse response = post.send();
		assertEquals(200, response.getStatus());
		
		
		JsonNode json = parseJSON(response.getContentAsString());
		assertNotNull(json);
		// check that ValidationError is thrown
		assertTrue(json.path("errors").path(0).path("errorType").toString().equals("\"ValidationError\""));
		
		
		
	}
	
	
	
	
	@Test
	public void testParsePojo() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		PojoTestService cityPojoTest = new PojoTestService();
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(cityPojoTest, properties, PojoTestService.class);
		assertTrue(serviceChecker.awaitModification());
		
	}
	
	
	
	
	public static class PojoTestService {
		public void helloWorld(String hello, String world) {
			System.out.println(hello + world);
		}
		
	}
	
	
	
	public static interface CountryTest{
		public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
				boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
				short embassiesWorldwide, float avgAge, double avgWageInDollars, Date nationalHoliday, DataFetchingEnvironment env);
	}
	
	public static interface CountryTestJustScalarTypes{
		public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
				boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
				short embassiesWorldwide, float avgAge, double avgWageInDollars);
	}
	
	public static interface CityTest {
		public City testCitySchema(String name, int inhabitants, byte famousSights, short districts, boolean isCapital,
				float avgAge, double avgWageInDollars, long touristsInTotal);
		
	} 
	
	public static interface InhabitantsByteArrayTest {
		public Inhabitant testInhabitantsSchema(byte[] numberOfChildren);
	}
	public static interface InhabitantsShortArrayTest {
		public Inhabitant testInhabitantsSchema(short[] iq);
	}
	public static interface InhabitantsIntArrayTest {
		public Inhabitant testInhabitantsSchema(int[] catsOwned);
	}
	public static interface InhabitantsLongArrayTest {
		public Inhabitant testInhabitantsSchema(long[] pizzasEaten);
	}
	public static interface InhabitantsFloatArrayTest {
		public Inhabitant testInhabitantsSchema(float[] weight);
	}
	public static interface InhabitantsDoubleArrayTest {
		public Inhabitant testInhabitantsSchema(double[] wages);
	}
	public static interface InhabitantsStringArrayTest {
		public Inhabitant testInhabitantsSchema(String[] nickNames);
	}
	public static interface InhabitantsBooleanArrayTest {
		public Inhabitant testInhabitantsSchema(boolean[] isCitizen);
	}
	
	public static interface InhabitantsCharArrayTest {
		public Inhabitant testInhabitantsSchema(char[] firstLetter);
	}
	
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}

	
	public static InputStream getInputStreamWithVarReplacement(String path, String... args ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream resourceAsStream = PojoParserTest.class.getResourceAsStream(path);
		int read = resourceAsStream.read();
		while(read != -1) {
			baos.write(read);
			read = resourceAsStream.read();
		}
		resourceAsStream.close();
		String content = new String(baos.toByteArray());
		content = String.format(content, args);
		System.out.println(content);
		return new ByteArrayInputStream(content.getBytes());
	}
}
