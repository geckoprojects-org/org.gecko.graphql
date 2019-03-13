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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.util.test.AbstractOSGiTest;
import org.gecko.util.test.ServiceChecker;
import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.test.InvokationTests.TestServiceScalarTypesFloat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;

/**
 * 
 * @author ChristophDockhorn
 * @since 06.12.2018
 */
@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlingTests extends AbstractOSGiTest{

	
	private HttpClient client;
	
	
	/**
	 * Creates a new instance.
	 */
	public ErrorHandlingTests() {
		super(FrameworkUtil.getBundle(ErrorHandlingTests.class).getBundleContext());
	}
	

	/**
	 * We need a Service that throws an exception, when a method is called. We have
	 * to make sure, that the error message will be transported as an error
	 * response. This will most likely right now.
	 * @throws InvalidSyntaxException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testExceptionHandling() 
			throws InvalidSyntaxException, InterruptedException, IOException, TimeoutException, ExecutionException {
		// ToDo: Service registrieren, an messagebody "throws exception" (irgendeine)
		// dranhängen, da bisher
		// nur null returned wird wenn irgendwas nicht klappt.

		Dictionary<String, Object> options = new Hashtable<String, Object>();
		options.put("id", "my.graphql.servlet");
		Configuration configuration = createConfigForCleanup("GeckoGraphQLWhiteboard", "?", options);
		
		ServiceChecker<Object> serviceChecker = createdCheckerTrackedForCleanUp("(id=my.graphql.servlet)");
		serviceChecker.setCreateExpectationCount(1);
		serviceChecker.setCreateTimeout(10);
		serviceChecker.start();
		
		assertTrue(serviceChecker.awaitCreation());
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TestServiceWithException exceptionService = new TestServiceWithException() {
			
			@Override
			public String testMethodWithStringAndException(String string) throws IllegalArgumentException {
				assertNotNull(string);
				latch.countDown();
				if (string != "null") {
					return string;
				} else {
					throw new IllegalArgumentException("The input argument must not be null!");
				}
			}
		};
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE, "*");
		serviceChecker.stop();
		serviceChecker.setModifyExpectationCount(1);
		serviceChecker.start();
		registerServiceForCleanup(exceptionService, properties, TestServiceWithException.class);
		
		assertTrue(serviceChecker.awaitModification());
		
		Request post = client.POST("http://localhost:8181/graphql");
		post.content(new StringContentProvider("{\n"
				+ "  \"query\": \"query {\\n  TestService{\\n    testMethodWithDataFetchingEnvironment\\n  }\\n}\"\n"
				+ "}"), "application/json");
		ContentResponse response = post.send();

		assertEquals(200, response.getStatus());
		
		
		
	}

	
	public static interface TestServiceWithException {
		public String testMethodWithStringAndException (String string) throws IllegalArgumentException;
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
