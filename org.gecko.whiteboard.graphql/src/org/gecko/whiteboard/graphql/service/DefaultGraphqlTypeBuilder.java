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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
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
public class DefaultGraphqlTypeBuilder implements GraphqlSchemaTypeBuilder {

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#canHandle(java.lang.reflect.Type)
	 */
	@Override
	public boolean canHandle(Type type) {
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#buildType(java.lang.reflect.Type, java.util.Map, boolean)
	 */
	@Override
	public GraphQLType buildType(Type type, Map<Object, GraphQLType> typeMapping, boolean inputType) {
		if(type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			GraphQLType createType = buildType(parameterizedType.getActualTypeArguments()[0], typeMapping, inputType);
			createType = createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;
			if(Collection.class.isAssignableFrom(rawType)) {
				return GraphQLList.list(createType);
			} else {
				return createType;
			}
		}

		GraphQLType scalarType = getGraphQLScalarType((Class<?>) type);
		if(scalarType != null) {
			return scalarType;
		}

		String name = ((Class<?>) type).getSimpleName();
		if(inputType) {
			name += "Input";
		}
		
		GraphQLType result = typeMapping.get(name);
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
		if(!inputType) {
			buildObjectType(typeMapping, inputType, clazzType, name);
		} else {
			buildInputObjectType(typeMapping, inputType, clazzType, name);
		}
		return GraphQLTypeReference.typeRef(name);
	}

	/**
	 * @param typeMapping
	 * @param inputType
	 * @param clazzType
	 * @return
	 */
	private GraphQLObjectType buildObjectType(Map<Object, GraphQLType> typeMapping, boolean inputType,
			Class<?> clazzType, String name) {
		graphql.schema.GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject()
				.name(name);
		GraphQLObjectType theType = typeBuilder.build();
		typeMapping.put(name, theType);
		typeBuilder = GraphQLObjectType.newObject(theType);
		for(Field f : clazzType.getDeclaredFields()) {
			Type fieldType = f.getGenericType();
			String fieldName = f.getName();
			GraphQLType createType = buildType(fieldType, typeMapping, inputType);
			typeBuilder.field(createField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		GraphQLObjectType objectType = typeBuilder.build();
		typeMapping.put(name, objectType);
		return objectType;
	}

	/**
	 * @param typeMapping
	 * @param inputType
	 * @param clazzType
	 * @return
	 */
	private GraphQLInputObjectType buildInputObjectType(Map<Object, GraphQLType> typeMapping, boolean inputType,
			Class<?> clazzType, String name) {
		graphql.schema.GraphQLInputObjectType.Builder typeBuilder = GraphQLInputObjectType.newInputObject()
				.name(name);
		for(Field f : clazzType.getDeclaredFields()) {
			Class<?> fieldType = f.getType();
			String fieldName = f.getName();
			GraphQLType createType = buildType(fieldType, typeMapping, inputType);
			typeBuilder.field(createInputField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		GraphQLInputObjectType objectType = typeBuilder.build();
		typeMapping.put(name, objectType);
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

	/**
	 * @param string
	 * @return
	 */
	private GraphQLInputObjectField createInputField(String name, DataFetcher<?> datafetcher, GraphQLType type) {
		GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()
				.name(name)
				.type((GraphQLInputType) type);
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
