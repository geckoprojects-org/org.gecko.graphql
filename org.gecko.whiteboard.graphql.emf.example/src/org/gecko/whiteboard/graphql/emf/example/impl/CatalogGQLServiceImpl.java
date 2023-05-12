/**
 * Copyright (c) 2012 - 2020 Data In Motion and others.
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

import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.annotation.RequireEMFGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.emf.example.api.CatalogGQLService;
import org.gecko.whiteboard.graphql.emf.example.api.CatalogService;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * 
 * @author ilenia
 * @since Feb 21, 2020
 */
@Component(scope = ServiceScope.PROTOTYPE) // The prototype is useful here, because the Service can be called in parallel and every caller should get its exclusive instance
@GraphqlQueryService(value = CatalogGQLService.class, name = "CatalogQuery")
@RequireEMFGraphQLWhiteboard
public class CatalogGQLServiceImpl implements CatalogGQLService {
	
	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private CatalogService catalogService;

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.emf.example.api.CatalogGQLService#getCatalogById(java.lang.String)
	 */
	@Override
	public Catalog getCatalogById(String id) {		
		return catalogService.getCatalogById(id);
	}

}
