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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.naming.directory.DirContext;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Servlet;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.common.service.ServiceChecker;
import org.gecko.util.test.common.test.AbstractOSGiTest;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.omg.CORBA.Environment;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

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
		Dictionary<String, String> options = new Hashtable<String, String>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.waitCreate());
		
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
		serviceChecker.setModifyCount(1);
		serviceChecker.start();

		registerServiceForCleanup(testServiceImpl, properties, TestService.class);
		
		assertTrue(serviceChecker.waitModify());
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
	
	public static interface TestService{
		public String testMethodWithDataFetchingEnvironment(DataFetchingEnvironment env);

		public String testMethodWithDataFetchingEnvironmentWithParam(String input, DataFetchingEnvironment env);
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