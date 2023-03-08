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
package org.gecko.whiteboard.graphql.schema;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * 
 * @author jalbert
 * @since 21 Nov 2018
 */
public class GeckoGraphQLDateScalar {
	public static final GraphQLScalarType INSTANCE;

	private static final DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private GeckoGraphQLDateScalar() {}

	static {

		Coercing<Date, String> coercing = new Coercing<Date, String>() {

			@Override
			public String serialize(Object input) throws CoercingSerializeException {
				if (!(input instanceof Date)) {
					throw new CoercingSerializeException(
							"Expected type 'java.util.Date' but was '" + typeName(input) + "'.");
				}
				return formater.format(input);
			}

			@Override
			public Date parseValue(Object input) throws CoercingParseValueException {
				Date result = convertImpl(input);
				if (result == null) {
					throw new CoercingParseValueException(
							"Expected type 'java.util.Date' but was '" + typeName(input) + "'.");
				}
				return result;
			}

			@Override
			public Date parseLiteral(Object input) throws CoercingParseLiteralException {
				if (!(input instanceof StringValue || input instanceof IntValue)) {
					throw new CoercingParseLiteralException(
							"Expected AST type 'StringValue' or 'IntValue' but was '" + typeName(input) + "'.");
				}
				if (input instanceof StringValue) {
					return convertImpl(((StringValue) input).getValue());
				} else {
					return convertImpl(((IntValue) input).getValue());
				}
			}

			private Date convertImpl(Object input) {
				if (input instanceof Date) {
					return (Date) input;
				} else if (input instanceof String) {
					try {
						return formater.parse(input.toString());
					} catch (ParseException e) {
						throw new CoercingParseLiteralException("The Date String " + input.toString()
								+ " appeared not to be of the expected format yyyy-MM-dd'T'HH:mm:ss.SSSZ");
					}
				} else if (input instanceof Number) {
					BigDecimal value;
					try {
						value = new BigDecimal(input.toString());
					} catch (NumberFormatException e) {
						throw new CoercingParseLiteralException("Internal error: should never happen");
					}
					return new Date(value.longValue());
				} else {
					return null;
				}

			}

			private String typeName(Object input) {
				if (input == null) {
					return "null";
				}

				return input.getClass().getSimpleName();
			}
		};

		INSTANCE = GraphQLScalarType.newScalar().name("Date").description(
				"Built-in Date with the following pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ an example would look like this: 2018-11-18T13:45:00.000+0100")
				.coercing(coercing).build();
	}
}
