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
import org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface;
import org.gecko.whiteboard.graphql.emf.test.api.TestService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestFactory;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;

@Component
@GraphqlQueryService(TestService.class)
@GraphqlMutationService(AnotherInterface.class)
@RequireEMFGraphQLWhiteboard
public class ExampleImpl implements TestService, AnotherInterface{

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
	 * @see org.gecko.whiteboard.graphql.test.service.api.AnotherInterface#halloWorld(java.lang.String)
	 */
	@Override
	public String halloWorld(String name) {
		return "Hallo " + name;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.TestService#getCatalogs()
	 */
	@Override
	public List<Catalog> getCatalogs() {
		return catalogs;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.TestService#getEntryById(java.lang.String)
	 */
	@Override
	public CatalogEntry getEntryById(String id) {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.TestService#getProducts(java.lang.String)
	 */
	@Override
	public List<Product> getProducts(String name) {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface#saveCatalog(de.dim.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog)
	 */
	@Override
	public Catalog saveCatalog(Catalog catalog) {
		catalog.setId("My ID");
		return catalog;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface#saveProduct(de.dim.whiteboard.graphql.emf.test.model.GraphqlTest.Product)
	 */
	@Override
	public Product saveProduct(Product catalog) {
		return catalog;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface#saveProduct(java.util.List)
	 */
	@Override
	public String saveProducts(List<Product> products) {
		return "List size " + products.size();
	}
}
