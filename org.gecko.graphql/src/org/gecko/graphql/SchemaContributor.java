package org.gecko.graphql;

import java.util.Collections;
import java.util.Set;

import graphql.schema.GraphQLFieldDefinition;

public interface SchemaContributor {

	public static final String QUERY = "query";
	public static final String MUTATION = "mutation";
	public static final String SUBSCRIPTION = "subscription";


	public default Set<GraphQLFieldDefinition> queries() {
		return Collections.emptySet();
	}


	public default Set<GraphQLFieldDefinition> mutations() {
		return Collections.emptySet();
	}


	public default Set<GraphQLFieldDefinition> subscriptions() {
		return Collections.emptySet();
	}


	public default Set<DataFetcherCoordinates<?>> dataFetcherCoordinates() {
		return Collections.emptySet();
	}
}
