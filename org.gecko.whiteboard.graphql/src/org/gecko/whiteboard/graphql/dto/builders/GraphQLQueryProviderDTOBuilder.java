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
package org.gecko.whiteboard.graphql.dto.builders;

import java.util.ArrayList;
import java.util.List;

import org.gecko.whiteboard.graphql.dto.GraphQLFieldDefinitionDTO;
import org.gecko.whiteboard.graphql.dto.GraphQLQueryProviderDTO;

import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.schema.GraphQLFieldDefinition;

public final class GraphQLQueryProviderDTOBuilder {

	public static GraphQLQueryProviderDTO build(final GraphQLQueryProvider queryProvider) {
		final GraphQLQueryProviderDTO dto = new GraphQLQueryProviderDTO();

		dto.name = queryProvider.getClass().getName();

		final List<GraphQLFieldDefinitionDTO> fieldDefinitionDTOs = new ArrayList<>();

		for (GraphQLFieldDefinition graphQLFieldDefinition : queryProvider.getQueries()) {
			fieldDefinitionDTOs.add(GraphQLFieldDefinitionDTOBuilder.build(graphQLFieldDefinition));
		}

		dto.queries = fieldDefinitionDTOs.toArray(new GraphQLFieldDefinitionDTO[fieldDefinitionDTOs.size()]);

		return dto;
	}
}
