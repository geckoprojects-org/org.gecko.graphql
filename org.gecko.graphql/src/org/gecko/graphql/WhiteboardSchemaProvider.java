package org.gecko.graphql;

import static org.gecko.graphql.annotation.GraphqlSchemaProviderType.WHITEBOARD_TYPE;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.gecko.graphql.annotation.GraphqlSchemaProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

@Component(
		service = SchemaProvider.class)
@GraphqlSchemaProviderType(WHITEBOARD_TYPE)
public class WhiteboardSchemaProvider implements SchemaProvider {

	private static final Logger LOG = LoggerFactory.getLogger(WhiteboardSchemaProvider.class);

	protected GraphQLSchema schema;

	private final Set<SchemaContributor> contributors = new CopyOnWriteArraySet<>();


	@Override
	public GraphQLSchema schemaInstance() {
		return schema;
	}


	protected void updateSchema() {
		GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry().build();
		GraphQLObjectType mutationType = GraphQLObjectType.newObject().name(SchemaContributor.MUTATION).build();
		GraphQLObjectType queryType = GraphQLObjectType.newObject().name(SchemaContributor.QUERY).build();
		GraphQLObjectType subscriptionType = GraphQLObjectType.newObject().name(SchemaContributor.SUBSCRIPTION).build();

		for (final SchemaContributor contributor : contributors) {
			mutationType = mutationType.transform(builder -> builder.fields(new ArrayList<>(contributor.mutations())));
			queryType = queryType.transform(builder -> builder.fields(new ArrayList<>(contributor.queries())));
			subscriptionType = subscriptionType.transform(builder -> builder.fields(new ArrayList<>(contributor.subscriptions())));

			codeRegistry = codeRegistry
					.transform(builder -> contributor.dataFetcherCoordinates()
							.forEach(dfc -> builder.dataFetcher(dfc.coordinates(), dfc.dataFetcher())));
		}

		schema = GraphQLSchema.newSchema()
				.codeRegistry(codeRegistry)
				.mutation(mutationType)
				.query(queryType)
				.subscription(subscriptionType)
				.build();
	}


	@Activate
	private void activate() {
		updateSchema();

		LOG.debug("{} activated", getClass().getSimpleName());
	}


	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC)
	protected void bindContributor(SchemaContributor contributor) {
		contributors.add(contributor);
		updateSchema();
	}


	protected void unbindContributor(SchemaContributor contributor) {
		contributors.remove(contributor);
		updateSchema();
	}


	protected void updatedContributor(SchemaContributor contributor) {
		updateSchema();
	}
}
