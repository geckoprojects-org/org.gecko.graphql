package org.gecko.graphql;

import org.osgi.service.component.annotations.Component;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

@Component(
		service = QueryInvoker.class)
public class DefaultQueryInvoker implements QueryInvoker {

	@Override
	public ExecutionResult execute(GraphQLSchema schema, ExecutionInput input) {
		final GraphQL graphQL = GraphQL.newGraphQL(schema).build();

		return graphQL.execute(input);
	}
}
