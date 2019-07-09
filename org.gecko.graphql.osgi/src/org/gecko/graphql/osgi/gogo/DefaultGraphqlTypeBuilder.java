package org.gecko.graphql.osgi.gogo;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import graphql.schema.DataFetcher;
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

public class DefaultGraphqlTypeBuilder implements GraphqlSchemaTypeBuilder {

	/*
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#canHandle(java.lang.reflect.Type)
	 */
	@Override
	public boolean canHandle(Type type, boolean inputType) {
		return !inputType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#buildType(java.lang.reflect.Type, java.util.Map, boolean)
	 */
	@Override
	public GraphQLType buildType(Type type, Map<Object, GraphQLType> typeMapping, boolean inputType) {
		if(type instanceof ParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) type;
			final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			GraphQLType createType = buildType(parameterizedType.getActualTypeArguments()[0], typeMapping, inputType);
			createType = createType instanceof GraphQLObjectType  ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;
			if(Collection.class.isAssignableFrom(rawType)) {
				return GraphQLList.list(createType);
			} else {
				return createType;
			}
		}
		// new
		if (((Class<?>) type).isArray()) {
			GraphQLType createType = buildType(((Class<?>) type).getComponentType(), typeMapping, inputType);
			createType = createType instanceof GraphQLObjectType ? GraphQLTypeReference.typeRef(((GraphQLObjectType) createType).getName()) : createType;

			return GraphQLList.list(createType);
		}

		final GraphQLType scalarType = GraphqlSchemaTypeBuilder.getGraphQLScalarType((Class<?>) type);
		if(scalarType != null) {
			return scalarType;
		}

		String name = ((Class<?>) type).getSimpleName();
		if(inputType) {
			name += "Input";
		}

		final GraphQLType result = typeMapping.get(name);
		if(result != null) {
			if(result instanceof GraphQLObjectType) {
				return GraphQLTypeReference.typeRef(result.getName());
			}
			return result;
		}

		final Class<?> clazzType = (Class<?>) type;

		if(clazzType.isEnum()) {
			final GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name(clazzType.getSimpleName());
			for (final Object object : clazzType.getEnumConstants()) {
				typeBuilder.value(object.toString());
			}
			final GraphQLEnumType theEnum = typeBuilder.build();
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

		if (name.toLowerCase().equals("void")) {
			System.out.println(1);
		}
		graphql.schema.GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject()
				.name(name);
		final GraphQLObjectType theType = typeBuilder.build();
		typeMapping.put(name, theType);
		typeBuilder = GraphQLObjectType.newObject(theType);
		for(final Field f : clazzType.getDeclaredFields()) {
			final Type fieldType = f.getGenericType();
			final String fieldName = f.getName();

			if (fieldName.toLowerCase().equals("void")) {
				System.out.println(2);
			}
			final GraphQLType createType = buildType(fieldType, typeMapping, inputType);
			typeBuilder.field(createField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		final GraphQLObjectType objectType = typeBuilder.build();
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
		final graphql.schema.GraphQLInputObjectType.Builder typeBuilder = GraphQLInputObjectType.newInputObject()
				.name(name);
		for(final Field f : clazzType.getDeclaredFields()) {
			final Class<?> fieldType = f.getType();
			final String fieldName = f.getName();
			final GraphQLType createType = buildType(fieldType, typeMapping, inputType);
			typeBuilder.field(createInputField(fieldName, new PropertyDataFetcher<String>(fieldName), createType));
		}
		final GraphQLInputObjectType objectType = typeBuilder.build();
		typeMapping.put(name, objectType);
		return objectType;
	}

	/**
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createField(String name, DataFetcher<?> datafetcher, GraphQLType type) {
		final GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
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
		final GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()
				.name(name)
				.type((GraphQLInputType) type);
		return builder.build();
	}




}
