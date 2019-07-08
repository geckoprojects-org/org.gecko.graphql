package org.gecko.graphql.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.gecko.graphql.example.model.Anniversary;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;

@Component(
		service = SchemaContributor.class)
public class AnniversarySchemaContributor implements SchemaContributor {

	private static final Logger LOG = LoggerFactory.getLogger(AnniversarySchemaContributor.class);

	@Reference
	private AnniversaryService anniversaryService;

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Set<GraphQLFieldDefinition> queries = new HashSet<>();

	private final Set<DataFetcherCoordinates<?>> dataFetcherCoordinates = new HashSet<>();


	@Override
	public Set<GraphQLFieldDefinition> queries() {
		if (!initialized.get()) {
			init();
		}

		return queries;
	}


	@Override
	public Set<DataFetcherCoordinates<?>> dataFetcherCoordinates() {
		if (!initialized.get()) {
			init();
		}

		return dataFetcherCoordinates;
	}


	private synchronized void init() {
		// queries for an anniversary specified by ID
		queries.add(
				GraphQLFieldDefinition.newFieldDefinition()
						.name(Anniversary.NAME)
						.argument(
								GraphQLArgument.newArgument()
										.name(Anniversary.Fields.ID)
										.type(Scalars.GraphQLID)
										.build())
						.type(Types.ANNIVERSARY)
						.build());

		// data fetcher for an anniversary specified by ID
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Anniversary>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Anniversary.NAME),
				new DataFetcher<Anniversary>() {
					@Override
					public Anniversary get(DataFetchingEnvironment env) throws Exception {
						if (env.containsArgument(Anniversary.Fields.ID)) {
							return anniversaryService.anniversaryById(env.getArgument(Anniversary.Fields.ID));
						}

						return null;
					}
				}));

		// queries for all anniversaries
		queries.add(GraphQLFieldDefinition.newFieldDefinition()
				.name(Anniversary.NAME_COLLECTION)
				.type(GraphQLList.list(Types.ANNIVERSARY))
				.build());

		// data fetcher for a list of all anniversaries
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Anniversary>>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Anniversary.NAME_COLLECTION),
				new DataFetcher<List<Anniversary>>() {
					@Override
					public List<Anniversary> get(DataFetchingEnvironment env) throws Exception {
						return new ArrayList<>(anniversaryService.allAnniversaries());
					}
				}));

		initialized.getAndSet(true);
	}

	@Activate
	private void activate() {
		LOG.debug("{} activated", getClass().getSimpleName());
	}


	@Deactivate
	private void deactivate() {
		LOG.debug("{} deactivated", getClass().getSimpleName());
	}
}
