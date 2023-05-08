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
package org.gecko.whiteboard.graphql.emf.integration.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService;
import org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExampleIntegrationTest {
	
	@InjectBundleContext
	BundleContext bundleContext;
	
	@Order(value = -1)
	@Test
	public void testServices(
			@InjectService(cardinality = 1, 
				timeout = 5000,
				filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware, 
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder)") ServiceAware<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderAware, 
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter)") ServiceAware<GeckoGraphQLValueConverter> graphQLValueConverterAware, 
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService)") ServiceAware<ExampleQueryService> exampleQueryServiceAware, 			
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService)") ServiceAware<ExampleMutationService> exampleMutationServiceAware) {
				
		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);	
		ServiceReference<GraphqlServiceRuntime> graphqlServiceRuntimeRef = graphqlServiceRuntimeAware.getServiceReference();
		assertThat(graphqlServiceRuntimeRef).isNotNull();	
		
		assertThat(graphqlSchemaTypeBuilderAware.getServices()).hasSize(1);	
		ServiceReference<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderRef = graphqlSchemaTypeBuilderAware.getServiceReference();
		assertThat(graphqlSchemaTypeBuilderRef).isNotNull();
		
		assertThat(graphQLValueConverterAware.getServices()).hasSize(1);	
		ServiceReference<GeckoGraphQLValueConverter> graphQLValueConverterRef = graphQLValueConverterAware.getServiceReference();
		assertThat(graphQLValueConverterRef).isNotNull();
		
		assertThat(exampleQueryServiceAware.getServices()).hasSize(1);	
		ServiceReference<ExampleQueryService> exampleQueryServiceRef = exampleQueryServiceAware.getServiceReference();
		assertThat(exampleQueryServiceRef).isNotNull();		
		
		assertThat(exampleMutationServiceAware.getServices()).hasSize(1);	
		ServiceReference<ExampleMutationService> exampleMutationServiceRef = exampleMutationServiceAware.getServiceReference();
		assertThat(exampleMutationServiceRef).isNotNull();		
	}
	
	///
	/*
		@ org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService
	
			List<Catalog> getCatalogs();
		
			@GraphqlUnionType(value = { Product.class, CatalogEntry.class })
			CatalogEntry getEntryById(String id);
		
			List<Product> getProducts(String name);
	 */
	///
	/*
		@ org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService

			String halloWorld(String name);
			
			Catalog saveCatalog(Catalog catalog);
		
			Product saveProduct(Product product );
		
			String saveProducts(List<Product> products);
	 */
	///
	
	@Test
	public void testHalloWorld(
			@InjectService(cardinality = 1, 
				timeout = 5000, 
				filter = "(objectClass=org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService)") ServiceAware<ExampleMutationService> exampleMutationServiceAware) {
		
		assertThat(exampleMutationServiceAware.getServices()).hasSize(1);
		ExampleMutationService exampleMutationService = exampleMutationServiceAware.getService();
		assertThat(exampleMutationService).isNotNull();
		
		String halloWorldResponse = exampleMutationService.halloWorld("world!", null);
		assertThat(halloWorldResponse).isNotNull();
		assertThat(halloWorldResponse).isEqualTo("Hallo world!");
	}
	
	/*
	public String halloWorld(String name) {
		return "Hallo " + name;
	}
	 */
	
	/*
	@Test
	public void testSaveUser(
			@InjectService(cardinality = 1, timeout = DELAYS_INJECTION) ServiceAware<UserMutation> userMutationAware,
			@InjectService(cardinality = 1, timeout = DELAYS_INJECTION) ServiceAware<UserQuery> userQueryAware) throws Exception {
		
		assertThat(userMutationAware.getServices()).hasSize(1);
		UserMutation userMutationService = userMutationAware.getService();
		assertThat(userMutationService).isNotNull();
		
		assertThat(userQueryAware.getServices()).hasSize(1);
		UserQuery userQueryService = userQueryAware.getService();
		assertThat(userQueryService).isNotNull();		
		
		User user = ProjectApiFactory.eINSTANCE.createUser();
		user.setFirstName("Mario");
		user.setLastName("Rossi");
		assertThat(user.getId()).isNull();
		
		user = userMutationService.saveUser(user, null);
		assertThat(user).isNotNull();
		assertThat(user.getId()).isNotNull();
		assertThat(user.getId().contains("-")).isFalse();
	}
	*/	
	///

}
