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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.PropertyDataFetcher;
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

	/**
	 * Creates a new instance.
	 * @param serviceReference
	 * @param serviceObjects
	 * @param queryTypeBuilder
	 * @param types
	 */
	public ServiceSchemaBuilder(ServiceReference<Object> serviceReference, ServiceObjects<Object> serviceObjects, Builder queryTypeBuilder,
			Set<GraphQLType> types) {
				this.serviceReference = serviceReference;
				this.serviceObjects = serviceObjects;
				this.queryTypeBuilder = queryTypeBuilder;
				this.types = types;
				types.forEach(type -> typeMapping.put(type.getName(), type));
	}

	/**
	 * @return
	 */
	public void build() {
		String name = (String) serviceReference.getProperty("graphql.service.name");
		Object service = serviceObjects.getService();
		queryTypeBuilder.field(createReferenceField(name, new StaticDataFetcher(service), createService(name, service)));
		types.addAll(typeMapping.values());
	}
	
	private GraphQLObjectType createService(String name, Object service) {
		graphql.schema.GraphQLObjectType.Builder serviceBuilder = GraphQLObjectType.newObject().name(name);
		Class<?>[] interfaces = service.getClass().getInterfaces();
		for(Class<?> curInterface : interfaces) {
			for(Method method : curInterface.getMethods()) {
				String methodName = method.getName();
				GraphQLOutputType returnType = (GraphQLOutputType) createType(method.getGenericReturnType(), typeMapping);
				
				Map<String, GraphQLInputType> parameters = new HashMap<String, GraphQLInputType>();
				boolean ignore = false;
				for(Parameter p : method.getParameters()) {
					String parameterName = p.getName();
					Class<?> parameterType = p.getType();
					GraphQLType basicType = getGraphQLScalarType(parameterType);
					if(basicType == null) {
						//TODO: Add logger here
						System.err.println(methodName + " parameter " + parameterName + " is a complex type, which is not allowed for query objects. Thus the Method will be ignored");
						ignore = true;
					}
					parameters.put(parameterName, (GraphQLInputType) createType(parameterType, typeMapping));
				}
				if(!ignore) {
					GraphQLFieldDefinition operation = createOperation(methodName, parameters, new DataFetcher<Object>() {
		
						@Override
						public Object get(DataFetchingEnvironment environment) {
							Object toInvokeOn = environment.getSource();
							Object[] parameters = new Object[method.getParameterCount()];
							for (int i = 0; i < method.getParameters().length; i++) {
								Parameter parameter = method.getParameters()[i];
								parameters[i] = environment.getArguments().get(parameter.getName());
							}
							try {
								return method.invoke(toInvokeOn, parameters);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
							return null;
						}
					}, returnType);
					serviceBuilder.field(operation);
				}
			}
		}
		return serviceBuilder.build();
	
	}
	
	/**
	 * @param clazzType
	 * @param typeMapping2
	 * @return
	 */
	private GraphQLType createType(Type type, Map<Object, GraphQLType> typeMapping) {
		if(type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			GraphQLType createType = createType(parameterizedType.getActualTypeArguments()[0], typeMapping);
			return GraphQLList.list(createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType);
		}

		GraphQLType scalarType = getGraphQLScalarType((Class<?>) type);
		if(scalarType != null) {
			return scalarType;
		}

		GraphQLType result = typeMapping.get(((Class<?>) type).getSimpleName());
		if(result != null) {
			if(result instanceof GraphQLObjectType) {
				return GraphQLTypeReference.typeRef(result.getName());
			}
			return result;
		}
		
		Class<?> clazzType = (Class<?>) type;

		if(clazzType.isEnum()) {
			GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name(clazzType.getSimpleName());
			for (Object object : clazzType.getEnumConstants()) {
				typeBuilder.value(object.toString());
			}
			GraphQLEnumType theEnum = typeBuilder.build();
			typeMapping.put(clazzType.getSimpleName(), theEnum);
			return theEnum;
			
		}
		
		graphql.schema.GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject()
				.name(clazzType.getSimpleName());
		for(Field f : clazzType.getDeclaredFields()) {
			Class<?> fieldType = f.getType();
			String fieldName = f.getName();
			GraphQLType createType = createType(fieldType, typeMapping);
			typeBuilder.field(createField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		GraphQLObjectType objectType = typeBuilder.build();
		typeMapping.put(clazzType.getSimpleName(), objectType);
		return GraphQLTypeReference.typeRef(objectType.getName());
	}

	/**
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createField(String name, DataFetcher<?> datafetcher, GraphQLType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
			.name(name)
			.dataFetcher(datafetcher)
			.type((GraphQLOutputType) type);
		return builder.build();
	}

	/**
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createReferenceField(String name, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.dataFetcher(datafetcher)
				.type(type);
		return builder.build();
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

	static GraphQLType getGraphQLScalarType(Class<?> instanceClass) {
		if (instanceClass == Integer.TYPE || instanceClass == Integer.class || instanceClass == Long.TYPE || instanceClass == Long.class) {
			return Scalars.GraphQLInt;
		} else if (instanceClass == Float.TYPE || instanceClass == Float.class || instanceClass == Double.TYPE || instanceClass == Double.class) {
			return Scalars.GraphQLFloat;
		} else if (instanceClass == Boolean.TYPE || instanceClass == Boolean.class) {
			return Scalars.GraphQLBoolean;
		} else if (String.class == instanceClass) {
			return Scalars.GraphQLString;
		}
		return null;
	}

}
