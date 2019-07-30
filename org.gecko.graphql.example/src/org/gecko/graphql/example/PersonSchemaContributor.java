package org.gecko.graphql.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.gecko.graphql.example.model.Anniversary;
import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.PersonStatus;
import org.gecko.graphql.example.model.Phone;
import org.gecko.graphql.example.model.Presence;
import org.gecko.graphql.example.model.Realm;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
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
public class PersonSchemaContributor implements SchemaContributor {

	private static final Logger LOG = LoggerFactory.getLogger(PersonSchemaContributor.class);

	@Reference
	private PersonService personService;

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC)
	private volatile AnniversaryService anniversaryService;

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC)
	private volatile ContactService contactService;

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC)
	private volatile PhoneService phoneService;

	@Reference(
			cardinality = ReferenceCardinality.OPTIONAL,
			policy = ReferencePolicy.DYNAMIC)
	private volatile PresenceService presenceService;

	@Reference
	private PersonStatusService statusService;

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Set<GraphQLFieldDefinition> queries = new HashSet<>();

	private final Set<DataFetcherCoordinates<?>> dataFetcherCoordinates = new HashSet<>();

	// XXX This would usually be determined externally or configured. Can be changed here on the fly to demonstrate the difference.
	private final Realm realm = null;

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
		// queries for a person with specified ID or user name
		queries.add(
				GraphQLFieldDefinition.newFieldDefinition()
						.name(Person.NAME)
						.argument(
								GraphQLArgument.newArgument()
										.name(Person.Fields.ID)
										.type(Scalars.GraphQLLong)
										.build())
						.argument(
								GraphQLArgument.newArgument()
										.name(Person.Fields.USER_NAME)
										.type(Scalars.GraphQLString)
										.build())
						.type(Types.PERSON)
						.build());

		// queries for all persons
		queries.add(GraphQLFieldDefinition.newFieldDefinition()
				.name(Person.NAME_COLLECTION)
				.type(GraphQLList.list(Types.PERSON))
				.build());

		// data fetcher for a person specified by either ID or user name argument
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Person>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Person.NAME),
				new DataFetcher<Person>() {
					@Override
					public Person get(DataFetchingEnvironment env) throws Exception {
						if (env.containsArgument(Person.Fields.ID)) {
							return personService.personById(env.getArgument(Person.Fields.ID));
						}

						if (env.containsArgument(Person.Fields.USER_NAME)) {
							return personService.personByUserName(env.getArgument(Person.Fields.USER_NAME)).orElse(null);
						}

						return null;
					}
				}));

		// data fetcher for a list of all persons
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Person>>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, Person.NAME_COLLECTION),
				new DataFetcher<List<Person>>() {
					@Override
					public List<Person> get(DataFetchingEnvironment env) throws Exception {
						return new ArrayList<>(personService.allPersons());
					}
				}));

		// data fetcher for a person's presence field using the dynamic PresenceService
		dataFetcherCoordinates.add(new DataFetcherCoordinates<Presence>(
				FieldCoordinates.coordinates(Types.PERSON.getName(), Person.Fields.PRESENCE),
				new DataFetcher<Presence>() {
					@Override
					public Presence get(DataFetchingEnvironment env) throws Exception {
						final PresenceService ps = presenceService;

						if (ps == null) {
							return null;
						}

						return ps.personPresence(env.getSource()).orElse(null);
					}
				}));

		// data fetcher for a person's status field using the static PersonStatusService
		dataFetcherCoordinates.add(new DataFetcherCoordinates<PersonStatus>(
				FieldCoordinates.coordinates(Types.PERSON.getName(), Person.Fields.STATUS),
				new DataFetcher<PersonStatus>() {
					@Override
					public PersonStatus get(DataFetchingEnvironment env) throws Exception {
						return statusService.personStatus(env.getSource(), LocalDate.now()).orElse(null);
					}
				}));

		// data fetcher for a person's anniversaries field using the dynamic AnniversaryService
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Anniversary>>(
				FieldCoordinates.coordinates(Types.PERSON.getName(), Person.Fields.ANNIVERSARIES),
				new DataFetcher<List<Anniversary>>() {
					@Override
					public List<Anniversary> get(DataFetchingEnvironment env) throws Exception {
						final AnniversaryService as = anniversaryService;

						if (as == null) {
							return null;
						}

						return new ArrayList<>(as.personAnniversaries(env.getSource(), realm));
					}
				}));

		// data fetcher for a person's contacts field using the dynamic ContactService
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Contact>>(
				FieldCoordinates.coordinates(Types.PERSON.getName(), Person.Fields.CONTACTS),
				new DataFetcher<List<Contact>>() {
					@Override
					public List<Contact> get(DataFetchingEnvironment env) throws Exception {
						final ContactService cs = contactService;

						if (cs == null) {
							return null;
						}

						return new ArrayList<>(cs.personContacts(env.getSource(), realm));
					}
				}));

		// data fetcher for a contact's phones field using the dynamic PhoneService
		dataFetcherCoordinates.add(new DataFetcherCoordinates<List<Phone>>(
				FieldCoordinates.coordinates(Types.CONTACT.getName(), Contact.Fields.PHONES),
				new DataFetcher<List<Phone>>() {
					@Override
					public List<Phone> get(DataFetchingEnvironment env) throws Exception {
						final PhoneService ps = phoneService;

						if (ps == null) {
							return null;
						}

						return new ArrayList<>(ps.contactPhones(env.getSource()));
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
