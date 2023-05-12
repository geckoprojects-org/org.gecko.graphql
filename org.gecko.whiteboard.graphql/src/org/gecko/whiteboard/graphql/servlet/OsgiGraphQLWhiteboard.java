/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     All Contributers of https://github.com/graphql-java-kickstart/graphql-java-servlet
 *     Data In Motion - initial implementation of the GraphQL Whiteboard
 */
package org.gecko.whiteboard.graphql.servlet;

import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.DEFAULT_SERVLET_PATTERN;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.OSGI_GRAPHQL_CAPABILITY_NAME;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.dto.RuntimeDTO;
import org.gecko.whiteboard.graphql.instrumentation.TracingInstrumentationProvider;
import org.gecko.whiteboard.graphql.schema.GeckoGraphQLSchemaBuilder;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import graphql.AssertException;
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.kickstart.execution.GraphQLRootObjectBuilder;
import graphql.kickstart.execution.config.DefaultExecutionStrategyProvider;
import graphql.kickstart.execution.config.ExecutionStrategyProvider;
import graphql.kickstart.execution.config.InstrumentationProvider;
import graphql.kickstart.execution.error.DefaultGraphQLErrorHandler;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import graphql.kickstart.execution.instrumentation.NoOpInstrumentationProvider;
import graphql.kickstart.servlet.AbstractGraphQLHttpServlet;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.OsgiGraphQLHttpServlet;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContextBuilder;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import graphql.kickstart.servlet.core.DefaultGraphQLRootObjectBuilder;
import graphql.kickstart.servlet.core.GraphQLServletListener;
import graphql.kickstart.servlet.core.GraphQLServletRootObjectBuilder;
import graphql.kickstart.servlet.osgi.GraphQLCodeRegistryProvider;
import graphql.kickstart.servlet.osgi.GraphQLMutationProvider;
import graphql.kickstart.servlet.osgi.GraphQLProvider;
import graphql.kickstart.servlet.osgi.GraphQLQueryProvider;
import graphql.kickstart.servlet.osgi.GraphQLSubscriptionProvider;
import graphql.kickstart.servlet.osgi.GraphQLTypesProvider;
import graphql.schema.GraphQLCodeRegistry;

/**
 * This GraphQL Whiteboard originally is a fork of the {@link OsgiGraphQLHttpServlet}.
 * 
 * @author Juergen Albert
 * @since 19 Dec 2018
 */
@Component(name = GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME, service = { jakarta.servlet.http.HttpServlet.class,
		jakarta.servlet.Servlet.class }, property = {
				"jmx.objectname=graphql.servlet:type=graphql" }, scope = ServiceScope.PROTOTYPE, configurationPolicy = ConfigurationPolicy.REQUIRE)
@HttpWhiteboardServletPattern(DEFAULT_SERVLET_PATTERN)
@RequireHttpWhiteboard
@Capability(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name = OSGI_GRAPHQL_CAPABILITY_NAME, version = "1.1.0")
public class OsgiGraphQLWhiteboard extends AbstractGraphQLHttpServlet
		implements ServiceTrackerCustomizer<Object, Object>, GraphqlServiceRuntime {
	private static final long serialVersionUID = -5524795258270847878L;

	private final GeckoGraphQLSchemaBuilder schemaBuilder = new GeckoGraphQLSchemaBuilder();

	private int schemaUpdateDelay = 0; // TODO: externalize config if needed
	
	private final Map<ServiceReference<Object>, ServiceObjects<Object>> serviceReferences = new HashMap<>();
	private ServiceRegistration<GraphqlServiceRuntime> runtimeRegistration;
	private ServiceTracker<Object, Object> serviceTracker;
	private BundleContext bundleContext;
	private Hashtable<String, Object> properties = new Hashtable<>();
	private Map<String, String> responseHeaders = new HashMap<>();
	private AtomicLong changeCount = new AtomicLong(0);

	@Reference(service = LoggerFactory.class)
	private Logger logger;
	
	@Activate
	public OsgiGraphQLWhiteboard(ComponentContext componentContext) throws InvalidSyntaxException {
		super();
		
		bundleContext = componentContext.getBundleContext();
		copyProperties(componentContext);

		serviceTracker = new ServiceTracker<Object, Object>(bundleContext,
				FrameworkUtil.createFilter("(|(" + GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER + "=true)("
						+ GeckoGraphQLConstants.GRAPHQL_MUTATION_SERVICE_MARKER + "=*))"),
				this);
		serviceTracker.open();

		Object tracingEnabled = componentContext.getProperties()
				.get(GeckoGraphQLConstants.SERVICE_PROPERTY_TRACING_ENABLED);
		if (tracingEnabled != null && Boolean.parseBoolean(tracingEnabled.toString())) {
			setInstrumentationProvider(new TracingInstrumentationProvider());
		}

		schemaBuilder.activate(schemaUpdateDelay);
	}

	/**
	 * Copies the properties for the Runtime Registration and extracts the
	 * additional request and response headers
	 * 
	 * @param componentContext
	 */
	private void copyProperties(ComponentContext componentContext) {
		Dictionary<String, Object> componentProperties = componentContext.getProperties();
		Enumeration<String> keys = componentProperties.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Object value = componentProperties.get(key);
			if (key.startsWith(GeckoGraphQLConstants.SERVICE_PROPERTY_RESPONSE_HEADER_PREFIX)) {
				String stringValue = "";
				if (value != null) {
					stringValue = value.toString();
				}
				responseHeaders.put(
						key.substring(GeckoGraphQLConstants.SERVICE_PROPERTY_RESPONSE_HEADER_PREFIX.length()),
						stringValue);
			}
			properties.put(key, value);
		}
		properties.put(Constants.SERVICE_CHANGECOUNT, changeCount.get());
	}
	
	protected synchronized void updateSchema() {
		Map<ServiceReference<Object>, ServiceObjects<Object>> copiedServiceMap = new HashMap<ServiceReference<Object>, ServiceObjects<Object>>(
				serviceReferences);
		
		try {
			
			if (!copiedServiceMap.isEmpty()) {

				schemaBuilder.prepareForBuild();
				
				copiedServiceMap.forEach(schemaBuilder::build);
			}			
			
			schemaBuilder.updateSchema();

			updateRuntime();
			
			// FIXME: this method is called from both #bindGraphqlSechemaTypeBuilder and #bindValueConverter BEFORE logger reference is injected, resulting in NullPointerException
			if (logger != null)
				logger.info("Schema generation sucessfull");
		} catch (AssertException e) {
			if (logger != null)
				logger.warn("The current Configuration is invalid: " + e.getMessage());
		}
	}

	/**
	 * Updates the runtime properties or registers the Runtime, if it wasn't set
	 * already
	 */
	private void updateRuntime() {
		if (runtimeRegistration == null) {

			// FIXME: 'bundleContext' may be null, since '#activate' method is called AFTER '#bind' methods
			if (bundleContext != null) {
				runtimeRegistration = bundleContext.registerService(GraphqlServiceRuntime.class, this, properties);
			}
		} else {
			properties.put(Constants.SERVICE_CHANGECOUNT, changeCount.incrementAndGet());
			runtimeRegistration.setProperties(properties);
		}
	}

	@Deactivate
	public void deactivate() {
		schemaBuilder.deactivate();

		serviceTracker.close();
		serviceTracker = null;
		if (runtimeRegistration != null) {
			runtimeRegistration.unregister();
			runtimeRegistration = null;
		}
	}

	@Override
	protected GraphQLConfiguration getConfiguration() {
		return schemaBuilder.buildConfiguration();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
	public void bindProvider(GraphQLProvider provider) {
		if (provider instanceof GraphQLQueryProvider) {
			schemaBuilder.add((GraphQLQueryProvider) provider);
		}
		if (provider instanceof GraphQLMutationProvider) {
			schemaBuilder.add((GraphQLMutationProvider) provider);
		}
		if (provider instanceof GraphQLSubscriptionProvider) {
			schemaBuilder.add((GraphQLSubscriptionProvider) provider);
		}
		if (provider instanceof GraphQLTypesProvider) {
			schemaBuilder.add((GraphQLTypesProvider) provider);
		}
		if (provider instanceof GraphQLCodeRegistryProvider) {
			schemaBuilder.setCodeRegistryProvider((GraphQLCodeRegistryProvider) provider);
		}
		updateSchema();
	}

	public void unbindProvider(GraphQLProvider provider) {
		if (provider instanceof GraphQLQueryProvider) {
			schemaBuilder.remove((GraphQLQueryProvider) provider);
		}
		if (provider instanceof GraphQLMutationProvider) {
			schemaBuilder.remove((GraphQLMutationProvider) provider);
		}
		if (provider instanceof GraphQLSubscriptionProvider) {
			schemaBuilder.remove((GraphQLSubscriptionProvider) provider);
		}
		if (provider instanceof GraphQLTypesProvider) {
			schemaBuilder.remove((GraphQLTypesProvider) provider);
		}
		if (provider instanceof GraphQLCodeRegistryProvider) {
			schemaBuilder.setCodeRegistryProvider(() -> GraphQLCodeRegistry.newCodeRegistry().build());
		}
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindQueryProvider(GraphQLQueryProvider queryProvider) {
		schemaBuilder.add(queryProvider);
		updateSchema();
	}

	public void unbindQueryProvider(GraphQLQueryProvider queryProvider) {
		schemaBuilder.remove(queryProvider);
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindMutationProvider(GraphQLMutationProvider mutationProvider) {
		schemaBuilder.add(mutationProvider);
		updateSchema();
	}

	public void unbindMutationProvider(GraphQLMutationProvider mutationProvider) {
		schemaBuilder.remove(mutationProvider);
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	public void bindSubscriptionProvider(GraphQLSubscriptionProvider subscriptionProvider) {
		schemaBuilder.add(subscriptionProvider);
		updateSchema();
	}

	public void unbindSubscriptionProvider(GraphQLSubscriptionProvider subscriptionProvider) {
		schemaBuilder.remove(subscriptionProvider);
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindTypesProvider(GraphQLTypesProvider typesProvider) {
		schemaBuilder.add(typesProvider);
		updateSchema();
	}

	public void unbindTypesProvider(GraphQLTypesProvider typesProvider) {
		schemaBuilder.remove(typesProvider);
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindServletListener(GraphQLServletListener listener) {
		schemaBuilder.add(listener);
	}

	public void unbindServletListener(GraphQLServletListener listener) {
		schemaBuilder.remove(listener);
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
	public void setContextBuilder(GraphQLServletContextBuilder contextBuilder) {
		schemaBuilder.setContextBuilder(contextBuilder);
	}

	public void unsetContextBuilder(GraphQLServletContextBuilder contextBuilder) {
		schemaBuilder.setContextBuilder(new DefaultGraphQLServletContextBuilder());
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setRootObjectBuilder(GraphQLServletRootObjectBuilder rootObjectBuilder) {
		schemaBuilder.setRootObjectBuilder(rootObjectBuilder);
	}

	public void unsetRootObjectBuilder(GraphQLRootObjectBuilder rootObjectBuilder) {
		schemaBuilder.setRootObjectBuilder(new DefaultGraphQLRootObjectBuilder());
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setExecutionStrategyProvider(ExecutionStrategyProvider provider) {
		schemaBuilder.setExecutionStrategyProvider(provider);
	}

	public void unsetExecutionStrategyProvider(ExecutionStrategyProvider provider) {
		schemaBuilder.setExecutionStrategyProvider(new DefaultExecutionStrategyProvider());
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setInstrumentationProvider(InstrumentationProvider provider) {
		schemaBuilder.setInstrumentationProvider(provider);
	}

	public void unsetInstrumentationProvider(InstrumentationProvider provider) {
		schemaBuilder.setInstrumentationProvider(new NoOpInstrumentationProvider());
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setErrorHandler(GraphQLErrorHandler errorHandler) {
		schemaBuilder.setErrorHandler(errorHandler);
	}

	public void unsetErrorHandler(GraphQLErrorHandler errorHandler) {
		schemaBuilder.setErrorHandler(new DefaultGraphQLErrorHandler());
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void setPreparsedDocumentProvider(PreparsedDocumentProvider preparsedDocumentProvider) {
		schemaBuilder.setPreparsedDocumentProvider(preparsedDocumentProvider);
	}

	public void unsetPreparsedDocumentProvider(PreparsedDocumentProvider preparsedDocumentProvider) {
		schemaBuilder.setPreparsedDocumentProvider(NoOpPreparsedDocumentProvider.INSTANCE);
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindCodeRegistryProvider(GraphQLCodeRegistryProvider graphQLCodeRegistryProvider) {
		schemaBuilder.setCodeRegistryProvider(graphQLCodeRegistryProvider);
		updateSchema();
	}

	public void unbindCodeRegistryProvider(GraphQLCodeRegistryProvider graphQLCodeRegistryProvider) {
		schemaBuilder.setCodeRegistryProvider(() -> GraphQLCodeRegistry.newCodeRegistry().build());
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
	public void bindGraphqlSechemaTypeBuilder(GraphqlSchemaTypeBuilder typeBuilder) {
		System.err.println("bindGraphqlSechemaTypeBuilder: " + typeBuilder.toString());
		schemaBuilder.add(typeBuilder);
		updateSchema();
	}

	public void unbindGraphqlSechemaTypeBuilder(GraphqlSchemaTypeBuilder typeBuilder) {
		schemaBuilder.remove(typeBuilder);
		updateSchema();
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindValueConverter(GeckoGraphQLValueConverter valueConverter) {
		System.err.println("bindValueConverter: " + valueConverter.toString());
		schemaBuilder.add(valueConverter);
	}

	public void unbindValueConverter(GeckoGraphQLValueConverter valueConverter) {
		schemaBuilder.remove(valueConverter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public Object addingService(ServiceReference<Object> reference) {
		ServiceObjects<Object> serviceObjects = bundleContext.getServiceObjects(reference);
		if (serviceObjects == null) {
			return null;
		}
		serviceReferences.put(reference, serviceObjects);
		
		return bundleContext.getService(reference);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		serviceReferences.remove(reference);
		bundleContext.ungetService(reference);
		updateSchema();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlServiceRuntime#getRuntimeDTO()
	 */
	@Override
	public RuntimeDTO getRuntimeDTO() {
		// TODO Auto-generated method stub
		return null;
	}
}
