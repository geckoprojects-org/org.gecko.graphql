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
package org.gecko.whiteboard.graphql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gecko.whiteboard.graphql.schema.GeckoGraphQLDateScalar;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.scalars.ExtendedScalars;

/**
 * 
 * @author Juergen Albert
 * @since 7 Nov 2018
 */
public interface GraphqlSchemaTypeBuilder {

	public boolean canHandle(Type type, boolean inputType);

	public GraphQLType buildType(Type type, Map<String, GraphQLType> typeMapping, boolean inputType,
			List<Annotation> annotations);

	public static GraphQLType getGraphQLScalarType(Class<?> instanceClass) {
		if (instanceClass == Integer.TYPE || instanceClass == Integer.class) {
			return Scalars.GraphQLInt;
		} else if (instanceClass == Float.TYPE || instanceClass == Float.class || instanceClass == Double.TYPE
				|| instanceClass == Double.class) {
			return Scalars.GraphQLFloat;
		} else if (instanceClass == BigInteger.class) {
			return ExtendedScalars.GraphQLBigInteger;
		} else if (instanceClass == Short.TYPE || instanceClass == Short.class) {
			return ExtendedScalars.GraphQLShort;
		} else if (instanceClass == Long.TYPE || instanceClass == Long.class) {
			return ExtendedScalars.GraphQLLong;
		} else if (instanceClass == Boolean.TYPE || instanceClass == Boolean.class) {
			return Scalars.GraphQLBoolean;
		} else if (instanceClass == Byte.TYPE || instanceClass == Byte.class) {
			return ExtendedScalars.GraphQLByte;
		} else if (instanceClass == Character.TYPE || instanceClass == Character.class) {
			return ExtendedScalars.GraphQLChar;
		} else if (String.class == instanceClass) {
			return Scalars.GraphQLString;
		} else if (Date.class == instanceClass) {
			return GeckoGraphQLDateScalar.INSTANCE;
		} else if ((java.time.OffsetDateTime.class == instanceClass)
				|| java.time.ZonedDateTime.class == instanceClass) {
			return ExtendedScalars.DateTime;
		} else if (java.time.LocalDate.class == instanceClass) {
			return ExtendedScalars.Date;
		} else if (java.time.OffsetTime.class == instanceClass) {
			return ExtendedScalars.Time;
		} else if (java.time.LocalTime.class == instanceClass) {
			return ExtendedScalars.LocalTime;
		}

		return null;
	}

	public static GraphQLFieldDefinition createReferenceField(String name, DataFetcher<?> datafetcher,
			GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(name)
				.dataFetcher(datafetcher).type(type);
		return builder.build();
	}

	public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
		List<Class<?>> result = new LinkedList<>();
		result.addAll(Arrays.asList(clazz.getInterfaces()));
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			result.addAll(getAllInterfaces(superclass));
		}
		return result;
	}
}
