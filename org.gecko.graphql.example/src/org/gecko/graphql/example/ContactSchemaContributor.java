package org.gecko.graphql.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.gecko.graphql.example.model.Contact;
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
public class ContactSchemaContributor implements SchemaContributor {

	private static final Logger LOG = LoggerFactory.getLogger(ContactSchemaContributor.class);

	@Reference
	private ContactService contactService;

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
		// queries for a contact specified by ID
		queries.add(
				GraphQLFieldDefinition.newFieldDefinition()
						.name(Contact.NAME)
						.argument(
								GraphQLArgument.newArgument()
										.name(Contact.Fields.ID)
										.type(Scalars.GraphQLID)
										.build())
						.type(Types.CONTACT)
						.build());

		// data fetcher for a contact specified by ID
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Contact>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Contact.NAME),
				new DataFetcher<Contact>() {
					@Override
					public Contact get(DataFetchingEnvironment env) throws Exception {
						if (env.containsArgument(Contact.Fields.ID)) {
							return contactService.contactById(env.getArgument(Contact.Fields.ID));
						}

						return null;
					}
				}));

		// queries for all contacts
		queries.add(GraphQLFieldDefinition.newFieldDefinition()
				.name(Contact.NAME_COLLECTION)
				.type(GraphQLList.list(Types.CONTACT))
				.build());

		// data fetcher for a list of all contacts
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Contact>>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Contact.NAME_COLLECTION),
				new DataFetcher<List<Contact>>() {
					@Override
					public List<Contact> get(DataFetchingEnvironment env) throws Exception {
						return new ArrayList<>(contactService.allContacts());
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
