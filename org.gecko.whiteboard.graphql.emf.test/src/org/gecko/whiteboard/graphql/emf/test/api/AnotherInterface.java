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

import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Product;

/**
 * 
 * @author jalbert
 * @since 7 Nov 2018
 */
public interface AnotherInterface {

	public String halloWorld(String name);
	
	public Catalog saveCatalog(Catalog catalog);

	public Product saveProduct(Product product );

	public String saveProducts(List<Product> products);
	
}
