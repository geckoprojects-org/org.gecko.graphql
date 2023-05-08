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
package org.gecko.whiteboard.graphql.emf.test.impl;

import java.util.LinkedList;
import java.util.List;

import org.gecko.whiteboard.graphql.annotation.GraphqlMutationService;
import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.annotation.RequireEMFGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService;
import org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestFactory;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import graphql.schema.DataFetchingEnvironment;

@Component(scope = ServiceScope.PROTOTYPE)
//@Component
@GraphqlQueryService( value = ExampleQueryService.class, name = "ExampleQuery" )
@GraphqlMutationService( value = ExampleMutationService.class, name = "ExampleMutation" )
@RequireEMFGraphQLWhiteboard
public class ExampleImpl implements ExampleQueryService, ExampleMutationService {

	private List<Catalog> catalogs = new LinkedList<>();
	
	@Activate
	public void activate() {
		Catalog catalog = GraphQLTestFactory.eINSTANCE.createCatalog();
		
		catalog.setId("Catalog1");
		catalog.setName("Catalog Number One");
		
		catalogs.add(catalog);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService#halloWorld(java.lang.String, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public String halloWorld(String name, DataFetchingEnvironment env) {
		return "Hallo " + name;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService#getCatalogs(graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public List<Catalog> getCatalogs(DataFetchingEnvironment env) {
		return catalogs;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService#getEntryById(java.lang.String, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public CatalogEntry getEntryById(String id, DataFetchingEnvironment env) {
		// TODO: implement
		Product prod = GraphQLTestFactory.eINSTANCE.createProduct();
		prod.setActive(true);
		prod.setId("test");
		prod.setName("A name");
		return prod;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService#getProducts(java.lang.String, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public List<Product> getProducts(String name, DataFetchingEnvironment env) {
		// TODO: implement
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService#saveCatalog(org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public Catalog saveCatalog(Catalog catalog, DataFetchingEnvironment env) {
		// TODO: implement
		catalog.setId("My ID");
		return catalog;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface#saveProduct(de.dim.whiteboard.graphql.emf.test.model.GraphqlTest.Product)
	 */
	@Override
	public Product saveProduct(Product product, DataFetchingEnvironment env) {
		// TODO: implement
		return product;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService#saveProducts(java.util.List, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public String saveProducts(List<Product> products, DataFetchingEnvironment env) {
		// TODO: implement
		return "List size " + products.size();
	}
}
