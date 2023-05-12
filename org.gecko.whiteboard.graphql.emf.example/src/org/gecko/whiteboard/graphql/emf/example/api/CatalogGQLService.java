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
package org.gecko.whiteboard.graphql.emf.example.api;

import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.annotation.GraphqlDocumentation;
import org.gecko.whiteboard.graphql.emf.example.model.GraphqlTest.Catalog;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface CatalogGQLService{

	@GraphqlDocumentation("Here you can put the method description and this will be displayed in GraphQL")
	public Catalog getCatalogById(
			@GraphqlDocumentation("This is the first argument")
			@GraphqlArgument(value = "catalogId", optional = false) 
			String id);

}
