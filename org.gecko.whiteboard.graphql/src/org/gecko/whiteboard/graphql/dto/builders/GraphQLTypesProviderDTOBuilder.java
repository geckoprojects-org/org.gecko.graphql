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

import org.gecko.whiteboard.graphql.dto.GraphQLTypeDTO;
import org.gecko.whiteboard.graphql.dto.GraphQLTypesProviderDTO;

import graphql.kickstart.servlet.osgi.GraphQLTypesProvider;
import graphql.schema.GraphQLType;

public final class GraphQLTypesProviderDTOBuilder {

	public static GraphQLTypesProviderDTO build(final GraphQLTypesProvider typesProvider) {
		final GraphQLTypesProviderDTO dto = new GraphQLTypesProviderDTO();

		dto.name = typesProvider.getClass().getName();

		final List<GraphQLTypeDTO> typeDTOs = new ArrayList<>();

		for (GraphQLType graphQLType : typesProvider.getTypes()) {
			typeDTOs.add(GraphQLTypeDTOBuilder.build(graphQLType));
		}

		dto.types = typeDTOs.toArray(new GraphQLTypeDTO[typeDTOs.size()]);

		return dto;
	}
}
