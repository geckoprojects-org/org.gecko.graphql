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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class VariableIntegrationTest extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 * @param bundleContext
	 */
	public VariableIntegrationTest() {
		super(FrameworkUtil.getBundle(VariableIntegrationTest.class).getBundleContext());
	}

	@Test
	public void testSingleVariable() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		VarService testServiceImpl = new VarService() {

			@Override
			public String testVariables(Product prod, String test) {
				return "test";
			}

			@Override
			public String testVariable(Product prod) {
				return prod.getName();
			}
		};
	
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, VarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query _($prod : ProductInput!){\\n VarService{\\n  testVariable(prod : $prod)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prod\": {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);

		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);

		JsonNode responseNode = dataNode.get("VarService").get("testVariable");
		assertNotNull(responseNode);
		assertEquals("name", responseNode.asText());
	}

	@Test
	public void testSingleVariableWithInputList() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		VarService testServiceImpl = new VarService() {
			
			@Override
			public String testVariables(Product prod, String test) {
				return "test";
			}
			
			@Override
			public String testVariable(Product prod) {
				return prod.getInputValueList().size() + "";
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, VarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query _($prod : ProductInput!){\\n VarService{\\n  testVariable(prod : $prod)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prod\": {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"inputValueList\": [\"test1\", \"test2\"],\r\n" + 
				"      \"active\": true\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		
		JsonNode responseNode = dataNode.get("VarService").get("testVariable");
		assertNotNull(responseNode);
		assertEquals("2", responseNode.asText());
	}

	@Test
	public void testSingleVariableWithEnumInputList() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		VarService testServiceImpl = new VarService() {
			
			@Override
			public String testVariables(Product prod, String test) {
				return "test";
			}
			
			@Override
			public String testVariable(Product prod) {
				return prod.getCurrencies().size() + "";
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, VarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query _($prod : ProductInput!){\\n VarService{\\n  testVariable(prod : $prod)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prod\": {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"currencies\": [\r\n" 
							+ "\"EUR\",\r\n"
							+ "\"DOLLAR\"\r\n"
							+ "],\r\n" + 
				"      \"active\": true\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		
		JsonNode responseNode = dataNode.get("VarService").get("testVariable");
		assertNotNull(responseNode);
		assertEquals("2", responseNode.asText());
	}

	@Test
	public void testSingleVariableList() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		ListVarService testServiceImpl = new ListVarService() {

			@Override
			public String testVariables(List<Product> prod) {
				return "Size " + prod.size();
			}
			
		
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, ListVarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query _($prods : [ProductInput]!){\\n ListVarService{\\n  testVariables(prods : $prods)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prods\": [{\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    }]\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		
		JsonNode responseNode = dataNode.get("ListVarService").get("testVariables");
		assertNotNull(responseNode);
		assertEquals("Size 1", responseNode.asText());
		
		
	}

	@Test
	public void testSingleVariableListMultipleValues() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		ListVarService testServiceImpl = new ListVarService() {
			
			@Override
			public String testVariables(List<Product> prod) {
				return "Size " + prod.size();
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, ListVarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query _($prods : [ProductInput]!){\\n ListVarService{\\n  testVariables(prods : $prods)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prods\": [" +
				"      {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    }," +
				"      {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    }," +
				"      {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    }" +
				"]\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);
		
		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);
		
		JsonNode responseNode = dataNode.get("ListVarService").get("testVariables");
		assertNotNull(responseNode);
		assertEquals("Size 3", responseNode.asText());
		
		
	}

	@Test
	public void testSingleVariables() throws InterruptedException, InvalidSyntaxException, Exception {
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		VarService testServiceImpl = new VarService() {
			
			@Override
			public String testVariables(Product prod, String test) {
				return prod.getName() + "_" + test;
			}
			
			@Override
			public String testVariable(Product prod) {
				return prod.getName();
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		
		properties.put(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER, "true");
		
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		
		registerServiceForCleanup(testServiceImpl, properties, VarService.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\r\n" + 
				"  \"query\": \"query name($prod : ProductInput!, $test : String!){\\n VarService{\\n  testVariables(prod : $prod, test : $test)\\n}\\n}\",\r\n" + 
				"  \"variables\": {\r\n" + 
				"    \"prod\": {\r\n" + 
				"      \"id\": \"test\",\r\n" + 
				"      \"name\": \"name\",\r\n" + 
				"      \"description\": \"desc\",\r\n" + 
				"      \"price\": 12,\r\n" + 
				"      \"active\": true\r\n" + 
				"    },\r\n" + 
				"    \"test\": \"bla\"\r\n" + 
				"  }\r\n" + 
				"}"), "application/json");
		ContentResponse response = post.send();
		
		assertEquals(200, response.getStatus());
		JsonNode json = parseJSON(response.getContentAsString());
		
		JsonNode errorNode = json.get("errors");
		assertNull(errorNode);

		JsonNode dataNode = json.get("data");
		assertNotNull(dataNode);

		JsonNode responseNode = dataNode.path("VarService").path("testVariables");
		assertNotNull(responseNode);
		assertEquals("name_bla", responseNode.asText());
		
	}

	// Helper method to parse JSON.
	public JsonNode parseJSON(String input) throws IOException {
		ObjectMapper mapp = new ObjectMapper();
		
		JsonNode jsonNode = mapp.reader().readTree(input);
		return jsonNode;
	}
	
	public static interface VarService{
		public String testVariables( @GraphqlArgument("prod") Product prod, @GraphqlArgument("test") String test );
		public String testVariable( @GraphqlArgument("prod") Product prod );
	}

	public static interface ListVarService{
		public String testVariables( @GraphqlArgument("prods") List<Product> prod);
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

}