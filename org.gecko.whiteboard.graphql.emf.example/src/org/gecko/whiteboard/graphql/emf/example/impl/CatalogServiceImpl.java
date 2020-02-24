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
package org.gecko.whiteboard.graphql.emf.example.impl;

import org.osgi.service.component.annotations.*;

import org.gecko.whiteboard.graphql.emf.example.api.CatalogService;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.Catalog;
import org.gecko.whiteboard.graphql.emf.test.model.GraphqlTest.GraphQLTestFactory;

@Component(scope = ServiceScope.PROTOTYPE)
public class CatalogServiceImpl implements CatalogService{

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.example.api.CatalogService#getCatalogById(java.lang.String)
	 */
	@Override
	public Catalog getCatalogById(String id) {
		Catalog catalog = GraphQLTestFactory.eINSTANCE.createCatalog();
		catalog.setId(id);
		catalog.setName("Test Catalog - "+id);
		return catalog;
	}


}
