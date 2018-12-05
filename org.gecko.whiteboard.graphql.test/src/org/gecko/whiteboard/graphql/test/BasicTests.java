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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.test.service.api.AddressBookService;
import org.gecko.whiteboard.graphql.test.service.api.AnotherInterface;
import org.gecko.whiteboard.graphql.test.service.impl.AddressBookServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class BasicTests extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public BasicTests() {
		super(FrameworkUtil.getBundle(BasicTests.class).getBundleContext());
	}

	/**
	 * 1. Create a Configuration
	 * 2. Check if a Servlet with a custom property is registered
	 * 3. Check if a GraphQLServiceRuntime service is registered with the custom property
	 * 4. Check if a request to http://localhost:8181/graphql/schema.json returns a Status 200
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 */
	@Test
	public void testGraphQLServlet() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		assertEquals("GeckoGraphQLWhiteboard", configuration.getFactoryPid());
		
		// check if GraphQlServiceRuntime is registered.
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(&(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)(id=my.graphql.servlet))");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
	
		assertTrue(serviceChecker.awaitCreation());
		assertEquals(1, serviceChecker.getCreateExpectationCount());
		
		// check if a Servlet is registered.
		ServiceChecker<Object> serviceChecker2 = createdCheckerTrackedForCleanUp("(&(objectClass=javax.servlet.Servlet)(id=my.graphql.servlet))");
		serviceChecker2.setCreateExpectationCount(1);
		serviceChecker2.setCreateTimeout(10);
		serviceChecker2.start();
	
		assertTrue(serviceChecker2.awaitCreation());
		assertEquals(1, serviceChecker2.getCreateExpectationCount());
		
		
		ContentResponse get = client.GET("http://localhost:8181/graphql/schema.json");
		assertEquals(200, get.getStatus());
	}
	

	/**
	 * 1. Create a GraqphQLWhiteboard
	 * 2. Manually register the {@link AddressBookServiceImpl} as a {@link AddressBookService} with the the {@link GraphqlQueryService} properties
	 * 3. make sure the {@link Constants#SERVICE_CHANGECOUNT} of the {@link GraphqlServiceRuntime} increments
	 * 4. parse the Schema look if a {@link AddressBookService} appears in the query element
	 * 5. unregister the service
	 * 6. make sure the {@link Constants#SERVICE_CHANGECOUNT} of the {@link GraphqlServiceRuntime} increments
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testGraphQLPureOSGiService() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		// create service checker.
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(&(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)(id=my.graphql.servlet))");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(1);
		serviceChecker.start();

		assertFalse(serviceChecker.awaitCreation());
		
		// Create GraphQL Whiteboard.
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		assertTrue(serviceChecker.awaitCreation());
		
		ServiceReference<GraphqlServiceRuntime> serviceReference = getServiceReference(GraphqlServiceRuntime.class);

		long changeCount = (long) serviceReference.getProperty(Constants.SERVICE_CHANGECOUNT);
		
		assertEquals(0L, changeCount);
		
		// register AddressBookServiceImpl as a AddressBookService with the GraphqlQueryService properties.
		Dictionary<String, Object> addressBookProps = new Hashtable<String, Object>();
		addressBookProps.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, new String[]{"*"});
		AddressBookServiceImpl addressBookServiceImpl = new AddressBookServiceImpl();
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(addressBookServiceImpl, addressBookProps, AddressBookService.class);
		assertTrue(serviceChecker.awaitModification());
		// make sure the SERVICE_CHANGECOUNT of the GraphqlServiceRuntime increments.
		changeCount = (long) serviceReference.getProperty(Constants.SERVICE_CHANGECOUNT);
		assertEquals(1L, changeCount);
		
		// check if http request delivers status 200.
		ContentResponse get = client.GET("http://localhost:8181/graphql/schema.json");
		assertEquals(200, get.getStatus());
		// Check if AddressBookService appears in query element
		JsonNode json = parseJSON(get.getContentAsString());
		assertNotNull(json);
		assertTrue(hasAddressBookService(json));
		
		// unregister the service
		unregisterService(addressBookServiceImpl);
		// make sure the change count increments
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		changeCount = (long) serviceReference.getProperty(Constants.SERVICE_CHANGECOUNT);
		assertEquals(2L, changeCount);
		
	
	}

	/**
	 * Register a Service under two interfaces. Both Interfaces must be found as
	 * individual query objects
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testMultipleServiceInterfaces2()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		// register AddressBookServiceImpl as a AddressBookService under two interfaces
		// properties.
		Dictionary<String, Object> addressBookProps = new Hashtable<String, Object>();
		addressBookProps.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, new String[] { AddressBookService.class.getName() });
		AddressBookServiceImpl addressBookServiceImpl = new AddressBookServiceImpl();
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(addressBookServiceImpl, addressBookProps, AddressBookService.class);
		assertTrue(serviceChecker.awaitModification());
		Dictionary<String, Object> addressBookProps2 = new Hashtable<String, Object>();
		addressBookProps2.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_MUTATION_SERVICE, new String[] { AnotherInterface.class.getName() });
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		AddressBookServiceImpl addressBookServiceImpl2 = new AddressBookServiceImpl();
		registerServiceForCleanup(addressBookServiceImpl2, addressBookProps2, AnotherInterface.class);
		assertTrue(serviceChecker.awaitModification());


		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
		// check for status 200 of http request
		ContentResponse get = client.GET("http://localhost:8181/graphql/schema.json");
		assertEquals(200, get.getStatus());
		// Check if both interfaces are found as individual query objects.
		JsonNode json = parseJSON(get.getContentAsString());
		assertNotNull(json);
		assertTrue(hasAddressBookService(json));
		assertTrue(hasAnotherInterface(json));
		
	}
	
	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	// Helper method to check if AddressBookService is contained within JSON.
	public boolean hasAddressBookService (JsonNode json) {
		boolean hasAddressBookServ = false;
		JsonNode types = json.path("data").path("__schema").path("types");
		Iterator<JsonNode> elements = types.elements();
		while (elements.hasNext() && hasAddressBookServ != true) {
			JsonNode name = elements.next().path("name");
			if (name.asText().contentEquals("AddressBookService")) {
				hasAddressBookServ = true;
			}
		}	
		
		return hasAddressBookServ;
	}
	
	// Helper method to check if AnotherInterface is contained within JSON.
	public boolean hasAnotherInterface(JsonNode json) {
		boolean hasInterface = false;
		JsonNode types = json.path("data").path("__schema").path("types");
		Iterator<JsonNode> elements = types.elements();
		while (elements.hasNext() && hasInterface != true) {
			JsonNode name = elements.next().path("name");
			if (name.asText().contentEquals("AnotherInterface")) {
				hasInterface = true;
			}
		}	
		
		return hasInterface;
	}
	
	/**
	 *  Helper method to check if all Methods are included.
	 * @param json
	 * @param serviceName
	 * @param expectedMethods
	 * @return
	 */
	public void hasAllMethods(JsonNode json, String serviceName, List<String> expectedMethods) {
		JsonNode types = json.path("data").path("__schema").path("types");
		Iterator<JsonNode> elements = types.elements();
		boolean found = false;
		while (elements.hasNext()) {
			JsonNode nextNode = elements.next();
			JsonNode name = nextNode.path("name");
			if (name.asText().contentEquals(serviceName)) {
				found = true;
				JsonNode fields = nextNode.path("fields");
//				assertEquals(fields.size(), expectedMethods.size());
				expectedMethods.forEach(methodName -> {
					Optional<String> optional = fields
												.findValues("name")
												.stream()
												.map(nameNode -> nameNode.asText())
												.filter(nameNode -> nameNode.equals(methodName))
												.findFirst();
					assertTrue("method " + methodName + " not found", optional.isPresent());
				});

			}
		}
		assertTrue("Service " + serviceName + " not found", found);
	}

	/**
	 * Register a Service under two interfaces with a given service name. All methods of both interfaces must be part of the named service object.
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testMultipleServiceInterfacesWithServiceNameProperty()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		// register AddressBookServiceImpl as a AddressBookService under two interfaces
		// properties.
		Dictionary<String, Object> addressBookProps = new Hashtable<String, Object>();
		addressBookProps.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE,
				new String[] { AddressBookService.class.getName(), AnotherInterface.class.getName() });
		addressBookProps.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_NAME, "TestServiceName");
		
		AddressBookServiceImpl addressBookServiceImpl = new AddressBookServiceImpl();
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(addressBookServiceImpl, addressBookProps, AddressBookService.class.getName(), AnotherInterface.class.getName());
		assertTrue(serviceChecker.awaitModification());
		
		// build List of all methods that should be included.
		List<String> expectedMethods = new ArrayList<String>();
		expectedMethods.add("getAllAddresses");
		expectedMethods.add("getAddressesByStreet");
		expectedMethods.add("getAllPersons");
//		expectedMethods.add("getAddressesByQuery");
		expectedMethods.add("getPersonByName");
		expectedMethods.add("halloWorld");
//		expectedMethods.add("saveAddresses");
//		expectedMethods.add("savePerson");

		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
		// check for status 200 of http request
		ContentResponse get = client.GET("http://localhost:8181/graphql/schema.json");
		assertEquals(200, get.getStatus());
		JsonNode json = parseJSON(get.getContentAsString());
		assertNotNull(json);
		hasAllMethods(json, "TestServiceName", expectedMethods);

//		System.err.println(get.getContentAsString());

//		TypeDefinitionRegistry schema = schemaParser.parse(get.getContentAsString());

	}
	
	
	
	/**
	 * Define both interfaces as a single query, all methods of both interfaces
	 * should be found under this query.
	 */
	@Test
	public void testMultipleServiceInterfacesInOneQuery()
			throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);

		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();

		assertTrue(serviceChecker.awaitCreation());

		// register AddressBookServiceImpl as a AddressBookService under two interfaces
		// properties.
		Dictionary<String, Object> addressBookProps = new Hashtable<String, Object>();
		addressBookProps.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE,
				new String[] { AddressBookService.class.getName() });
		AddressBookServiceImpl addressBookServiceImpl = new AddressBookServiceImpl();
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(addressBookServiceImpl, addressBookProps, AddressBookService.class);
		assertTrue(serviceChecker.awaitModification());
		Dictionary<String, Object> addressBookProps2 = new Hashtable<String, Object>();
		addressBookProps2.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE,
				new String[] { AnotherInterface.class.getName() });
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		AddressBookServiceImpl addressBookServiceImpl2 = new AddressBookServiceImpl();
		registerServiceForCleanup(addressBookServiceImpl2, addressBookProps2, AnotherInterface.class);
		assertTrue(serviceChecker.awaitModification());
		
		// create Lists of expected methods.
		List<String> addressBookServiceMethods = new ArrayList<String>();
		addressBookServiceMethods.add("getAllAddresses");
		addressBookServiceMethods.add("getAddressesByStreet");
		addressBookServiceMethods.add("getAllPersons");
		addressBookServiceMethods.add("getPersonByName");
//		addressBookServiceMethods.add("getAddressesByQuery");
//		addressBookServiceMethods.add("saveAddresses");
//		addressBookServiceMethods.add("savePerson");
		List<String> anotherInterfaceMethods = new ArrayList<String>();
		anotherInterfaceMethods.add("halloWorld");

		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
		// check for status 200 of http request
		ContentResponse get = client.GET("http://localhost:8181/graphql/schema.json");
		assertEquals(200, get.getStatus());
		JsonNode json = parseJSON(get.getContentAsString());
		assertNotNull(json);
		hasAllMethods(json, "AddressBookService", addressBookServiceMethods);
		hasAllMethods(json, "AnotherInterface", anotherInterfaceMethods);
		
		
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