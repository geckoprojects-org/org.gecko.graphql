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
package org.gecko.whiteboard.graphql.emf.example.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gecko.whiteboard.graphql.annotation.GraphqlMutationService;
import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.annotation.RequireEMFGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Currency;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.GraphQLTestFactory;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Product;
import org.gecko.whiteboard.graphql.emf.example.service.api.ExampleMutationService;
import org.gecko.whiteboard.graphql.emf.example.service.api.ExampleQueryService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
@GraphqlQueryService(value = ExampleQueryService.class, name = "ExampleQuery")
@GraphqlMutationService(value = ExampleMutationService.class, name = "ExampleMutation")
@RequireEMFGraphQLWhiteboard
public class ExampleImpl implements ExampleQueryService, ExampleMutationService {

	private List<Catalog> catalogs = new LinkedList<>();

	private Map<String, Product> productsMap = new HashMap<>();

	@Activate
	public void activate() {
		Catalog catalog = GraphQLTestFactory.eINSTANCE.createCatalog();
		catalog.setId("Catalog1");
		catalog.setName("Catalog number one");
		catalogs.add(catalog);

		Product product = GraphQLTestFactory.eINSTANCE.createProduct();
		product.setId("Product1");
		product.setName("Product number one");
		product.setActive(true);
		product.setCurrency(Currency.EUR);
		product.setPrice(9.99);
		product.setCatalog(catalog);
		catalog.getEntries().add(product);
		productsMap.put(product.getId(), product);
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
		if (productsMap.containsKey(id)) {
			return productsMap.get(id);
		} else {
			return null;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleQueryService#getProducts(java.lang.String, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public List<Product> getProducts(String name, DataFetchingEnvironment env) {
		return productsMap.values().stream().toList();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService#saveCatalog(org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public Catalog saveCatalog(Catalog catalog, DataFetchingEnvironment env) {
		if (catalog.getId() == null) {
			catalog.setId(UUID.randomUUID().toString());
		}

		catalogs.add(catalog);

		return catalog;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.AnotherInterface#saveProduct(de.dim.whiteboard.graphql.emf.test.model.GraphqlTest.Product)
	 */
	@Override
	public Product saveProduct(Product product, DataFetchingEnvironment env) {
		if (product.getId() == null) {
			product.setId(UUID.randomUUID().toString());
		}

		productsMap.put(product.getId(), product);

		return product;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.test.api.ExampleMutationService#saveProducts(java.util.List, graphql.schema.DataFetchingEnvironment)
	 */
	@Override
	public String saveProducts(List<Product> products, DataFetchingEnvironment env) {
		int savedProductsCount = 0;

		for (Product product : products) {
			if (product.getId() == null) {
				product.setId(UUID.randomUUID().toString());
			}
			productsMap.put(product.getId(), product);

			savedProductsCount++;
		}

		return "Saved " + savedProductsCount + " product(s)";
	}
}
