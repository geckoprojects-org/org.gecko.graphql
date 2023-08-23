/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.emf.example.test.providers;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.schema.GraphQLFieldDefinition;

public class TestQueryProvider1 implements GraphQLQueryProvider {

	/* 
	 * (non-Javadoc)
	 * @see graphql.kickstart.servlet.osgi.GraphQLQueryProvider#getQueries()
	 */
	@Override
	public Collection<GraphQLFieldDefinition> getQueries() {
		List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<GraphQLFieldDefinition>();
		// @formatter:off
	    fieldDefinitions.add(newFieldDefinition()
	        .type(GraphQLString)
	        .name("hello")
	        .description(
	            "Basic example of a GraphQLQueryProvider")
	        .staticValue("world")
	        .build());
	    // @formatter:on
		return fieldDefinitions;
	}		
}