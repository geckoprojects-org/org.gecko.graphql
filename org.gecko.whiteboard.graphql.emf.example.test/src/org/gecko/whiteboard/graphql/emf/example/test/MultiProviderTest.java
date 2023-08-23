/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.emf.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.dto.RuntimeDTO;
import org.gecko.whiteboard.graphql.emf.example.test.providers.TestQueryProvider1;
import org.gecko.whiteboard.graphql.emf.example.test.providers.TestQueryProvider2;
import org.gecko.whiteboard.graphql.emf.example.test.providers.TestTypesProvider1;
import org.gecko.whiteboard.graphql.emf.example.test.providers.TestTypesProvider2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MultiProviderTest {
	
	@InjectBundleContext
	BundleContext bundleContext;

	@Order(value = -1)
	@Test
	public void testServices(
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder)") ServiceAware<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderAware) {

		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);
		ServiceReference<GraphqlServiceRuntime> graphqlServiceRuntimeRef = graphqlServiceRuntimeAware
				.getServiceReference();
		assertThat(graphqlServiceRuntimeRef).isNotNull();

		assertThat(graphqlSchemaTypeBuilderAware.getServices()).hasSize(1);
		ServiceReference<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderRef = graphqlSchemaTypeBuilderAware
				.getServiceReference();
		assertThat(graphqlSchemaTypeBuilderRef).isNotNull();
	}
	
	// Register 2 separate implementations of a query and type provider
	@Test
	public void testTwoSeparateInstances(@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware) throws InvalidSyntaxException {

		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);
		GraphqlServiceRuntime graphqlServiceRuntime = graphqlServiceRuntimeAware.getService();
		assertThat(graphqlServiceRuntime).isNotNull();		
		
		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();
		
		// register TestQueryProvider1
		Dictionary<String, Object> testQueryProvider1Props = new Hashtable<String, Object>();
		testQueryProvider1Props.put("component.name", "TestQueryProvider1");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLQueryProvider.class, new TestQueryProvider1(), testQueryProvider1Props));
		
		RuntimeDTO runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(1, runtimeDTO.queryProviderDTOs.length);
		assertEquals(TestQueryProvider1.class.getName(), runtimeDTO.queryProviderDTOs[0].name);
		assertTrue(Arrays.asList(runtimeDTO.queries).contains("hello"));

		// register TestQueryProvider2
		Dictionary<String, Object> testQueryProvider2Props = new Hashtable<String, Object>();
		testQueryProvider2Props.put("component.name", "TestQueryProvider2");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLQueryProvider.class, new TestQueryProvider2(), testQueryProvider2Props));
	
		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(2, runtimeDTO.queryProviderDTOs.length);
		assertEquals(TestQueryProvider2.class.getName(), runtimeDTO.queryProviderDTOs[1].name);
		assertTrue(Arrays.asList(runtimeDTO.queries).contains("hallo"));
		
		// register TestTypesProvider1
		Dictionary<String, Object> testTypesProvider1Props = new Hashtable<String, Object>();
		testTypesProvider1Props.put("component.name", "TestTypesProvider1");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLTypesProvider.class, new TestTypesProvider1(), testTypesProvider1Props));
		
		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(1, runtimeDTO.typesProviderDTOs.length);
		assertEquals(TestTypesProvider1.class.getName(), runtimeDTO.typesProviderDTOs[0].name);

		// register TestTypesProvider2
		Dictionary<String, Object> testTypesProvider2Props = new Hashtable<String, Object>();
		testTypesProvider2Props.put("component.name", "TestTypesProvider2");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLTypesProvider.class, new TestTypesProvider2(), testTypesProvider2Props));

		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(2, runtimeDTO.typesProviderDTOs.length);
		assertEquals(TestTypesProvider2.class.getName(), runtimeDTO.typesProviderDTOs[1].name);
		
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();			
		}
	}	
	
	// Register 2 instances of the same implementation of a query and type provider
	@Test
	public void testTwoInstancesOfTheSame(@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware) throws InvalidSyntaxException {
		
		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);
		GraphqlServiceRuntime graphqlServiceRuntime = graphqlServiceRuntimeAware.getService();
		assertThat(graphqlServiceRuntime).isNotNull();		
		
		List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();
		
		// register instance #1 of TestQueryProvider1
		Dictionary<String, Object> testQueryProvider1Instance1Props = new Hashtable<String, Object>();
		testQueryProvider1Instance1Props.put("component.name", "TestQueryProvider1Instance1");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLQueryProvider.class, new TestQueryProvider1(), testQueryProvider1Instance1Props));
		
		RuntimeDTO runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(1, runtimeDTO.queryProviderDTOs.length);
		assertEquals(TestQueryProvider1.class.getName(), runtimeDTO.queryProviderDTOs[0].name);
		assertTrue(Arrays.asList(runtimeDTO.queries).contains("hello"));		
		
		// register instance #2 of TestQueryProvider1
		Dictionary<String, Object> testQueryProvider1Instance2Props = new Hashtable<String, Object>();
		testQueryProvider1Instance2Props.put("component.name", "TestQueryProvider1Instance2");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLQueryProvider.class, new TestQueryProvider1(), testQueryProvider1Instance2Props));
	
		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(2, runtimeDTO.queryProviderDTOs.length);
		assertEquals(TestQueryProvider1.class.getName(), runtimeDTO.queryProviderDTOs[1].name);
		assertTrue(Arrays.asList(runtimeDTO.queries).contains("hello"));
		
		// register instance #1 of TestTypesProvider1
		Dictionary<String, Object> testTypesProvider1Instance1Props = new Hashtable<String, Object>();
		testTypesProvider1Instance1Props.put("component.name", "TestTypesProvider1Instance1");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLTypesProvider.class, new TestTypesProvider1(), testTypesProvider1Instance1Props));
		
		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(1, runtimeDTO.typesProviderDTOs.length);
		assertEquals(TestTypesProvider1.class.getName(), runtimeDTO.typesProviderDTOs[0].name);
		
		// register instance #2 of TestTypesProvider1
		Dictionary<String, Object> testTypesProvider1Instance2Props = new Hashtable<String, Object>();
		testTypesProvider1Instance2Props.put("component.name", "TestTypesProvider1Instance2");
		serviceRegistrations.add(bundleContext.registerService(graphql.kickstart.servlet.osgi.GraphQLTypesProvider.class, new TestTypesProvider1(), testTypesProvider1Instance2Props));

		runtimeDTO = graphqlServiceRuntime.getRuntimeDTO();
		assertNotNull(runtimeDTO);
		assertEquals(2, runtimeDTO.typesProviderDTOs.length);
		assertEquals(TestTypesProvider1.class.getName(), runtimeDTO.typesProviderDTOs[1].name);
		
		for (ServiceRegistration<?> serviceRegistration : serviceRegistrations) {
			serviceRegistration.unregister();			
		}
	}
}
