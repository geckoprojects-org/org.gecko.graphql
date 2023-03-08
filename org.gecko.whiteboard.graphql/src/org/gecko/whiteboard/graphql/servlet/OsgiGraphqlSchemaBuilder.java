/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.servlet;

import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import graphql.Scalars;
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLQueryInvoker;
import graphql.kickstart.execution.config.DefaultExecutionStrategyProvider;
import graphql.kickstart.execution.config.ExecutionStrategyProvider;
import graphql.kickstart.execution.config.InstrumentationProvider;
import graphql.kickstart.execution.error.DefaultGraphQLErrorHandler;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import graphql.kickstart.execution.instrumentation.NoOpInstrumentationProvider;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.config.DefaultGraphQLSchemaServletProvider;
import graphql.kickstart.servlet.config.GraphQLSchemaServletProvider;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContextBuilder;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import graphql.kickstart.servlet.core.DefaultGraphQLRootObjectBuilder;
import graphql.kickstart.servlet.core.GraphQLServletListener;
import graphql.kickstart.servlet.core.GraphQLServletRootObjectBuilder;
import graphql.kickstart.servlet.input.GraphQLInvocationInputFactory;
import graphql.kickstart.servlet.osgi.GraphQLCodeRegistryProvider;
import graphql.kickstart.servlet.osgi.GraphQLFieldProvider;
import graphql.kickstart.servlet.osgi.GraphQLMutationProvider;
import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.kickstart.servlet.osgi.GraphQLSubscriptionProvider;
import graphql.kickstart.servlet.osgi.GraphQLTypesProvider;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;

/**
 * Based on {@link graphql.kickstart.servlet.OsgiSchemaBuilder}
 */
// TODO: merge with org.gecko.whiteboard.graphql.service.ServiceSchemaBuilder ? 
class OsgiGraphqlSchemaBuilder {
	private final List<GraphQLQueryProvider> queryProviders = new ArrayList<>();
	private final List<GraphQLMutationProvider> mutationProviders = new ArrayList<>();
	private final List<GraphQLSubscriptionProvider> subscriptionProviders = new ArrayList<>();
	private final List<GraphQLTypesProvider> typesProviders = new ArrayList<>();
	private final List<GraphQLServletListener> listeners = new ArrayList<>();

	private GraphQLServletContextBuilder contextBuilder = new DefaultGraphQLServletContextBuilder();
	private GraphQLServletRootObjectBuilder rootObjectBuilder = new DefaultGraphQLRootObjectBuilder();
	private ExecutionStrategyProvider executionStrategyProvider = new DefaultExecutionStrategyProvider();
	private InstrumentationProvider instrumentationProvider = new NoOpInstrumentationProvider();
	private GraphQLErrorHandler errorHandler = new DefaultGraphQLErrorHandler();
	private PreparsedDocumentProvider preparsedDocumentProvider = NoOpPreparsedDocumentProvider.INSTANCE;
	private GraphQLCodeRegistryProvider codeRegistryProvider = () -> GraphQLCodeRegistry.newCodeRegistry().build();

	private GraphQLSchemaServletProvider schemaProvider;

	private ScheduledExecutorService executor;
	private ScheduledFuture<?> updateFuture;
	private int schemaUpdateDelay;

	void activate(int schemaUpdateDelay) {
		this.schemaUpdateDelay = schemaUpdateDelay;
		if (schemaUpdateDelay != 0) {
			executor = Executors.newSingleThreadScheduledExecutor();
		}
	}

	void deactivate() {
		if (executor != null) {
			executor.shutdown();
		}
	}

	void updateSchema() {
		if (schemaUpdateDelay == 0) {
			doUpdateSchema();
		} else {
			if (updateFuture != null) {
				updateFuture.cancel(true);
			}

			updateFuture = executor.schedule(this::doUpdateSchema, schemaUpdateDelay, TimeUnit.MILLISECONDS);
		}
	}

	private void doUpdateSchema() {
		this.schemaProvider = new DefaultGraphQLSchemaServletProvider(
				newSchema().query(buildQueryType()).mutation(buildMutationType()).subscription(buildSubscriptionType())
						.additionalTypes(buildTypes()).codeRegistry(codeRegistryProvider.getCodeRegistry()).build());
	}

	GraphQLObjectType.Builder getQueryTypeBuilder() {
		final GraphQLObjectType.Builder queryTypeBuilder = newObject().name("Query").description("Root query type");

		if (!queryProviders.isEmpty()) {
			for (GraphQLQueryProvider provider : queryProviders) {
				if (provider.getQueries() != null && !provider.getQueries().isEmpty()) {
					provider.getQueries().forEach(queryTypeBuilder::field);
				}
			}
		} else {
			// graphql-java enforces Query type to be there with at least some field.
			queryTypeBuilder.field(
					GraphQLFieldDefinition.newFieldDefinition().name("_empty").type(Scalars.GraphQLBoolean).build());
		}
		return queryTypeBuilder;
	}

	private GraphQLObjectType buildQueryType() {
		return getQueryTypeBuilder().build();
	}

	Set<GraphQLType> buildTypes() {
		return typesProviders.stream().map(GraphQLTypesProvider::getTypes).flatMap(Collection::stream).collect(toSet());
	}

	GraphQLObjectType.Builder getMutationTypeBuilder() {
		return getObjectTypeBuilder("Mutation", new ArrayList<>(mutationProviders));
	}

	private GraphQLObjectType buildMutationType() {
		final GraphQLObjectType.Builder mutationTypeBuilder = getMutationTypeBuilder();
		if (mutationTypeBuilder != null) {
			return mutationTypeBuilder.build();
		}

		return null;
	}

	private GraphQLObjectType buildSubscriptionType() {
		return buildObjectType("Subscription", new ArrayList<>(subscriptionProviders));
	}

	private GraphQLObjectType.Builder getObjectTypeBuilder(String name, List<GraphQLFieldProvider> providers) {
		if (!providers.isEmpty()) {
			final GraphQLObjectType.Builder typeBuilder = newObject().name(name)
					.description("Root " + name.toLowerCase() + " type");

			for (GraphQLFieldProvider provider : providers) {
				provider.getFields().forEach(typeBuilder::field);
			}

			if (!typeBuilder.build().getFieldDefinitions().isEmpty()) {
				return typeBuilder;
			}
		}

		return null;
	}

	private GraphQLObjectType buildObjectType(String name, List<GraphQLFieldProvider> providers) {
		final GraphQLObjectType.Builder typeBuilder = getObjectTypeBuilder(name, providers);
		if (typeBuilder != null) {
			return typeBuilder.build();
		}

		return null;
	}

	void add(GraphQLQueryProvider provider) {
		queryProviders.add(provider);
	}

	void add(GraphQLMutationProvider provider) {
		mutationProviders.add(provider);
	}

	void add(GraphQLSubscriptionProvider provider) {
		subscriptionProviders.add(provider);
	}

	void add(GraphQLTypesProvider provider) {
		typesProviders.add(provider);
	}

	void remove(GraphQLQueryProvider provider) {
		queryProviders.remove(provider);
	}

	void remove(GraphQLMutationProvider provider) {
		mutationProviders.remove(provider);
	}

	void remove(GraphQLSubscriptionProvider provider) {
		subscriptionProviders.remove(provider);
	}

	void remove(GraphQLTypesProvider provider) {
		typesProviders.remove(provider);
	}

	GraphQLSchemaServletProvider getSchemaProvider() {
		return schemaProvider;
	}

	GraphQLConfiguration buildConfiguration() {
		return GraphQLConfiguration.with(buildInvocationInputFactory()).with(buildQueryInvoker())
				.with(buildObjectMapper()).with(listeners).build();
	}

	private GraphQLInvocationInputFactory buildInvocationInputFactory() {
		return GraphQLInvocationInputFactory.newBuilder(this::getSchemaProvider)
				.withGraphQLContextBuilder(contextBuilder).withGraphQLRootObjectBuilder(rootObjectBuilder).build();
	}

	private GraphQLQueryInvoker buildQueryInvoker() {
		return GraphQLQueryInvoker.newBuilder().withPreparsedDocumentProvider(preparsedDocumentProvider)
				.withInstrumentation(() -> instrumentationProvider.getInstrumentation())
				.withExecutionStrategyProvider(executionStrategyProvider).build();
	}

	private GraphQLObjectMapper buildObjectMapper() {
		return GraphQLObjectMapper.newBuilder().withGraphQLErrorHandler(errorHandler).build();
	}

	void add(GraphQLServletListener listener) {
		listeners.add(listener);
	}

	void remove(GraphQLServletListener listener) {
		listeners.remove(listener);
	}

	void setCodeRegistryProvider(GraphQLCodeRegistryProvider provider) {
		codeRegistryProvider = provider;
	}

	void setContextBuilder(GraphQLServletContextBuilder builder) {
		contextBuilder = builder;
	}

	void setRootObjectBuilder(GraphQLServletRootObjectBuilder builder) {
		rootObjectBuilder = builder;
	}

	void setExecutionStrategyProvider(ExecutionStrategyProvider provider) {
		executionStrategyProvider = provider;
	}

	void setInstrumentationProvider(InstrumentationProvider provider) {
		instrumentationProvider = provider;
	}

	void setErrorHandler(GraphQLErrorHandler handler) {
		errorHandler = handler;
	}

	void setPreparsedDocumentProvider(PreparsedDocumentProvider provider) {
		preparsedDocumentProvider = provider;
	}
}
