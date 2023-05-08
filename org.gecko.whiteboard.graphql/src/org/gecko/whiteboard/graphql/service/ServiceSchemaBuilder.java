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
package org.gecko.whiteboard.graphql.service;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;

/**
 * Builds the schema out of a Service reference
 * @author Juergen Albert
 * @since 2 Nov 2018
 */
public class ServiceSchemaBuilder {
	private final GraphQLObjectType.Builder queryTypeBuilder;
	private final GraphQLObjectType.Builder mutationTypeBuilder;
	private final Set<GraphQLType> types;
	private final List<GeckoGraphQLValueConverter> valueConverters;
	
	private final Map<String, GraphQLType> typeMapping = new HashMap<String, GraphQLType>();
	private final List<GraphqlSchemaTypeBuilder> schemaTypeBuilder = new LinkedList<>();
	private final GraphqlSchemaTypeBuilder defaultBuilder = new DefaultGraphqlTypeBuilder();
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceSchemaBuilder.class);
	
	private BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();
	
	public ServiceSchemaBuilder(
			GraphQLObjectType.Builder queryTypeBuilder,
			GraphQLObjectType.Builder mutationTypeBuilder, 
			Set<GraphQLType> types, 
			List<GraphqlSchemaTypeBuilder> typeBuilder, 
			List<GeckoGraphQLValueConverter> valueConverters) {
		this.queryTypeBuilder = queryTypeBuilder;
		this.mutationTypeBuilder = mutationTypeBuilder;
		this.types = types;
		this.valueConverters = valueConverters;
		types.forEach(type -> typeMapping.put(GeckoGraphQLUtil.INSTANCE.getTypeName(type), type));
		schemaTypeBuilder.addAll(typeBuilder);
	}	
	
//	/**
//	 * Builds the query and mutation Schema
//	 */
//	public void build() {
//		build(serviceReference, serviceObjects);
//	}

	public void build(Map.Entry<ServiceReference<Object>, ServiceObjects<Object>> entry) {
		build(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Builds the query and mutation Schema
	 */
	public void build(ServiceReference<Object> serviceReference, ServiceObjects<Object> serviceObjects) {
//		Object service = ctx.getService(serviceReference);
		try {
//			List<Class<?>> interfaces = GraphqlSchemaTypeBuilder.getAllInterfaces(service.getClass());
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

		@SuppressWarnings("unchecked")
		@Override
		public Object get(DataFetchingEnvironment environment) throws Exception {
			ServiceObjects<Object> serviceObjects = environment.getSource();
			long start = 0;
			if(LOG.isDebugEnabled()) {
				start = System.currentTimeMillis();
				LOG.debug("calling {} matching to service {} and method {}", 
						environment.getField().getName(), 
						serviceObjects.getServiceReference().getClass(), 
						method.getName());
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

					if (parameter.getType().isInstance(parameterValue)) {

						parameters[i] = parameterValue;

					} else {

						if ((valueConverters != null && !valueConverters.isEmpty())
								&& (parameterValue instanceof Map)) {
							// @formatter:off
							GraphQLArgument graphqlArgument = arguments.stream()
									.filter(a -> a.getName().equalsIgnoreCase(parameterName))
									.findFirst()
									.orElse(null);
							// @formatter:on

							if (graphqlArgument != null) {

								// @formatter:off
								GeckoGraphQLValueConverter graphqlValueConverter = valueConverters.stream()
										.filter(vc -> vc.canHandle( graphqlArgument.getType(), parameter.getType() ))
										.findFirst().orElse(null);
								// @formatter:on

								if (graphqlValueConverter != null) {
									parameters[i] = graphqlValueConverter.convert(graphqlArgument.getType(),
											parameter.getType(), (Map<String, Object>) parameterValue);
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
			} catch (InvocationTargetException  e) {
				throw (Exception) e.getTargetException();
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw e;
			} finally {
				serviceObjects.ungetService(toInvokeOn);
				if(LOG.isDebugEnabled()) {
					LOG.debug("finished {}  after {} ms matching to service {} and method {}", 
							environment.getField().getName(), 
							System.currentTimeMillis() - start , 
							serviceObjects.getServiceReference().getClass(), 
							method.getName());
				}
			}
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
}
