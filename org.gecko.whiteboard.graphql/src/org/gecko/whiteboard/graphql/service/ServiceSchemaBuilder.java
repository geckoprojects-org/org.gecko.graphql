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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.annotation.GraphqlDocumentation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public class ServiceSchemaBuilder {

	private final ServiceReference<Object> serviceReference;
	private final ServiceObjects<Object> serviceObjects;
	private final Builder queryTypeBuilder;
	private final Builder mutationTypeBuilder;
	private final Set<GraphQLType> types;
	
	private final Map<Object, GraphQLType> typeMapping = new HashMap<Object, GraphQLType>();
	private final List<GraphqlSchemaTypeBuilder> schemaTypeBuilder = new LinkedList<>();
	private final GraphqlSchemaTypeBuilder defaultBuilder = new DefaultGraphqlTypeBuilder();
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceSchemaBuilder.class);
	
	private BundleContext ctx = FrameworkUtil.getBundle(getClass()).getBundleContext();
	
	/**
	 * Creates a new instance.
	 * @param serviceReference
	 * @param serviceObjects
	 * @param queryTypeBuilder
	 * @param mutationTypeBuilder 
	 * @param types
	 * @param typeBuilder 
	 */
	public ServiceSchemaBuilder(ServiceReference<Object> serviceReference, ServiceObjects<Object> serviceObjects, Builder queryTypeBuilder,
			Builder mutationTypeBuilder, Set<GraphQLType> types, List<GraphqlSchemaTypeBuilder> typeBuilder) {
				this.serviceReference = serviceReference;
				this.serviceObjects = serviceObjects;
				this.queryTypeBuilder = queryTypeBuilder;
				this.mutationTypeBuilder = mutationTypeBuilder;
				this.types = types;
				types.forEach(type -> typeMapping.put(type.getName(), type));
				schemaTypeBuilder.addAll(typeBuilder);
	}

	/**
	 * Builds the query and mutation Schema
	 */
	public void build() {
		Object service = ctx.getService(serviceReference);
		try {
			List<Class<?>> interfaces = GraphqlSchemaTypeBuilder.getAllInterfaces(service.getClass());
//			List<Class<?>> interfaces = getServiceInterfaces(serviceReference);
			
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
	 * Decides if the Interface should be handled as a Query 
	 * @param curInterface
	 * @param serviceReference2
	 * @return
	 */
	private boolean isDeclaredQueryInterface(Class<?> curInterface, ServiceReference<Object> serviceReference) {
		return isDeclaredInterfaceForProperty(curInterface, serviceReference, GeckoGraphQLConstants.GRAPHQL_WHITEBOARD_QUERY_SERVICE);
	}

	/**
	 * Decides if the Interface should be handled as a Mutation 
	 * @param curInterface
	 * @param serviceReference
	 * @return
	 */
	private boolean isDeclaredMutationInterface(Class<?> curInterface, ServiceReference<Object> serviceReference) {
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
			return false;
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
		if(interfaces[0].equals("*")) {
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
		if(name == null) {
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
		
		if(name == null) {
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
	private GraphQLObjectType createService(String name, Class<?> curInterface, Map<Object, GraphQLType> typeMapping) {
		GraphQLType existingType = typeMapping.get(name);
		graphql.schema.GraphQLObjectType.Builder serviceBuilder = null;
		if(existingType != null) {
			serviceBuilder = GraphQLObjectType.newObject((GraphQLObjectType) existingType);
		} else {
			serviceBuilder = GraphQLObjectType.newObject().name(name);
		}
		for(Method method : curInterface.getMethods()) {
			String methodName = method.getName();
			
			GraphQLOutputType returnType = (GraphQLOutputType) createType(method.getGenericReturnType(), typeMapping, false);
			Map<String, GraphQLInputType> parameters = new HashMap<String, GraphQLInputType>();
			boolean ignore = false;
			String methodDocumentation = getDocumentation(method);
			for(Parameter p : method.getParameters()) {
				if(p.getType().equals(DataFetchingEnvironment.class)) {
					continue;
				}
				String parameterName = getParameterName(p);
				Class<?> parameterType = p.getType();
					GraphQLType basicType = GraphqlSchemaTypeBuilder.getGraphQLScalarType(parameterType);
					if(basicType == null) {
						boolean hasHandler = schemaTypeBuilder
							.stream()
							.filter(stb -> stb.canHandle(parameterType, true))
							.map(stb -> Boolean.TRUE)
							.findFirst()
							.orElseGet(() -> defaultBuilder.canHandle(parameterType, true));
						if(hasHandler) {
							parameters.put(parameterName, (GraphQLInputType) createType(parameterType, typeMapping, true));
						} else {
							LOG.error("{} parameter {} is a complex type and no handler is available. Thus the Method will be ignored", method, parameterName);
							ignore = true;
						}
					} else {
						parameters.put(parameterName, (GraphQLInputType) basicType);
					}
			}
			if(!ignore) {
				GraphQLFieldDefinition operation = createOperation(methodName, methodDocumentation, parameters, new DataFetcher<Object>() {

					@Override
					public Object get(DataFetchingEnvironment environment) {
						ServiceObjects<?> serviceObjects = environment.getSource();
						Object[] parameters = new Object[method.getParameterCount()];
						for (int i = 0; i < method.getParameters().length; i++) {
							Parameter parameter = method.getParameters()[i];
							if(parameter.getType().equals(DataFetchingEnvironment.class)) {
								parameters[i] = environment;
							} else {
								parameters[i] = environment.getArguments().get(getParameterName(parameter));
							}
						}
						Object toInvokeOn = serviceObjects.getService();
						try {
							Object result = method.invoke(toInvokeOn, parameters);
							return result;
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						} finally {
							ServiceSchemaBuilder.this.serviceObjects.ungetService(toInvokeOn);
						}
						return null;
					}
				}, returnType);
				serviceBuilder.field(operation);
			}
		}
		GraphQLObjectType objectType = serviceBuilder.build();
		typeMapping.put(name, objectType);
		return objectType;
	
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
	 * @param p the parameter we want the name for
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
	private GraphQLType createType(Type type, Map<Object, GraphQLType> typeMapping, boolean inputType) {
		GraphqlSchemaTypeBuilder builder = schemaTypeBuilder.stream().filter(stb -> stb.canHandle(type, inputType)).findFirst().orElseGet(() -> defaultBuilder);
		return builder.buildType(type, typeMapping, inputType);
	}
	
	
	
	private GraphQLFieldDefinition createOperation(String name, String methodDocumentation, Map<String, GraphQLInputType> parameters, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.description(methodDocumentation)
				.dataFetcher(datafetcher)
				.type(type);
		parameters.entrySet().stream().map(e -> this.createArgument(e.getKey(), e.getValue())).forEach(builder::argument);
		return builder.build();
	}
	
	private GraphQLArgument createArgument(String name, GraphQLInputType type) {
		GraphQLInputType typeToUse = type;
		if(type instanceof GraphQLScalarType) {
			switch (type.getName()) {
				case "Int":
				case "Float":
				case "Short":
				case "Long":
				case "Boolean":
				case "Byte":
				case "Char":
					typeToUse = GraphQLNonNull.nonNull(type);
				default:
					break;
			}
		}
		return GraphQLArgument.newArgument()
				.name(name)
				.type(typeToUse)
				.build();
		
	}
}
