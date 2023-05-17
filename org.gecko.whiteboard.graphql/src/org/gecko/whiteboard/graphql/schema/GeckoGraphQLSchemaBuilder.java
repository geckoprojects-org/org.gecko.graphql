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
package org.gecko.whiteboard.graphql.schema;

import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;
import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GeckoGraphQLUtil;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.annotation.GraphqlDocumentation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;

/**
 * Mostly product of merge of {@link graphql.kickstart.servlet.OsgiSchemaBuilder} with what was known previously as org.gecko.whiteboard.graphql.service.ServiceSchemaBuilder, refactored, with some additional methods.
 * 
 * @author Michal H. Siemaszko
 * @since May 9, 2023
 */
public class GeckoGraphQLSchemaBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(GeckoGraphQLSchemaBuilder.class);
	
	private final List<GraphQLQueryProvider> queryProviders = new ArrayList<>();
	private final List<GraphQLMutationProvider> mutationProviders = new ArrayList<>();
	private final List<GraphQLSubscriptionProvider> subscriptionProviders = new ArrayList<>();
	private final List<GraphQLTypesProvider> typesProviders = new ArrayList<>();
	private final List<GraphQLServletListener> listeners = new ArrayList<>();
	private final List<GeckoGraphQLValueConverter> valueConverters = new ArrayList<>();
	private final Map<String, GraphQLType> typeMapping = new HashMap<String, GraphQLType>();
	private final List<GraphqlSchemaTypeBuilder> schemaTypeBuilder = new LinkedList<>();
	private final GraphqlSchemaTypeBuilder defaultBuilder = new DefaultGraphQLSchemaTypeBuilder();	
	
	private BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();	
	private GraphQLObjectType.Builder queryTypeBuilder;
	private GraphQLObjectType.Builder mutationTypeBuilder;	
	private Set<GraphQLType> types = new HashSet<>();
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
	
	public void activate(int schemaUpdateDelay) {
		this.schemaUpdateDelay = schemaUpdateDelay;
		if (schemaUpdateDelay != 0) {
			executor = Executors.newSingleThreadScheduledExecutor();
		}
		
		this.queryTypeBuilder = getQueryTypeBuilder();
		
		this.mutationTypeBuilder = getMutationTypeBuilder();
		
		this.types = buildTypes();

		this.types.forEach(type -> typeMapping.put(GeckoGraphQLUtil.INSTANCE.getTypeName(type), type));
	}
	
	public void deactivate() {
		if (executor != null) {
			executor.shutdown();
		}
	}	

	public void updateSchema() {
		if (schemaUpdateDelay == 0) {
			doUpdateSchema();
		} else {
			if (updateFuture != null) {
				updateFuture.cancel(true);
			}

			updateFuture = executor.schedule(this::doUpdateSchema, schemaUpdateDelay, TimeUnit.MILLISECONDS);
		}
	}
	
	public GraphQLConfiguration buildConfiguration() {
		// @formatter:off
	    return GraphQLConfiguration.with(buildInvocationInputFactory())
	        .with(buildQueryInvoker())
	        .with(buildObjectMapper())
	        .with(listeners)
	        .build();
	    // @formatter:on
	}	
	
	public void prepareForBuild() {
		this.queryTypeBuilder = getQueryTypeBuilder();
		/**
		 * {@link graphql.schema.GraphQLObjectType.Builder} does not define a method to check if there are any fields defined, nor to retrieve list of all fields defined,
		 * so, '_empty' field communicates that there are no queryProviders registered / no other fields were defined yet
		 */
		if (this.queryTypeBuilder.hasField("_empty")) {
			this.queryTypeBuilder.clearFields();
		}
		
		this.mutationTypeBuilder = getMutationTypeBuilder();
		/**
		 * {@link graphql.schema.GraphQLObjectType.Builder} does not define a method to check if there are any fields defined, nor to retrieve list of all fields defined,
		 * so, '_empty' field communicates that there are no mutationProviders registered / no other fields were defined yet
		 */
		if (this.mutationTypeBuilder.hasField("_empty")) {
			this.mutationTypeBuilder.clearFields();
		}		
		
		this.types = buildTypes();
		this.types.forEach(type -> typeMapping.put(GeckoGraphQLUtil.INSTANCE.getTypeName(type), type));
	}
	
	/**
	 * Builds the query and mutation Schema
	 */
	public void build(Map.Entry<ServiceReference<Object>, ServiceObjects<Object>> entry) {
		build(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Builds the query and mutation Schema
	 */
	public void build(ServiceReference<Object> serviceReference, ServiceObjects<Object> serviceObjects) {
		try {
			List<Class<?>> interfaces = getDeclaredObjectClasses(serviceReference);
			
			for(Class<?> curInterface : interfaces) {
				boolean isQuery = isDeclaredQueryInterface(curInterface, serviceReference);
				boolean isMutation = isDeclaredMutationInterface(curInterface, serviceReference);
				if(isQuery && isMutation) {
					LOG.warn("The Interace {} is marked as query and mutation. You must chose one. The Interface will be ignored", curInterface.getName());
					continue;
				}
				if(isQuery) {
					String name = getQueryName(serviceReference, curInterface);
					
					queryTypeBuilder.field(GraphqlSchemaTypeBuilder.createReferenceField(name, new StaticDataFetcher(serviceObjects), createService(name, curInterface, typeMapping)));
					
				} else if (isMutation){
					String name = getMutationName(serviceReference, curInterface);

					mutationTypeBuilder.field(GraphqlSchemaTypeBuilder.createReferenceField(name, new StaticDataFetcher(serviceObjects), createService(name, curInterface, typeMapping)));
				}
			}
			types.addAll(typeMapping.values());
		} catch (Throwable e) {
			LOG.error("Args... " + e.getMessage(), e);
		} finally {
			ctx.ungetService(serviceReference);
		}
	}	
	
	public void add(GraphqlSchemaTypeBuilder typeBuilder) {
		schemaTypeBuilder.add(typeBuilder);
	}
	
	public void remove(GraphqlSchemaTypeBuilder typeBuilder) {
		schemaTypeBuilder.remove(typeBuilder);
	}	
	
	public void add(GraphQLQueryProvider provider) {
		queryProviders.add(provider);
	}

	public void add(GraphQLMutationProvider provider) {
		mutationProviders.add(provider);
	}

	public void add(GraphQLSubscriptionProvider provider) {
		subscriptionProviders.add(provider);
	}

	public void add(GraphQLTypesProvider provider) {
		typesProviders.add(provider);
	}
	
	public void add(GraphQLServletListener listener) {
		listeners.add(listener);
	}	
	
	public void remove(GraphQLQueryProvider provider) {
		queryProviders.remove(provider);
	}

	public void remove(GraphQLMutationProvider provider) {
		mutationProviders.remove(provider);
	}

	public void remove(GraphQLSubscriptionProvider provider) {
		subscriptionProviders.remove(provider);
	}

	public void remove(GraphQLTypesProvider provider) {
		typesProviders.remove(provider);
	}

	public void remove(GraphQLServletListener listener) {
		listeners.remove(listener);
	}
	
	public void setCodeRegistryProvider(GraphQLCodeRegistryProvider provider) {
		codeRegistryProvider = provider;
	}
	
	public void setContextBuilder(GraphQLServletContextBuilder builder) {
		contextBuilder = builder;
	}

	public void setRootObjectBuilder(GraphQLServletRootObjectBuilder builder) {
		rootObjectBuilder = builder;
	}

	public void setExecutionStrategyProvider(ExecutionStrategyProvider provider) {
		executionStrategyProvider = provider;
	}

	public void setInstrumentationProvider(InstrumentationProvider provider) {
		instrumentationProvider = provider;
	}

	public void setErrorHandler(GraphQLErrorHandler handler) {
		errorHandler = handler;
	}

	public void setPreparsedDocumentProvider(PreparsedDocumentProvider provider) {
		preparsedDocumentProvider = provider;
	}
	
	public void add(GeckoGraphQLValueConverter valueConverter) {
		valueConverters.add(valueConverter);
	}
	
	public void remove(GeckoGraphQLValueConverter valueConverter) {
		valueConverters.remove(valueConverter);
	}
	
	private void doUpdateSchema() {
		try {
			
			// @formatter:off
			graphql.schema.GraphQLSchema.Builder graphQLSchemaBuilder = newSchema()
	                .query(this.queryTypeBuilder.build())
	                .mutation(this.mutationTypeBuilder.build())
	                .subscription(buildSubscriptionType())
	                .additionalTypes(this.types)
	                .codeRegistry(codeRegistryProvider.getCodeRegistry());
			// @formatter:on
			
			this.schemaProvider = new DefaultGraphQLSchemaServletProvider(graphQLSchemaBuilder.build());
			
			LOG.info("Schema created successfully!");
			
		} catch (Throwable t) {
			LOG.warn("Schema could not be created!", t);
		}
	}	
	
	private GraphQLObjectType.Builder getQueryTypeBuilder() {
		final GraphQLObjectType.Builder queryTypeBuilder = newObject().name("Query").description("Root query type");

		if (!queryProviders.isEmpty()) {
			for (GraphQLQueryProvider provider : queryProviders) {
				if (provider.getQueries() != null && !provider.getQueries().isEmpty()) {
					provider.getQueries().forEach(queryTypeBuilder::field);
				}
			}
		} else {
			// graphql-java enforces Query type to be there with at least some field.
			// @formatter:off
			queryTypeBuilder.field(
					GraphQLFieldDefinition.newFieldDefinition()
						.name("_empty")
						.type(Scalars.GraphQLBoolean)
						.build());
			// @formatter:on
		}
		return queryTypeBuilder;
	}
	
	private Set<GraphQLType> buildTypes() {
		if (!this.types.isEmpty()) {
			return this.types;
		} else {
			// @formatter:off
		    return typesProviders.stream()
		        .map(GraphQLTypesProvider::getTypes)
		        .flatMap(Collection::stream)
		        .collect(toSet());
		    // @formatter:on
		}
	}

	private GraphQLObjectType.Builder getMutationTypeBuilder() {
		return getObjectTypeBuilder("Mutation", new ArrayList<>(mutationProviders));
	}

	private GraphQLObjectType buildSubscriptionType() {
		return buildObjectType("Subscription", new ArrayList<>(subscriptionProviders));
	}
	
	private GraphQLObjectType buildObjectType(String name, List<GraphQLFieldProvider> providers) {
		final GraphQLObjectType.Builder objectTypeBuilder = getObjectTypeBuilder(name, providers);
		if (objectTypeBuilder != null) {
			return objectTypeBuilder.build();
		}
		
		return null;
	}
	
	private GraphQLObjectType.Builder getObjectTypeBuilder(String name, List<GraphQLFieldProvider> providers) {
		final GraphQLObjectType.Builder typeBuilder = newObject().name(name).description("Root " + name.toLowerCase() + " type");
		
		if (!providers.isEmpty()) {
			for (GraphQLFieldProvider provider : providers) {
				provider.getFields().forEach(typeBuilder::field);
			}
		} else {
			// graphql-java enforces both Query, Mutation and Subscription type to have at least one field
			// @formatter:off
			typeBuilder.field(
					GraphQLFieldDefinition.newFieldDefinition()
						.name("_empty")
						.type(Scalars.GraphQLBoolean)
						.build());
			// @formatter:on
		}

		return typeBuilder;
	}

	private GraphQLSchemaServletProvider getSchemaProvider() {
		return schemaProvider;
	}

	private GraphQLInvocationInputFactory buildInvocationInputFactory() {
		// @formatter:off
	    return GraphQLInvocationInputFactory.newBuilder(this::getSchemaProvider)
	        .withGraphQLContextBuilder(contextBuilder)
	        .withGraphQLRootObjectBuilder(rootObjectBuilder)
	        .build();
	    // @formatter:on
	}

	private GraphQLQueryInvoker buildQueryInvoker() {
		// @formatter:off
	    return GraphQLQueryInvoker.newBuilder()
	        .withPreparsedDocumentProvider(preparsedDocumentProvider)
	        .withInstrumentation(() -> instrumentationProvider.getInstrumentation())
	        .withExecutionStrategyProvider(executionStrategyProvider)
	        .build();
	    // @formatter:on
	}

	private GraphQLObjectMapper buildObjectMapper() {
		return GraphQLObjectMapper.newBuilder().withGraphQLErrorHandler(errorHandler).build();
	}
	
	/**
	 * Tries to get all declared object classes
	 * @param serviceReference the
	 * @return
	 */
	private List<Class<?>> getDeclaredObjectClasses(ServiceReference<Object> serviceReference) {
		if(serviceReference.getBundle() == null) {
			LOG.warn("The Service is already unregistered. This service need to be ignored {}", serviceReference.getProperty(Constants.OBJECTCLASS));
			return Collections.emptyList();
		}
		String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
		List<Class<?>> interfaces = new ArrayList<Class<?>>(objectClasses.length);
		for(String objectClass : objectClasses) {
			try {
				BundleWiring bundleWiring = serviceReference.getBundle().adapt(BundleWiring.class);
				interfaces.add(bundleWiring.getClassLoader().loadClass(objectClass));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Cant load class for ObjectClass", e);
			}
		}
		return interfaces;
	}
	
	/**
	 * Decides if the Interface should be handled as a Query 
	 * @param curInterface
	 * @param serviceReference2
	 * @return
	 */
	private boolean isDeclaredQueryInterface(Class<?> curInterface, ServiceReference<Object> serviceReference) {
		Object marker = serviceReference.getProperty(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_MARKER);
		if(marker == null || !Boolean.valueOf(marker.toString())) {
			return false;
		}
		return isDeclaredInterfaceForProperty(curInterface, serviceReference, GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE);
	}

	/**
	 * Decides if the Interface should be handled as a Mutation 
	 * @param curInterface
	 * @param serviceReference
	 * @return
	 */
	private boolean isDeclaredMutationInterface(Class<?> curInterface, ServiceReference<Object> serviceReference) {
		Object marker = serviceReference.getProperty(GeckoGraphQLConstants.GRAPHQL_MUTATION_SERVICE_MARKER);
		if(marker == null || !Boolean.valueOf(marker.toString())) {
			return false;
		}
		return isDeclaredInterfaceForProperty(curInterface, serviceReference, GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_MUTATION_SERVICE);
	}

	/**
	 * Looks if the given Interfacce is mentioned in the given Service property 
	 * @param curInterface
	 * @param serviceReference
	 * @return
	 */
	private boolean isDeclaredInterfaceForProperty(Class<?> curInterface, ServiceReference<Object> serviceReference, String property) {
		
		String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
		Object queryInterfacesUntyped = serviceReference.getProperty(property);
		if(queryInterfacesUntyped == null) {
			return true;
		}
		String[] queryInterfaces = null;
		if(queryInterfacesUntyped instanceof String[]) {
			queryInterfaces = (String[]) queryInterfacesUntyped;
		} else {
			queryInterfaces = new String[] {queryInterfacesUntyped.toString()};
		}

		for(String objectClass : objectClasses) {
			String intefaceName = curInterface.getName();
			if(intefaceName.equals(objectClass) && containsInterface(queryInterfaces, intefaceName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param queryInterfaces
	 * @param intefaceName
	 * @return
	 */
	private boolean containsInterface(String[] interfaces, String intefaceName) {
		if(interfaces.length == 0) {
			return true;
		}
		for(String name : interfaces) {
			if(intefaceName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param serviceReference2
	 * @return
	 */
	private String getQueryName(ServiceReference<Object> serviceReference, Class<?> theInterface) {
		String name = (String) serviceReference.getProperty(GeckoGraphQLConstants.GRAPHQL_QUERY_SERVICE_NAME);
		if(name == null || name.isEmpty()) {
			name = theInterface.getSimpleName(); 
		}
		return name;
	}

	/**
	 * @param serviceReference2
	 * @return
	 */
	private String getMutationName(ServiceReference<Object> serviceReference, Class<?> theInterface) {
		String name = (String) serviceReference.getProperty(GeckoGraphQLConstants.GRAPHQL_MUTATION_SERVICE_NAME);
		
		if(name == null || name.isEmpty()) {
			name = theInterface.getSimpleName(); 
		}
		return name;
	}	
	
	/**
	 * Uses Reflection to build a Schema for the given Service Interface
	 * @param name the name of the Service
	 * @param curInterface the interface we want to map
	 * @param typeMapping the list of the 
	 * @return the Object type of th service
	 */
	private GraphQLObjectType createService(String name, Class<?> curInterface, Map<String, GraphQLType> typeMapping) {
		GraphQLType existingType = typeMapping.get(name);
		graphql.schema.GraphQLObjectType.Builder serviceBuilder = null;
		if(existingType != null) {
			serviceBuilder = GraphQLObjectType.newObject((GraphQLObjectType) existingType);
		} else {
			serviceBuilder = GraphQLObjectType.newObject().name(name);
		}
		for(Method method : curInterface.getMethods()) {
			String methodName = method.getName();
			
			GraphQLOutputType returnType = (GraphQLOutputType) createType(method.getGenericReturnType(), typeMapping, false, getMethodAnnotations(method));
			Map<String, ParameterContext> parameters = new HashMap<>();
			boolean ignore = false;
			String methodDocumentation = getDocumentation(method);
			for(Parameter p : method.getParameters()) {
				if(p.getType().equals(DataFetchingEnvironment.class)) {
					continue;
				}
				
				String parameterName = getParameterName(p);
				final Type parameterType = Collection.class.isAssignableFrom(p.getType()) ? p.getParameterizedType() : p.getType();
				GraphQLType basicType = null;
				if(Collection.class.isAssignableFrom(p.getType())) {
					Type theType = ((ParameterizedType) parameterType).getActualTypeArguments()[0];
					
					basicType = GraphqlSchemaTypeBuilder.getGraphQLScalarType((Class<?>) theType);
					if(basicType != null) {
						basicType = GraphQLList.list(basicType);
					}
					
				} else {
					basicType = GraphqlSchemaTypeBuilder.getGraphQLScalarType(p.getType());
				}
				if(basicType == null) {
					boolean hasHandler = schemaTypeBuilder
						.stream()
						.filter(stb -> stb.canHandle(parameterType, true))
						.map(stb -> Boolean.TRUE)
						.findFirst()
						.orElseGet(() -> defaultBuilder.canHandle(parameterType, true));
					if(hasHandler) {
						parameters.put(parameterName, new ParameterContext(p, (GraphQLInputType) createType(parameterType, typeMapping, true, getParameterAnnotations(p))));
					} else {
						LOG.error("{} parameter {} is a complex type and no handler is available. Thus the Method will be ignored", method, parameterName);
						ignore = true;
					}
				} else {
					parameters.put(parameterName, new ParameterContext(p,(GraphQLInputType) basicType));
				}
			}
			if(!ignore) {
				GraphQLFieldDefinition operation = createOperation(methodName, methodDocumentation, parameters, new DataFetcherImplementation(method), returnType);
				serviceBuilder.field(operation);
			}
		}
		GraphQLObjectType objectType = serviceBuilder.build();
		typeMapping.put(name, objectType);
		return objectType;
	}
	
	/**
	 * @param p
	 * @return
	 */
	private List<Annotation> getParameterAnnotations(Parameter p) {
		return Arrays.asList(p.getAnnotations());
	}

	/**
	 * @param method
	 * @return
	 */
	private List<Annotation> getMethodAnnotations(Method method) {
		return Arrays.asList(method.getAnnotations());
	}

	/**
	 * Looks if the parameter is annotated with {@link GraphqlArgument} and uses this value. If not the parameter name is returned.
	 * @param p the parameter we want the name for
	 * @return
	 */
	private String getParameterName(Parameter p) {
		String name = p.getName();
		GraphqlArgument argAnnotation = p.getAnnotation(GraphqlArgument.class);
		if(argAnnotation != null) {
			return argAnnotation.value();
		}
		return name;
	}

	/**
	 * Looks if the parameter is annotated with {@link GraphqlArgument} and if the value is declared optional. By Default, every parameter is mandatory.
	 * @param p the parameter we want the name for
	 * @return
	 */
	private boolean isParameterOptional(Parameter p) {
		GraphqlArgument argAnnotation = p.getAnnotation(GraphqlArgument.class);
		if(argAnnotation != null) {
			return argAnnotation.optional();
		}
		return false;
	}

	/**
	 * Looks if the parameter is annotated with {@link GraphqlDocumentation} and uses this value. If not the parameter name is returned.
	 * @param p the parameter we want the name for
	 * @return
	 */
	private String getDocumentation(Parameter p) {
		String name = null;
		GraphqlDocumentation argAnnotation = p.getAnnotation(GraphqlDocumentation.class);
		if(argAnnotation != null) {
			return argAnnotation.value();
		}
		return name;
	}

	/**
	 * Looks if the method is annotated with {@link GraphqlDocumentation} and returns this value
	 * @param parameter the parameter we want the name for
	 * @return
	 */
	private String getDocumentation(Method method) {
		String name = null;
		GraphqlDocumentation argAnnotation = method.getAnnotation(GraphqlDocumentation.class);
		if(argAnnotation != null) {
			return argAnnotation.value();
		}
		return name;
	}

	/**
	 * @param clazzType
	 * @param typeMapping2
	 * @return
	 */
	private GraphQLType createType(Type type, Map<String, GraphQLType> typeMapping, boolean inputType, List<Annotation> annotations) {
		GraphqlSchemaTypeBuilder builder = schemaTypeBuilder.stream().filter(stb -> stb.canHandle(type, inputType)).findFirst().orElseGet(() -> defaultBuilder);
		return builder.buildType(type, typeMapping, inputType, annotations);
	}
	
	private GraphQLFieldDefinition createOperation(String name, String methodDocumentation, Map<String, ParameterContext> parameters, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.description(methodDocumentation)
				.dataFetcher(datafetcher)
				.type(type);
		parameters.entrySet().stream().map(e -> this.createArgument(e.getKey(), e.getValue())).forEach(builder::argument);
		return builder.build();
	}
	
	/**
	 * Creates the argument for an Operation
	 * @param name the name of the parameter
	 * @param context the {@link ParameterContext} containing the {@link GraphQLInputType} and the {@link Parameter} 
	 * @return the {@link GraphQLArgument}
	 */
	private GraphQLArgument createArgument(String name, ParameterContext context) {
		GraphQLInputType typeToUse = context.getType();
		switch (context.getParameter().getType().getName()) {
			case "int":
			case "float":
			case "short":
			case "long":
			case "boolean":
			case "byte":
			case "char":
				typeToUse = GraphQLNonNull.nonNull(typeToUse);
				break;
			default:
				if(!isParameterOptional(context.parameter)) {
					typeToUse = GraphQLNonNull.nonNull(typeToUse);
				}
				break;
		}
		
		return GraphQLArgument.newArgument()
				.name(name)
				.description(getDocumentation(context.getParameter()))
				.type(typeToUse)
				.build();
	}
	
	/**
	 * 
	 * @author jalbert
	 * @since 9 Jan 2019
	 */
	private final class DataFetcherImplementation implements DataFetcher<Object> {
		/** method */
		private final Method method;

		/**
		 * Creates a new instance.
		 * @param method
		 */
		private DataFetcherImplementation(Method method) {
			this.method = method;
		}

		@Override
		public Object get(DataFetchingEnvironment environment) throws Exception {
			ServiceObjects<Object> serviceObjects = environment.getSource();
			long start = 0;
			if (LOG.isDebugEnabled()) {
				start = System.currentTimeMillis();
				LOG.debug("calling {} matching to service {} and method {}", environment.getField().getName(),
						serviceObjects.getServiceReference().getClass(), method.getName());
			}

			Parameter[] methodParameters = method.getParameters();

			List<GraphQLArgument> arguments = environment.getFieldDefinition().getArguments();

			Object[] parameters = new Object[method.getParameterCount()];

			for (int i = 0; i < methodParameters.length; i++) {

				Parameter parameter = methodParameters[i];

				if (parameter.getType().equals(DataFetchingEnvironment.class)) {

					parameters[i] = environment;

				} else {

					String parameterName = getParameterName(parameter);

					Object parameterValue = environment.getArguments().get(parameterName);

					if (!(parameterValue instanceof List) && parameter.getType().isInstance(parameterValue)) {

						parameters[i] = parameterValue;

					} else {

						if ((valueConverters != null) && !valueConverters.isEmpty()) {

							// @formatter:off
							GraphQLArgument graphqlArgument = arguments.stream()
									.filter(a -> a.getName().equalsIgnoreCase(parameterName))
									.findFirst()
									.orElse(null);
							// @formatter:on

							if (graphqlArgument != null) {

								Class<?> actualType = getActualType(parameter);

								// @formatter:off
								GeckoGraphQLValueConverter graphqlValueConverter = valueConverters.stream()
										.filter(vc -> vc.canHandle( graphqlArgument.getType(), actualType, parameterValue ))
										.findFirst().orElse(null);
								// @formatter:on								

								if (graphqlValueConverter != null) {
									
									parameters[i] = graphqlValueConverter.convert( actualType, parameterValue );
								}
							}
						}
					}
				}
			}

			Object toInvokeOn = serviceObjects.getService();
			try {
				Object result = method.invoke(toInvokeOn, parameters);
				return result;
			} catch (InvocationTargetException e) {
				throw (Exception) e.getTargetException();
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw e;
			} finally {
				serviceObjects.ungetService(toInvokeOn);
				if (LOG.isDebugEnabled()) {
					LOG.debug("finished {}  after {} ms matching to service {} and method {}",
							environment.getField().getName(), System.currentTimeMillis() - start,
							serviceObjects.getServiceReference().getClass(), method.getName());
				}
			}
		}
		
		/**
		 * Returns actual type, whether a single element or list of elements is used as input.
		 * 
		 * @param parameter
		 * @return actual type
		 */
		private Class<?> getActualType(Parameter parameter) {
			boolean isParameterizedType = (parameter.getParameterizedType() instanceof ParameterizedType);
			boolean isList = List.class.isAssignableFrom(parameter.getType());

			return (isParameterizedType && isList)
					? (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0]
					: parameter.getType();
		}
	}
	
	private static final class ParameterContext {
		private Parameter parameter;
		private GraphQLInputType type;

		public ParameterContext(Parameter parameter, GraphQLInputType type) {
			this.parameter = parameter;
			this.type = type;
		}
		
		/**
		 * Returns the type.
		 * @return the type
		 */
		public GraphQLInputType getType() {
			return type;
		}
		
		/**
		 * Returns the parameter.
		 * @return the parameter
		 */
		public Parameter getParameter() {
			return parameter;
		}
	}	
}
