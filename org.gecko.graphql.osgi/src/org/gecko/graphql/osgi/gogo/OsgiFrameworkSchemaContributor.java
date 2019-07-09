package org.gecko.graphql.osgi.gogo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLFieldDefinition;

@Component(
		service = SchemaContributor.class)
public class OsgiFrameworkSchemaContributor implements SchemaContributor {

	private static final Logger LOG = LoggerFactory.getLogger(OsgiFrameworkSchemaContributor.class);

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Set<GraphQLFieldDefinition> queries = new HashSet<>();
	private final Set<GraphQLFieldDefinition> mutations = new HashSet<>();

	private final Set<DataFetcherCoordinates<?>> dataFetcherCoordinates = new HashSet<>();


	@Override
	public Set<GraphQLFieldDefinition> queries() {
		if (!initialized.get()) {
			init();
		}

		// TODO: List gogo Commands
		return queries;
	}


	@Override
	public Set<GraphQLFieldDefinition> mutations() {
		if (!initialized.get()) {
			init();
		}
		// TODO: Execute gogo Commands
		return mutations;
	}


	@Override
	public Set<DataFetcherCoordinates<?>> dataFetcherCoordinates() {
		if (!initialized.get()) {
			init();
		}

		return dataFetcherCoordinates;
	}


	private synchronized void init() {

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
