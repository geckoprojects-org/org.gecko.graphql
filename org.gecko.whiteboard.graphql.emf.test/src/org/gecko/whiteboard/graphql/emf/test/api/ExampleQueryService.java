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
import org.gecko.whiteboard.graphql.annotation.GraphqlUnionType;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;
import org.osgi.annotation.versioning.ProviderType;

import graphql.schema.DataFetchingEnvironment;

@ProviderType
public interface ExampleQueryService {

	@GraphqlDocumentation("Returns a list of all catalogs.")
	List<Catalog> getCatalogs(DataFetchingEnvironment env);

	@GraphqlDocumentation("Returns a catalog entry matching the given ID.")
	@GraphqlUnionType(value = { Product.class, CatalogEntry.class })
	CatalogEntry getEntryById(
			@GraphqlArgument("id") String id, 
			DataFetchingEnvironment env);

	@GraphqlDocumentation("Returns a list of all products matching supplied name.")
	List<Product> getProducts(
			@GraphqlArgument("name") String name, 
			DataFetchingEnvironment env);
}
