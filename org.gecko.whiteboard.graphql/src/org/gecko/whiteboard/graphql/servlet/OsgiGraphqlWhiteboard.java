/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
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

import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.DEFAULT_SERVLET_PATTERN;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_MUTATION_SERVICE;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE;
import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.OSGI_GRAPHQL_CAPABILITY_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.dto.RuntimeDTO;
import org.gecko.whiteboard.graphql.instrumentation.TracingInstrumentationProvider;
import org.gecko.whiteboard.graphql.service.ServiceSchemaBuilder;
import org.gecko.whiteboard.graphql.servlet.OsgiGraphqlWhiteboard.GraphqlWhiteboardConfig;
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
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import graphql.AssertException;
import graphql.execution.preparsed.NoOpPreparsedDocumentProvider;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.servlet.AbstractGraphQLHttpServlet;
import graphql.servlet.DefaultExecutionStrategyProvider;
import graphql.servlet.DefaultGraphQLContextBuilder;
import graphql.servlet.DefaultGraphQLErrorHandler;
import graphql.servlet.DefaultGraphQLRootObjectBuilder;
import graphql.servlet.DefaultGraphQLSchemaProvider;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLContextBuilder;
import graphql.servlet.GraphQLErrorHandler;
import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLProvider;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLRootObjectBuilder;
import graphql.servlet.GraphQLSchemaProvider;
import graphql.servlet.GraphQLServletListener;
import graphql.servlet.GraphQLTypesProvider;
import graphql.servlet.InstrumentationProvider;
import graphql.servlet.NoOpInstrumentationProvider;

/**
 * 
 * @author Juergen Albert
 * @since 19 Dec 2018
 */
@Component(
		name = GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME,
        service={Servlet.class},
        property = {"jmx.objectname=graphql.servlet:type=graphql"},
        scope=ServiceScope.PROTOTYPE,
        configurationPolicy=ConfigurationPolicy.REQUIRE
)
@HttpWhiteboardServletPattern(DEFAULT_SERVLET_PATTERN)
@RequireHttpWhiteboard
@Capability(namespace=ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name= OSGI_GRAPHQL_CAPABILITY_NAME, version="1.0.0")
@Designate(factory=true,ocd=GraphqlWhiteboardConfig.class)
public class OsgiGraphqlWhiteboard extends AbstractGraphQLHttpServlet implements ServiceTrackerCustomizer<Object, Object>, GraphqlServiceRuntime {

	@ObjectClassDefinition
	@interface GraphqlWhiteboardConfig {
		@AttributeDefinition(required = false)
		boolean cors_enable() default true;

		@AttributeDefinition(required = false)
		String cors_access_control_allow_origin() default "*";

		@AttributeDefinition(required = false)
		String cors_access_control_allow_headers() default "*";

		@AttributeDefinition(required = false)
		String cors_access_control_request_method() default "*";
	}

	@Activate
	GraphqlWhiteboardConfig config;
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final List<GraphQLQueryProvider> queryProviders = new ArrayList<>();
    private final List<GraphQLMutationProvider> mutationProviders = new ArrayList<>();
    private final List<GraphQLTypesProvider> typesProviders = new ArrayList<>();
    private final Map<ServiceReference<Object>, ServiceObjects<Object>> serviceReferences = new HashMap<>();
    private final List<GraphqlSchemaTypeBuilder> typeBuilder = new LinkedList<>();

    private final GraphQLQueryInvoker queryInvoker;
    private final GraphQLInvocationInputFactory invocationInputFactory;
    private final GraphQLObjectMapper graphQLObjectMapper;

    private GraphQLContextBuilder contextBuilder = new DefaultGraphQLContextBuilder();
    private GraphQLRootObjectBuilder rootObjectBuilder = new DefaultGraphQLRootObjectBuilder();
    private ExecutionStrategyProvider executionStrategyProvider = new DefaultExecutionStrategyProvider();
    private InstrumentationProvider instrumentationProvider = new NoOpInstrumentationProvider();
    private GraphQLErrorHandler errorHandler = new DefaultGraphQLErrorHandler();
    private PreparsedDocumentProvider preparsedDocumentProvider = NoOpPreparsedDocumentProvider.INSTANCE;

    private GraphQLSchemaProvider schemaProvider;
	private ServiceTracker<Object, Object> serviceTracker;
	private BundleContext bundleContext;
	
	private Hashtable<String, Object> properties = new Hashtable<>();

	private AtomicLong changeCount = new AtomicLong(0);
	private ServiceRegistration<GraphqlServiceRuntime> runtimeRegistration;
	
    /* 
     * (non-Javadoc)
     * @see graphql.servlet.AbstractGraphQLHttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (config.cors_enable()) {
			resp.setHeader("Access-Control-Allow-Origin", config.cors_access_control_allow_origin());
    	}
    	super.doGet(req, resp);
    }
    
    /* 
     * (non-Javadoc)
     * @see graphql.servlet.AbstractGraphQLHttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
		if (config.cors_enable()) {
			resp.setHeader("Access-Control-Allow-Origin", config.cors_access_control_allow_origin());
		}
    	super.doPost(req, resp);
    }
    
    /* 
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (config.cors_enable()) {

			resp.setHeader("Access-Control-Allow-Origin", config.cors_access_control_allow_origin());
			resp.setHeader("Access-Control-Allow-Headers", config.cors_access_control_allow_headers());
			resp.setHeader("Access-Control-Request-Method", config.cors_access_control_request_method());
    	}
    	super.service(req, resp);
    }
    
    @Override
    protected GraphQLQueryInvoker getQueryInvoker() {
        return queryInvoker;
    }

    @Override
    protected GraphQLInvocationInputFactory getInvocationInputFactory() {
        return invocationInputFactory;
    }

    @Override
    protected GraphQLObjectMapper getGraphQLObjectMapper() {
        return graphQLObjectMapper;
    }

    @Activate
    public OsgiGraphqlWhiteboard(ComponentContext componentContext) throws InvalidSyntaxException {
    	bundleContext = componentContext.getBundleContext();
    	copyProperties(componentContext);
    	serviceTracker = new ServiceTracker<Object, Object>(bundleContext, FrameworkUtil.createFilter("(|(" + GRAPHQL_WHITEBOARD_QUERY_SERVICE + "=*)(" + GRAPHQL_WHITEBOARD_MUTATION_SERVICE + "=*))"), this);
    	serviceTracker.open();
    	Object tracingEnabled = componentContext.getProperties().get(GeckoGraphQLConstants.TRACING_ENABLED);
    	if(tracingEnabled != null && Boolean.parseBoolean(tracingEnabled.toString())) {
    		this.instrumentationProvider = new TracingInstrumentationProvider(); 
    	}
    	
    	updateSchema();
        this.queryInvoker = GraphQLQueryInvoker.newBuilder()
            .withPreparsedDocumentProvider(this::getPreparsedDocumentProvider)
            .withInstrumentation(() -> this.getInstrumentationProvider().getInstrumentation())
            .withExecutionStrategyProvider(this::getExecutionStrategyProvider).build();

        this.invocationInputFactory = GraphQLInvocationInputFactory.newBuilder(this::getSchemaProvider)
            .withGraphQLContextBuilder(this::getContextBuilder)
            .withGraphQLRootObjectBuilder(this::getRootObjectBuilder)
            .build();

        this.graphQLObjectMapper = GraphQLObjectMapper.newBuilder()
            .withGraphQLErrorHandler(this::getErrorHandler)
            .build();
    }
    
    
    /**
	 * @param componentContext
	 */
	private void copyProperties(ComponentContext componentContext) {
		Dictionary<String, Object> componentProperties = componentContext.getProperties();
		Enumeration<String> keys = componentProperties.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, componentProperties.get(key));
		}
		properties.put(Constants.SERVICE_CHANGECOUNT, changeCount.get());
	}

	@Deactivate
    public void deactivate() {
    	serviceTracker.close();
    	serviceTracker = null;
    	if(runtimeRegistration != null) {
    		runtimeRegistration.unregister();
    		runtimeRegistration = null;
    	}
    }

    protected synchronized void updateSchema() {
        final GraphQLObjectType.Builder queryTypeBuilder = GraphQLObjectType.newObject().name("Query").description("Root query type");

        for (GraphQLQueryProvider provider : queryProviders) {
            if (provider.getQueries() != null && !provider.getQueries().isEmpty()) {
                provider.getQueries().forEach(queryTypeBuilder::field);
            }
        }

        final Set<GraphQLType> types = new HashSet<>();
        for (GraphQLTypesProvider typesProvider : typesProviders) {
            types.addAll(typesProvider.getTypes());
        }

        final GraphQLObjectType.Builder mutationTypeBuilder = GraphQLObjectType.newObject().name("Mutation").description("Root mutation type");

        if (!mutationProviders.isEmpty()) {
            for (GraphQLMutationProvider provider : mutationProviders) {
                provider.getMutations().forEach(mutationTypeBuilder::field);
            }
        }
        
        Map<ServiceReference<Object>, ServiceObjects<Object>> copiedServiceMap = new HashMap<ServiceReference<Object>, ServiceObjects<Object>>(serviceReferences);
        
        if(!copiedServiceMap.isEmpty()) {
        	ServiceSchemaBuilder sb = new ServiceSchemaBuilder(queryTypeBuilder, mutationTypeBuilder, types, typeBuilder);
        	copiedServiceMap.forEach(sb::build);
        }
        try {
        	GraphQLObjectType query = queryTypeBuilder.build();
        	GraphQLObjectType mutation = mutationTypeBuilder.build();
        	this.schemaProvider = new DefaultGraphQLSchemaProvider(GraphQLSchema.newSchema().query(query).mutation(mutation.getFieldDefinitions().isEmpty() ? null : mutation).additionalTypes(types).build());
        	updateRuntime();
        	log.info("Schema generation sucessfull");
        } catch(AssertException e) {
        	log.warn("The current Configuration is invalid: " + e.getMessage());
        }
    }

    /**
	 * Updates the runtime properties or registers the Runtime, if it wasn't set already
	 */
	private void updateRuntime() {
		if(runtimeRegistration == null) {
			runtimeRegistration = bundleContext.registerService(GraphqlServiceRuntime.class, this, properties);
		} else {
			properties.put(Constants.SERVICE_CHANGECOUNT, changeCount.incrementAndGet());
			runtimeRegistration.setProperties(properties);
		}
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policyOption = ReferencePolicyOption.GREEDY)
    public void bindProvider(GraphQLProvider provider) {
        if (provider instanceof GraphQLQueryProvider) {
            queryProviders.add((GraphQLQueryProvider) provider);
        }
        if (provider instanceof GraphQLMutationProvider) {
            mutationProviders.add((GraphQLMutationProvider) provider);
        }
        if (provider instanceof GraphQLTypesProvider) {
            typesProviders.add((GraphQLTypesProvider) provider);
        }
        updateSchema();
    }
    public void unbindProvider(GraphQLProvider provider) {
        if (provider instanceof GraphQLQueryProvider) {
            queryProviders.remove(provider);
        }
        if (provider instanceof GraphQLMutationProvider) {
            mutationProviders.remove(provider);
        }
        if (provider instanceof GraphQLTypesProvider) {
            typesProviders.remove(provider);
        }
        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void bindQueryProvider(GraphQLQueryProvider queryProvider) {
        queryProviders.add(queryProvider);
        updateSchema();
    }
    public void unbindQueryProvider(GraphQLQueryProvider queryProvider) {
        queryProviders.remove(queryProvider);
        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void bindMutationProvider(GraphQLMutationProvider mutationProvider) {
        mutationProviders.add(mutationProvider);
        updateSchema();
    }
    public void unbindMutationProvider(GraphQLMutationProvider mutationProvider) {
        mutationProviders.remove(mutationProvider);
        updateSchema();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void bindTypesProviders(GraphQLTypesProvider typesProvider) {
        typesProviders.add(typesProvider);
        updateSchema();
    }

    public void unbindTypesProviders(GraphQLTypesProvider typesProvider) {
    	if(typesProviders.contains(typesProvider)) {
    		typesProviders.remove(typesProvider);
    		updateSchema();
    	}
    }
 
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void bindServletListener(GraphQLServletListener listener) {
        this.addListener(listener);
    }
    public void unbindServletListener(GraphQLServletListener listener) {
        this.removeListener(listener);
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
    public void setContextProvider(GraphQLContextBuilder contextBuilder) {
        this.contextBuilder = contextBuilder;
    }
    public void unsetContextProvider(GraphQLContextBuilder contextBuilder) {
        this.contextBuilder = new DefaultGraphQLContextBuilder();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy=ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setRootObjectBuilder(GraphQLRootObjectBuilder rootObjectBuilder) {
        this.rootObjectBuilder = rootObjectBuilder;
    }
    public void unsetRootObjectBuilder(GraphQLRootObjectBuilder rootObjectBuilder) {
        this.rootObjectBuilder = new DefaultGraphQLRootObjectBuilder();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy= ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setExecutionStrategyProvider(ExecutionStrategyProvider provider) {
        executionStrategyProvider = provider;
    }
    public void unsetExecutionStrategyProvider(ExecutionStrategyProvider provider) {
        executionStrategyProvider = new DefaultExecutionStrategyProvider();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy= ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setInstrumentationProvider(InstrumentationProvider provider) {
        instrumentationProvider = provider;
    }
    public void unsetInstrumentationProvider(InstrumentationProvider provider) {
        instrumentationProvider = new NoOpInstrumentationProvider();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy= ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setErrorHandler(GraphQLErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    public void unsetErrorHandler(GraphQLErrorHandler errorHandler) {
        this.errorHandler = new DefaultGraphQLErrorHandler();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy= ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setPreparsedDocumentProvider(PreparsedDocumentProvider preparsedDocumentProvider) {
        this.preparsedDocumentProvider = preparsedDocumentProvider;
    }
    public void unsetPreparsedDocumentProvider(PreparsedDocumentProvider preparsedDocumentProvider) {
        this.preparsedDocumentProvider = NoOpPreparsedDocumentProvider.INSTANCE;
    }

    public GraphQLContextBuilder getContextBuilder() {
        return contextBuilder;
    }

    public GraphQLRootObjectBuilder getRootObjectBuilder() {
        return rootObjectBuilder;
    }

    public ExecutionStrategyProvider getExecutionStrategyProvider() {
        return executionStrategyProvider;
    }

    public InstrumentationProvider getInstrumentationProvider() {
        return instrumentationProvider;
    }

    public GraphQLErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public PreparsedDocumentProvider getPreparsedDocumentProvider() {
        return preparsedDocumentProvider;
    }

    public GraphQLSchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public Object addingService(ServiceReference<Object> reference) {
		ServiceObjects<Object> serviceObjects = bundleContext.getServiceObjects(reference);
		serviceReferences.put(reference, serviceObjects);
		updateSchema();
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
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policyOption=ReferencePolicyOption.GREEDY)
	public void bindGraphqlSechemaTypeBuilder (GraphqlSchemaTypeBuilder typeBuilder) {
		this.typeBuilder.add(typeBuilder);
		updateSchema();
	}

	public void unbindGraphqlSechemaTypeBuilder (GraphqlSchemaTypeBuilder typeBuilder) {
		this.typeBuilder.remove(typeBuilder);
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