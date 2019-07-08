package org.gecko.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;

public interface QueryInvoker {

	public ExecutionResult execute(GraphQLSchema schema, ExecutionInput input);
}
