package org.gecko.graphql;

import graphql.schema.GraphQLSchema;

public interface SchemaProvider {

	public GraphQLSchema schemaInstance();
}
