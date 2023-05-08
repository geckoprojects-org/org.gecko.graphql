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
package org.gecko.whiteboard.graphql.emf.test.api;

import java.util.List;

import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.annotation.GraphqlDocumentation;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;

import graphql.schema.DataFetchingEnvironment;

/**
 * 
 * @author jalbert
 * @since 7 Nov 2018
 */
public interface ExampleMutationService {

	String halloWorld(
			@GraphqlArgument("name") String name, 
			DataFetchingEnvironment env);
	
	Catalog saveCatalog(
			@GraphqlArgument("catalog") Catalog catalog, 
			DataFetchingEnvironment env);
	
	@GraphqlDocumentation("Saves given product.")
	Product saveProduct(
			@GraphqlArgument("product") Product product, 
			DataFetchingEnvironment env);

	String saveProducts(
			@GraphqlArgument("products") List<Product> products, 
			DataFetchingEnvironment env);
}
