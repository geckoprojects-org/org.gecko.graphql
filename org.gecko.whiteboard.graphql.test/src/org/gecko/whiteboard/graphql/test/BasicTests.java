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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.Servlet;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.gecko.util.test.common.service.ServiceChecker;
import org.gecko.util.test.common.test.AbstractOSGiTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RunWith(MockitoJUnitRunner.class)
public class BasicTests extends AbstractOSGiTest{

	private HttpClient client;

	/**
	 * Creates a new instance.
	 */
	public BasicTests() {
		super(FrameworkUtil.getBundle(BasicTests.class).getBundleContext());
	}

	@Test
	public void testGraphQLServlet() throws IOException, InvalidSyntaxException, InterruptedException {
		Dictionary<String, String> options = new Hashtable<String, String>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.waitCreate());
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
		
		SchemaParser schemaParser = new SchemaParser();
		
//		CountDownLatch latch = new CountDownLatch(1);
//		latch.await(1, TimeUnit.SECONDS);
		
//		ContentResponse get = client.GET("http://localhost:8081/graphql/schema.json");
//		
//		assertEquals(200, get.getStatus());
		
//		TypeDefinitionRegistry schema = schemaParser.parse(get.getContentAsString());
	
	}

	/**
	 * Register a Service under two interfaces. Both Interfaces must be found as individuel query objects
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	@Test
	public void testMiltipleServiceInterfaces() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, String> options = new Hashtable<String, String>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.waitCreate());
		
		SchemaParser schemaParser = new SchemaParser();
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
		
		ContentResponse get = client.GET("http://localhost:8081/graphql/schema.json");
		
		assertEquals(200, get.getStatus());
		
//		TypeDefinitionRegistry schema = schemaParser.parse(get.getContentAsString());
		
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
	public void testMiltipleServiceInterfacesWithServiceNameProperty() throws IOException, InvalidSyntaxException, InterruptedException, ExecutionException, TimeoutException {
		Dictionary<String, String> options = new Hashtable<String, String>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.waitCreate());
		
		SchemaParser schemaParser = new SchemaParser();
		
		CountDownLatch latch = new CountDownLatch(1);
		latch.await(1, TimeUnit.SECONDS);
		
		ContentResponse get = client.GET("http://localhost:8081/graphql/schema.json");
		
		assertEquals(200, get.getStatus());
		
//		System.err.println(get.getContentAsString());
		
//		TypeDefinitionRegistry schema = schemaParser.parse(get.getContentAsString());
		
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