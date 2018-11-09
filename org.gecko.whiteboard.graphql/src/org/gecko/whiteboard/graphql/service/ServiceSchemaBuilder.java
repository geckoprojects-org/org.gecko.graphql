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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public class ServiceSchemaBuilder {

	private ServiceReference<Object> serviceReference;
	private ServiceObjects<Object> serviceObjects;
	private Builder queryTypeBuilder;
	private Set<GraphQLType> types;
	
	private Map<Object, GraphQLType> typeMapping = new HashMap<Object, GraphQLType>();
	private List<GraphqlSchemaTypeBuilder> schemaTypeBuilder = new LinkedList<>();
	private GraphqlSchemaTypeBuilder defaultBuilder = new DefaultGraphqlTypeBuilder();
	
	/**
	 * Creates a new instance.
	 * @param serviceReference
	 * @param serviceObjects
	 * @param queryTypeBuilder
	 * @param types
	 * @param typeBuilder 
	 */
	public ServiceSchemaBuilder(ServiceReference<Object> serviceReference, ServiceObjects<Object> serviceObjects, Builder queryTypeBuilder,
			Set<GraphQLType> types, List<GraphqlSchemaTypeBuilder> typeBuilder) {
				this.serviceReference = serviceReference;
				this.serviceObjects = serviceObjects;
				this.queryTypeBuilder = queryTypeBuilder;
				this.types = types;
				types.forEach(type -> typeMapping.put(type.getName(), type));
				schemaTypeBuilder.addAll(typeBuilder);
	}

	/**
	 * @return
	 */
	public void build() {
		Object service = serviceObjects.getService();
		try {
			List<Class<?>> interfaces = GraphqlSchemaTypeBuilder.getAllInterfaces(service.getClass());
			for(Class<?> curInterface : interfaces) {
				if(isDeclaredInterface(curInterface, serviceReference)) {
					String name = getServiceName(serviceReference, curInterface);
					queryTypeBuilder.field(GraphqlSchemaTypeBuilder.createReferenceField(name, new StaticDataFetcher(serviceObjects), createService(name, curInterface, typeMapping)));
				}
			}
			types.addAll(typeMapping.values());
		} catch (Throwable e) {
			System.err.println("Args... " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			serviceObjects.ungetService(service);
		}
	}
	
	/**
	 * @param curInterface
	 * @param serviceReference2
	 * @return
	 */
	private boolean isDeclaredInterface(Class<?> curInterface, ServiceReference<Object> serviceReference) {
		String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
		
		for(String objectClass : objectClasses) {
			if(curInterface.getName().equals(objectClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param serviceReference2
	 * @return
	 */
	private String getServiceName(ServiceReference<Object> serviceReference, Class<?> theInterface) {
		String name = (String) serviceReference.getProperty(GeckoGraphQLConstants.GRAPHQL_SERVICE_NAME);
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
			for(Parameter p : method.getParameters()) {
				String parameterName = getParameterName(p);
				Class<?> parameterType = p.getType();
					GraphQLType basicType = GraphqlSchemaTypeBuilder.getGraphQLScalarType(parameterType);
					if(basicType == null) {
						//TODO: Add logger here and DTO here
						System.err.println(methodName + " parameter " + parameterName + " is a complex type, which is not allowed for query objects. Thus the Method will be ignored");
						ignore = true;
					}
					parameters.put(parameterName, (GraphQLInputType) createType(parameterType, typeMapping, true));
			}
			if(!ignore) {
				GraphQLFieldDefinition operation = createOperation(methodName, parameters, new DataFetcher<Object>() {

					@Override
					public Object get(DataFetchingEnvironment environment) {
						ServiceObjects<?> serviceObjects = environment.getSource();
						Object[] parameters = new Object[method.getParameterCount()];
						for (int i = 0; i < method.getParameters().length; i++) {
							Parameter parameter = method.getParameters()[i];
							parameters[i] = environment.getArguments().get(parameter.getName());
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
	 * @param p
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
	 * @param clazzType
	 * @param typeMapping2
	 * @return
	 */
	private GraphQLType createType(Type type, Map<Object, GraphQLType> typeMapping, boolean inputType) {
		GraphqlSchemaTypeBuilder builder = schemaTypeBuilder.stream().filter(stb -> stb.canHandle(type)).findFirst().orElseGet(() -> defaultBuilder);
		return builder.buildType(type, typeMapping, inputType);
	}
	
	private GraphQLFieldDefinition createOperation(String name, Map<String, GraphQLInputType> parameters, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.dataFetcher(datafetcher)
				.type(type);
		parameters.entrySet().stream().map(e -> this.createArgument(e.getKey(), e.getValue())).forEach(builder::argument);
		return builder.build();
	}
	
	private GraphQLArgument createArgument(String name, GraphQLInputType type) {
		return GraphQLArgument.newArgument()
				.name(name)
				.type(type)
				.build();
		
	}
//		if(type instanceof ParameterizedType) {
//			ParameterizedType parameterizedType = (ParameterizedType) type;
//			GraphQLType createType = createType(parameterizedType.getActualTypeArguments()[0], typeMapping, inputType);
//			createType = createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;
//			if(type instanceof Collection) {
//				return GraphQLList.list(createType);
//			} else {
//				return createType;
//			}
//		}
//
//		GraphQLType scalarType = getGraphQLScalarType((Class<?>) type);
//		if(scalarType != null) {
//			return scalarType;
//		}
//
//		String name = ((Class<?>) type).getSimpleName();
//		if(inputType) {
//			name += "Input";
//		}
//		
//		GraphQLType result = typeMapping.get(name);
//		if(result != null) {
//			if(result instanceof GraphQLObjectType) {
//				return GraphQLTypeReference.typeRef(result.getName());
//			}
//			return result;
//		}
//		
//		Class<?> clazzType = (Class<?>) type;
//
//		if(clazzType.isEnum()) {
//			GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name(clazzType.getSimpleName());
//			for (Object object : clazzType.getEnumConstants()) {
//				typeBuilder.value(object.toString());
//			}
//			GraphQLEnumType theEnum = typeBuilder.build();
//			typeMapping.put(clazzType.getSimpleName(), theEnum);
//			return theEnum;
//			
//		}
//		if(!inputType) {
//			buildObjectType(typeMapping, inputType, clazzType, name);
//		} else {
//			buildInputObjectType(typeMapping, inputType, clazzType, name);
//		}
//		return GraphQLTypeReference.typeRef(name);
//	}

//	/**
//	 * @param typeMapping
//	 * @param inputType
//	 * @param clazzType
//	 * @return
//	 */
//	private GraphQLObjectType buildObjectType(Map<Object, GraphQLType> typeMapping, boolean inputType,
//			Class<?> clazzType, String name) {
//		graphql.schema.GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject()
//				.name(name);
//		for(Field f : clazzType.getDeclaredFields()) {
//			Class<?> fieldType = f.getType();
//			String fieldName = f.getName();
//			GraphQLType createType = createType(fieldType, typeMapping, inputType);
//			typeBuilder.field(createField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
//		}
//		GraphQLObjectType objectType = typeBuilder.build();
//		typeMapping.put(name, objectType);
//		return objectType;
//	}
//
//	/**
//	 * @param typeMapping
//	 * @param inputType
//	 * @param clazzType
//	 * @return
//	 */
//	private GraphQLInputObjectType buildInputObjectType(Map<Object, GraphQLType> typeMapping, boolean inputType,
//			Class<?> clazzType, String name) {
//		graphql.schema.GraphQLInputObjectType.Builder typeBuilder = GraphQLInputObjectType.newInputObject()
//				.name(name);
//		for(Field f : clazzType.getDeclaredFields()) {
//			Class<?> fieldType = f.getType();
//			String fieldName = f.getName();
//			GraphQLType createType = createType(fieldType, typeMapping, inputType);
//			typeBuilder.field(createInputField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
//		}
//		GraphQLInputObjectType objectType = typeBuilder.build();
//		typeMapping.put(name, objectType);
//		return objectType;
//	}
//
//	/**
//	 * @param string
//	 * @return
//	 */
//	private GraphQLFieldDefinition createField(String name, DataFetcher<?> datafetcher, GraphQLType type) {
//		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
//			.name(name)
//			.dataFetcher(datafetcher)
//			.type((GraphQLOutputType) type);
//		return builder.build();
//	}
//
//	/**
//	 * @param string
//	 * @return
//	 */
//	private GraphQLInputObjectField createInputField(String name, DataFetcher<?> datafetcher, GraphQLType type) {
//		GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()
//				.name(name)
//				.type((GraphQLInputType) type);
//		return builder.build();
//	}
//
//	/**
//	 * @param string
//	 * @return
//	 */
//	private GraphQLFieldDefinition createReferenceField(String name, DataFetcher<?> datafetcher, GraphQLOutputType type) {
//		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
//				.name(name)
//				.dataFetcher(datafetcher)
//				.type(type);
//		return builder.build();
//	}
//

//
//	static GraphQLType getGraphQLScalarType(Class<?> instanceClass) {
//		if (instanceClass == Integer.TYPE || instanceClass == Integer.class || instanceClass == Long.TYPE || instanceClass == Long.class) {
//			return Scalars.GraphQLInt;
//		} else if (instanceClass == Float.TYPE || instanceClass == Float.class || instanceClass == Double.TYPE || instanceClass == Double.class) {
//			return Scalars.GraphQLFloat;
//		} else if (instanceClass == Boolean.TYPE || instanceClass == Boolean.class) {
//			return Scalars.GraphQLBoolean;
//		} else if (String.class == instanceClass) {
//			return Scalars.GraphQLString;
//		}
//		return null;
//	}
}
