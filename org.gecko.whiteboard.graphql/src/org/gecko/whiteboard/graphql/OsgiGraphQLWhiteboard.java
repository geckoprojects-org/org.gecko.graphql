package org.gecko.whiteboard.graphql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gecko.whiteboard.graphql.service.ServiceSchemaBuilder;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
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
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.namespace.implementation.ImplementationNamespace;

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

@Component(
		name = "GeckoGraphQLWhiteboard",
        service={Servlet.class},
        property = {"jmx.objectname=graphql.servlet:type=graphql"},
        scope=ServiceScope.PROTOTYPE,
        configurationPolicy=ConfigurationPolicy.REQUIRE
)
@HttpWhiteboardServletPattern("/graphql/*")
@RequireHttpWhiteboard
@Capability(namespace=ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name="osgi.graphql", version="1.0.0")
public class OsgiGraphQLWhiteboard extends AbstractGraphQLHttpServlet implements ServiceTrackerCustomizer<Object, Object> {

    /** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private final List<GraphQLQueryProvider> queryProviders = new ArrayList<>();
    private final List<GraphQLMutationProvider> mutationProviders = new ArrayList<>();
    private final List<GraphQLTypesProvider> typesProviders = new ArrayList<>();
    private final Map<ServiceReference<Object>, ServiceObjects<Object>> serviceReferences = new HashMap<>();

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

    /* 
     * (non-Javadoc)
     * @see graphql.servlet.AbstractGraphQLHttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.setHeader("Access-Control-Allow-Origin", "*");
    	super.doGet(req, resp);
    }
    
    /* 
     * (non-Javadoc)
     * @see graphql.servlet.AbstractGraphQLHttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.setHeader("Access-Control-Allow-Origin", "*");
    	super.doPost(req, resp);
    }
    
    /* 
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.setHeader("Access-Control-Allow-Origin", "*");
    	resp.setHeader("Access-Control-Allow-Headers", "*");
    	resp.setHeader("Access-Control-Request-Method", "*");
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

    public OsgiGraphQLWhiteboard() {
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
        //this.graphQLObjectMapper.getJacksonMapper().registerModule(new EMFModule());
    }
    
    @Activate
    public void activate(ComponentContext componentContext) throws InvalidSyntaxException {
    	bundleContext = componentContext.getBundleContext();
    	serviceTracker = new ServiceTracker<Object, Object>(bundleContext, FrameworkUtil.createFilter("(graphql.service.name=*)"), this);
    	serviceTracker.open();
    }
    
    @Deactivate
    public void deactivate() {
    	serviceTracker.close();
    }

    protected void updateSchema() {
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

        GraphQLObjectType mutationType = null;

        if (!mutationProviders.isEmpty()) {
            final GraphQLObjectType.Builder mutationTypeBuilder = GraphQLObjectType.newObject().name("Mutation").description("Root mutation type");

            for (GraphQLMutationProvider provider : mutationProviders) {
                provider.getMutations().forEach(mutationTypeBuilder::field);
            }

            if (!mutationTypeBuilder.build().getFieldDefinitions().isEmpty()) {
                mutationType = mutationTypeBuilder.build();
            }
        }
        
        if(!serviceReferences.isEmpty()) {
        	serviceReferences.entrySet().stream()
        		.map(e -> new ServiceSchemaBuilder(e.getKey(), e.getValue(), queryTypeBuilder, types))
        		.forEach(sb -> sb.build());
        }
        try {
        	this.schemaProvider = new DefaultGraphQLSchemaProvider(GraphQLSchema.newSchema().query(queryTypeBuilder.build()).mutation(mutationType).additionalTypes(types).build());
        	log.info("Schema generation sucessfull");
        } catch(AssertException e) {
        	log.warn("The current Configuration is invalid: " + e.getMessage());
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
		return serviceObjects.getService();
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
		ServiceObjects<Object> remove = serviceReferences.remove(reference);
		if(remove != null) {
			remove.ungetService(service);
		}
		updateSchema();
	}
}