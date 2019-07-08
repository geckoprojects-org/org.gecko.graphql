package org.gecko.graphql;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import graphql.Scalars;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * Additional scalar GraphQL types.
 * <p>
 * Containes static {@link GraphQLScalarType} implementations for frequently used Java classes which are not covered by {@link Scalars}, e.g.
 * {@link java.util.UUID} and classes from {@link java.time}.
 * </p>
 *
 * @see Scalars
 * @author Frank Rosenberger, 2019
 */
public class ScalarTypes {

	public static final GraphQLScalarType LOCAL_DATE_ISO_8601 = GraphQLScalarType.newScalar()
			.name(LocalDate.class.getSimpleName())
			.coercing(new Coercing<LocalDate, String>() {
				@Override
				public LocalDate parseLiteral(Object literal) throws CoercingParseLiteralException {
					if (literal instanceof StringValue) {
						try {
							return LocalDate.parse(((StringValue) literal).getValue());
						} catch (final DateTimeParseException dtpe) {
							throw new CoercingParseLiteralException(dtpe);
						}
					} else {
						throw new CoercingParseLiteralException("Unable to parse as a LocalDate literal: " + literal);
					}
				}


				@Override
				public LocalDate parseValue(Object value) throws CoercingParseValueException {
					try {
						return LocalDate.parse(String.valueOf(value));
					} catch (final DateTimeParseException dtpe) {
						throw new CoercingParseLiteralException(dtpe);
					}
				}


				@Override
				public String serialize(Object localDate) throws CoercingSerializeException {
					if (localDate instanceof LocalDate) {
						return localDate.toString();
					} else {
						throw new CoercingSerializeException("Unable to serialize as LocalDate: " + localDate);
					}
				}
			})
			.build();

	public static final GraphQLScalarType UUID = GraphQLScalarType.newScalar()
			.name(java.util.UUID.class.getSimpleName())
			.coercing(new Coercing<java.util.UUID, String>() {
				@Override
				public java.util.UUID parseLiteral(Object literal) throws CoercingParseLiteralException {
					if (literal instanceof StringValue) {
						try {
							return java.util.UUID.fromString(((StringValue) literal).getValue());
						} catch (final IllegalArgumentException iae) {
							throw new CoercingParseLiteralException(iae);
						}
					} else {
						throw new CoercingParseLiteralException("Unable to parse as a UUID literal: " + literal);
					}
				}


				@Override
				public java.util.UUID parseValue(Object value) throws CoercingParseValueException {
					try {
						return java.util.UUID.fromString(String.valueOf(value));
					} catch (final IllegalArgumentException iae) {
						throw new CoercingParseLiteralException(iae);
					}
				}


				@Override
				public String serialize(Object uuid) throws CoercingSerializeException {
					if (uuid instanceof java.util.UUID) {
						return uuid.toString();
					} else {
						throw new CoercingSerializeException("Unable to serialize as UUID: " + uuid);
					}
				}
			})
			.build();
}
