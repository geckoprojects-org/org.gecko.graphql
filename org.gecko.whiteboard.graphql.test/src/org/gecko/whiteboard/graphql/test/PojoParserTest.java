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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
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
	
	
	@Test
	public void testParseCountry() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		CountryTest countryTestImpl = new CountryTest() {
			
			@Override
			public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
					boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
					short embassiesWorldwide, float avgAge, double avgWageInDollars, Date nationalHoliday, DataFetchingEnvironment env) {
				
				Country country = new Country();
				country.setName(name);
				country.setLanguage(language);
				country.setSizeInSqurKm(sizeInSqurKm);
				country.setInhabitants(inhabitants);
				country.setMemberOfUnSecurityCouncil(isMemberOfUNSecurityCouncil);
				country.setNumberOfOfficialLanguages(numberOfOfficialLanguages);
				country.setEmbassiesWorldwide(embassiesWorldwide);
				country.setAvgAge(avgAge);
				country.setAvgWageInDollars(avgWageInDollars);
				country.setNationalHoliday(nationalHoliday);
				
				latch.countDown();
				
				return country;
			}
			
			
		};
		
		
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(countryTestImpl, properties, CountryTest.class);
		assertTrue(serviceChecker.awaitModification());

//		Request post = client.POST("http://localhost:8181/graphql");
		
		
	}
	
	
	
	@Test
	public void testJustScalarsCountry() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		CountryTestJustScalarTypes countryScalarTestImpl = new CountryTestJustScalarTypes() {
			
			@Override
			public Country testCountrySchema(String name, String language, int sizeInSqurKm, long inhabitants,
					boolean isMemberOfUNSecurityCouncil, byte numberOfOfficialLanguages, 
					short embassiesWorldwide, float avgAge, double avgWageInDollars) {
				
				Country country = new Country();
				country.setName(name);
				country.setLanguage(language);
				country.setSizeInSqurKm(sizeInSqurKm);
				country.setInhabitants(inhabitants);
				country.setMemberOfUnSecurityCouncil(isMemberOfUNSecurityCouncil);
				country.setNumberOfOfficialLanguages(numberOfOfficialLanguages);
				country.setEmbassiesWorldwide(embassiesWorldwide);
				country.setAvgAge(avgAge);
				country.setAvgWageInDollars(avgWageInDollars);
				
				latch.countDown();
				
				return country;
			}
			
			
		};
		
		
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(countryScalarTestImpl, properties, CountryTestJustScalarTypes.class);
		assertTrue(serviceChecker.awaitModification());
		
//		Request post = client.POST("http://localhost:8181/graphql");
		
		
	}
	
	
	
	@Test
	public void testParseCity() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		CityTest cityTestImpl = new CityTest() {
			
			@Override
			public City testCitySchema(String name, int inhabitants,
					byte famousSights, short districts, boolean isCapital,  
					float avgAge, double avgWageInDollars, long touristsInTotal) {
				
				City city = new City();
				city.setName(name);
				city.setInhabitants(inhabitants);
				city.setFamousSights(famousSights);
				city.setCapitalCity(isCapital);
				city.setAvgAge(avgAge);
				city.setAvgWageInDollars(avgWageInDollars);
				city.setDistricts(districts);
				city.setTouristsInTotal(touristsInTotal);
				
				latch.countDown();
				
				return city;
			}
			
			
		};
		
		
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(cityTestImpl, properties, CityTest.class);
		assertTrue(serviceChecker.awaitModification());
		
//		Request post = client.POST("http://localhost:8181/graphql");
		
		
	}
	
	
	
	
	@Test
	public void testParseInhabitants() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		assertTrue(serviceChecker.awaitCreation());
		CountDownLatch latch = new CountDownLatch(1);
		
		InhabitantsTest inhabitantsTestImpl = new InhabitantsTest() {
			
			@Override
			public Inhabitant testInhabitantsSchema(byte[] numberOfChildren, short[] iq, int[] catsOwned, long[] pizzasEaten, 
					float[] weight, double[] wages, String[] nickNames, boolean[] isCitizen) {
				
				Inhabitant inhabitant = new Inhabitant();
				inhabitant.setNumberOfChildren(numberOfChildren);
				inhabitant.setIq(iq);
				inhabitant.setCatsOwned(catsOwned);
				inhabitant.setPizzasEaten(pizzasEaten);
				inhabitant.setWeight(weight);
				inhabitant.setWages(wages);
				inhabitant.setNickNames(nickNames);
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
		registerServiceForCleanup(inhabitantsTestImpl, properties, InhabitantsTest.class);
		assertTrue(serviceChecker.awaitModification());

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
	
	public static interface InhabitantsTest {
		public Inhabitant testInhabitantsSchema(byte[] numberOfChildren, short[] iq, int[] catsOwned, long[] pizzasEaten, 
				float[] weight, double[] wages, String[] nickNames, boolean[] isCitizen);
		
	}

}
