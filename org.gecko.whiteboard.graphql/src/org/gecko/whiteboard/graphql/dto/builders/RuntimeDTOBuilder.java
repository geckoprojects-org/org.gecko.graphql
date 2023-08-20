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

import org.gecko.whiteboard.graphql.dto.GraphQLQueryProviderDTO;
import org.gecko.whiteboard.graphql.dto.GraphQLTypesProviderDTO;
import org.gecko.whiteboard.graphql.dto.RuntimeDTO;
import org.gecko.whiteboard.graphql.schema.GeckoGraphQLSchemaBuilder;
import org.osgi.framework.dto.ServiceReferenceDTO;

import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.kickstart.servlet.osgi.GraphQLTypesProvider;

public final class RuntimeDTOBuilder {
	private final ServiceReferenceDTO serviceRefDTO;
	private final GeckoGraphQLSchemaBuilder schemaBuilder;
	public final String[] queries;
	public final String[] mutations;	

    public RuntimeDTOBuilder(final ServiceReferenceDTO srDTO, final GeckoGraphQLSchemaBuilder schemaBuilder, final String[] queries, final String[] mutations) {
        this.serviceRefDTO = srDTO;
        this.schemaBuilder = schemaBuilder;
        this.queries = queries;
        this.mutations = mutations;
    }

    public RuntimeDTO build() {
        final RuntimeDTO runtimeDTO = new RuntimeDTO();
        runtimeDTO.serviceDTO = this.serviceRefDTO;
        runtimeDTO.queries = this.queries;
        runtimeDTO.mutations = this.mutations;

        runtimeDTO.queryProviderDTOs = createGraphQLQueryProviderDTOs();
        runtimeDTO.typesProviderDTOs = createGraphQLTypesProviderDTOs();
        
        // TODO: create DTOs for 'GraphQLMutationProvider', 'GraphQLSubscriptionProvider', etc.  and fill them - as needed
        
        return runtimeDTO;
    }
    
    private GraphQLQueryProviderDTO[] createGraphQLQueryProviderDTOs() {
    	final List<GraphQLQueryProviderDTO> queryProviderDTOs = new ArrayList<>();
    	
    	for (GraphQLQueryProvider graphQLQueryProvider : this.schemaBuilder.getQueryProviders()) {
    		queryProviderDTOs.add(GraphQLQueryProviderDTOBuilder.build(graphQLQueryProvider));
    	}
    	
    	return queryProviderDTOs.toArray(new GraphQLQueryProviderDTO[queryProviderDTOs.size()]);
    }
    
    private GraphQLTypesProviderDTO[] createGraphQLTypesProviderDTOs() {
    	final List<GraphQLTypesProviderDTO> typesProviderDTOs = new ArrayList<>();
    	
    	for (GraphQLTypesProvider graphQLTypesProvider : this.schemaBuilder.getTypesProviders()) {
    		typesProviderDTOs.add(GraphQLTypesProviderDTOBuilder.build(graphQLTypesProvider));
    	}
    	
    	return typesProviderDTOs.toArray(new GraphQLTypesProviderDTO[typesProviderDTOs.size()]);
    }
}
