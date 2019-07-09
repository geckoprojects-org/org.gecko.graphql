package org.gecko.graphql.osgi.framework;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;

//@Component(
//		service = SchemaContributor.class)
public class OsgiFrameworkSchemaContributor implements SchemaContributor {

	private static final String STOP_BUNDLE_METHOD = "stopBundle";
	private static final String START_BUNDLE_METHOD = "startBundle";

	private static final String FRAMEWORK = "frameworkDTO";

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
						.name(FRAMEWORK)
						.type(FrameworkTypes.FRAMEWORK)
						.build());

		dataFetcherCoordinates.add(new DataFetcherCoordinates<FrameworkDTO>(
				FieldCoordinates.coordinates(SchemaContributor.QUERY, FRAMEWORK),
				new DataFetcher<FrameworkDTO>() {
					@Override
					public FrameworkDTO get(DataFetchingEnvironment env) throws Exception {

						return getFrameworkDTO();
					}
				}));

		/// MUTATION

		final GraphQLFieldDefinition stopBundleFD = GraphQLFieldDefinition.newFieldDefinition()
				.name(STOP_BUNDLE_METHOD)
				.description("Stops the bundle.")
				.type(Scalars.GraphQLBoolean)
				.argument(GraphQLArgument.newArgument()
						.name("id")
						.description("The identifier of the bundle to retrieve.")
						.type(GraphQLNonNull.nonNull(Scalars.GraphQLLong)))
				.argument(GraphQLArgument.newArgument()
						.name("option")
						.description(
								"STOP_TRANSIENT : 1 [0x1]\\n"
										+ "\n"
										+ "The bundle stop is transient and the persistent autostart setting of the bundle is not modified")
						.type(Scalars.GraphQLInt))
				.build();

		final GraphQLFieldDefinition startBundleFD = GraphQLFieldDefinition.newFieldDefinition()
				.name(START_BUNDLE_METHOD)
				.description("Starts this bundle.")
				.type(Scalars.GraphQLBoolean)
				.argument(GraphQLArgument.newArgument()
						.name("id")
						.description("The identifier of the bundle to retrieve.")
						.type(GraphQLNonNull.nonNull(Scalars.GraphQLLong)))
				.argument(GraphQLArgument.newArgument()
						.name("option")
						.description(
								"START_TRANSIENT : 1 [0x1]\n" +
										"\n" +
										"The bundle start operation is transient and the persistent autostart setting of the bundle is not modified.")
						.type(Scalars.GraphQLInt))
				.build();

// TODO: generate own MUTATION_ROOT
// final GraphQLObjectType createReviewForEpisodeMutation = GraphQLObjectType.newObject()
// .name("Framework")
// .field(stopBundleFD)
// .build();

		dataFetcherCoordinates.add(new DataFetcherCoordinates<Boolean>(
				FieldCoordinates.coordinates(SchemaContributor.MUTATION, STOP_BUNDLE_METHOD),
				stopBundleDataFetcher()));

		dataFetcherCoordinates.add(new DataFetcherCoordinates<Boolean>(
				FieldCoordinates.coordinates(SchemaContributor.MUTATION, START_BUNDLE_METHOD),
				startBundleDataFetcher()));

		mutations.add(stopBundleFD);
		mutations.add(startBundleFD);

		initialized.getAndSet(true);

	}


	public static DataFetcher<Boolean> stopBundleDataFetcher() {
		return new DataFetcher<Boolean>() {
			@Override
			public Boolean get(DataFetchingEnvironment environment) {

				final Long id = environment.getArgument("id");
				int option = 0;
				if (environment.containsArgument("option")) {
					option = environment.getArgument("option");
				}

				try {
					getBundleContext().getBundle(id).stop(option);
					return true;
				} catch (final BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					return false;
				}

			}
		};
	}


	public static DataFetcher<Boolean> startBundleDataFetcher() {
		return new DataFetcher<Boolean>() {
			@Override
			public Boolean get(DataFetchingEnvironment environment) {

				final Long id = environment.getArgument("id");
				int option = 0;
				if (environment.containsArgument("option")) {
					option = environment.getArgument("option");
				}

				try {
					getBundleContext().getBundle(id).start(option);
					return true;
				} catch (final BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					return false;
				}

			}
		};
	}


	@Activate
	private void activate() {
		LOG.debug("{} activated", getClass().getSimpleName());

	}


	@Deactivate
	private void deactivate() {
		LOG.debug("{} deactivated", getClass().getSimpleName());
	}


	public static FrameworkDTO getFrameworkDTO() {
		final BundleContext bundleContext = getBundleContext();

		// FrameworkBundle has id 0
		final Bundle bundle = bundleContext.getBundle(0);
		final FrameworkDTO frameworkDTO = bundle.adapt(FrameworkDTO.class);

		return frameworkDTO;
	}


	public static BundleContext getBundleContext() {
		final Bundle thisBundle = FrameworkUtil.getBundle(OsgiFrameworkSchemaContributor.class);
		final BundleContext bundleContext = thisBundle.getBundleContext();
		return bundleContext;
	}
}
