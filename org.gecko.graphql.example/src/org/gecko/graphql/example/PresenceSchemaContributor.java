package org.gecko.graphql.example;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Presence;
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
import graphql.schema.GraphQLNonNull;

@Component(
		service = SchemaContributor.class)
public class PresenceSchemaContributor implements SchemaContributor {

	private static final Logger LOG = LoggerFactory.getLogger(PresenceSchemaContributor.class);

	@Reference
	private PresenceService presenceService;

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Set<GraphQLFieldDefinition> queries = new HashSet<>();
	private final Set<GraphQLFieldDefinition> mutations = new HashSet<>();
	private final Set<GraphQLFieldDefinition> subscriptions = new HashSet<>();

	private final Set<DataFetcherCoordinates<?>> dataFetcherCoordinates = new HashSet<>();


	@Override
	public Set<GraphQLFieldDefinition> queries() {
		if (!initialized.get()) {
			init();
		}

		return queries;
	}


	@Override
	public Set<GraphQLFieldDefinition> mutations() {
		if (!initialized.get()) {
			init();
		}

		return mutations;
	}


	@Override
	public Set<GraphQLFieldDefinition> subscriptions() {
		if (!initialized.get()) {
			init();
		}

		return subscriptions;
	}


	@Override
	public Set<DataFetcherCoordinates<?>> dataFetcherCoordinates() {
		if (!initialized.get()) {
			init();
		}

		return dataFetcherCoordinates;
	}


	private synchronized void init() {
		// query for the presence of a person specified by ID argument
		queries.add(
				GraphQLFieldDefinition.newFieldDefinition()
						.name(Presence.NAME)
						.argument(
								GraphQLArgument.newArgument()
										.name(Person.Fields.ID)
										.type(Scalars.GraphQLLong)
										.build())
						.type(Types.PRESENCE)
						.build());

		// data fetcher for the presence of a person specified by ID argument
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Presence>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Presence.NAME),
				new DataFetcher<Presence>() {
					@Override
					public Presence get(DataFetchingEnvironment env) throws Exception {
						if (env.containsArgument(Person.Fields.ID)) {
							return presenceService.presenceById(env.getArgument(Person.Fields.ID));
						}

						return null;
					}
				}));

		// mutation for the presence of a person with specified ID
		mutations.add(
				GraphQLFieldDefinition.newFieldDefinition()
						.name(Presence.NAME)
						.argument(
								GraphQLArgument.newArgument()
										.name(Person.Fields.ID)
										.type(GraphQLNonNull.nonNull(Scalars.GraphQLLong))
										.build())
						.argument(
								GraphQLArgument.newArgument()
										.name(Presence.NAME)
										.type(GraphQLNonNull.nonNull(Types.PRESENCE_INPUT))
										.build())
						.type(Types.PRESENCE)
						.build());

		// data fetcher for the mutation of the presence of a person with specified ID
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Presence>(
				FieldCoordinates.coordinates(SchemaContributor.MUTATION, Presence.NAME),
				new DataFetcher<Presence>() {
					@Override
					public Presence get(DataFetchingEnvironment env) throws Exception {
						final Person person = Person.builder().id(env.getArgument(Person.Fields.ID)).build();
						final Map<String, Object> presenceInput = env.getArgument(Presence.NAME);
						// FIXME PresenceType enum funktioniert noch nicht (Typ?)
						presenceService.updatePresence(person, Presence.fromMap(presenceInput));

						return presenceService.personPresence(person).orElse(null);
					}
				}));

		// TODO subscription for the presence of a person with specified ID

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
