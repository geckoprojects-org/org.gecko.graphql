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

import org.osgi.annotation.versioning.ProviderType;
import org.gecko.whiteboard.graphql.annotation.GraphqlUnionType;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.CatalogEntry;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;

@ProviderType
public interface TestService{

	public List<Catalog> getCatalogs();
	
	@GraphqlUnionType(value = {
		Product.class,
		CatalogEntry.class
	})
	public CatalogEntry getEntryById(String id);
	
	public List<Product> getProducts(String name);
	
	
}
