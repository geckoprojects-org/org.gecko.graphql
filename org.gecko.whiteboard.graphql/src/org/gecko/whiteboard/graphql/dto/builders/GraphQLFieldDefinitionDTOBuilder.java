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

import org.gecko.whiteboard.graphql.dto.GraphQLFieldDefinitionDTO;

import graphql.schema.GraphQLFieldDefinition;

public final class GraphQLFieldDefinitionDTOBuilder {

	public static GraphQLFieldDefinitionDTO build(final GraphQLFieldDefinition fieldDefinition) {
		final GraphQLFieldDefinitionDTO dto = new GraphQLFieldDefinitionDTO();

		dto.name = fieldDefinition.getName();
		dto.description = fieldDefinition.getDescription();

		// TODO: create DTOs for and fill other fields - as needed

		return dto;
	}
}
