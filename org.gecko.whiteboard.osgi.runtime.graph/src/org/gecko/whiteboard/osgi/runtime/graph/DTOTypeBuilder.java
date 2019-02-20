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
package org.gecko.whiteboard.osgi.runtime.graph;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.PropertyDataFetcher;

/**
 * 
 * @author jalbert
 * @since 7 Nov 2018
 */
public class DTOTypeBuilder  {

	private Map<Class<?>, GraphQLType> typeMapping;
	
	/**
	 * Creates a new instance.
	 */
	public DTOTypeBuilder(Map<Class<?>, GraphQLType> hashMap) {
		this.typeMapping = hashMap;
	}

	public GraphQLType buildType(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			GraphQLType createType = buildType(parameterizedType.getActualTypeArguments()[0]);
			createType = createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;
			if(Collection.class.isAssignableFrom(rawType)) {
				return GraphQLList.list(createType);
			} else {
				return createType;
			}
		}
		
		Class<?> theClazz = (Class<?>) type;

		if(theClazz.isArray()) {
			Class<?> rawType = theClazz.getComponentType();
			GraphQLType createType = buildType(rawType);
			createType = createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;
			return GraphQLList.list(createType);
		}
		
		GraphQLType scalarType = GraphqlSchemaTypeBuilder.getGraphQLScalarType(theClazz);
		if(scalarType != null) {
			return scalarType;
		}

		String name = ((Class<?>) type).getSimpleName();
		
		GraphQLType result = typeMapping.get(type);
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
			typeMapping.put(clazzType, theEnum);
			return theEnum;
			
		}
		return buildObjectType(clazzType, name);
	}

	/**
	 * @param typeMapping
	 * @param inputType
	 * @param clazzType
	 * @return
	 */
	private GraphQLObjectType buildObjectType(Class<?> clazzType, String name) {
		graphql.schema.GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject()
				.name(name);
		GraphQLObjectType theType = typeBuilder.build();
		typeMapping.put(clazzType, theType);
		typeBuilder = GraphQLObjectType.newObject(theType);
		for(Field f : clazzType.getDeclaredFields()) {
			Type fieldType = f.getGenericType();
			String fieldName = f.getName();
			GraphQLType createType = buildType(fieldType);
			typeBuilder.field(createField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		GraphQLObjectType objectType = typeBuilder.build();
		typeMapping.put(clazzType, objectType);
		return objectType;
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
}
