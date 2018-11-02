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
package org.gecko.whiteboard.graphql.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gecko.whiteboard.graphql.annotation.RequireGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.test.dto.Address;
import org.gecko.whiteboard.graphql.test.dto.ContactType;
import org.gecko.whiteboard.graphql.test.dto.Person;
import org.gecko.whiteboard.graphql.test.service.api.AddressBookService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldDefinition.Builder;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.StaticDataFetcher;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
//@Component
@RequireGraphQLWhiteboard
public class AddressQueryProvider implements GraphQLTypesProvider, GraphQLQueryProvider {

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	AddressBookService addressBookService;
	private GraphQLEnumType contactEnum;
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.servlet.GraphQLQueryProvider#getQueries()
	 */
	@Override
	public Collection<GraphQLFieldDefinition> getQueries() {
		List<GraphQLFieldDefinition> queryFields = new LinkedList<>();

		queryFields.add(createReferenceField("AddressBooK", new StaticDataFetcher(addressBookService), createAddressBookService()));
		
		return queryFields;
	}
	
	private GraphQLObjectType createAddressBookService() {
		graphql.schema.GraphQLObjectType.Builder addressBookBuilder = GraphQLObjectType.newObject().name("AddressBook");
		addressBookBuilder.field(createReferenceField("getAllAddresses", new DataFetcher<List<Address>>() {

			@Override
			public List<Address> get(DataFetchingEnvironment environment) {
				return ((AddressBookService) environment.getSource()).getAllAdresses();
			}
		}, GraphQLList.list(GraphQLTypeReference.typeRef("Address"))));

		Map<String, GraphQLInputType> parameters = new HashMap<String, GraphQLInputType>();
		parameters.put("street", (GraphQLInputType) getGraphQLScalarType(String.class));
		addressBookBuilder.field(createOperation("getAddressesByStreet", parameters, new DataFetcher<List<Address>>() {
			
			@Override
			public List<Address> get(DataFetchingEnvironment environment) {
				return ((AddressBookService) environment.getSource()).getAdressesByStreet(environment.getArgument("street"));
			}
		}, GraphQLList.list(GraphQLTypeReference.typeRef("Address"))));

		addressBookBuilder.field(createReferenceField("Person", new DataFetcher<List<Person>>() {
			
			@Override
			public List<Person> get(DataFetchingEnvironment environment) {
				return ((AddressBookService) environment.getSource()).getAllPersons();
			}
		}, GraphQLList.list(GraphQLTypeReference.typeRef("Person"))));
		
		return addressBookBuilder.build();
	}

	/* 
	 * (non-Javadoc)
	 * @see graphql.servlet.GraphQLTypesProvider#getTypes()
	 */
	@Override
	public Collection<GraphQLType> getTypes() {
		List<GraphQLType> types = new LinkedList<>();
		
		types.add(createContactTypeEnum());
		types.add(createAddress());
		types.add(createPerson());
		types.add(createContact());
		
		return types;
	}
	
	/**
	 * @return
	 */
	private GraphQLType createContact() {
		graphql.schema.GraphQLObjectType.Builder addressBuilder = GraphQLObjectType.newObject()
				.name("Contact")
				.description("contact person")
				.field(createField("value", new PropertyDataFetcher<String>("value"), String.class))
				.field(createReferenceField("type", new PropertyDataFetcher<String>("type"), contactEnum));
		return addressBuilder.build();
	}
	
	protected GraphQLEnumType createContactTypeEnum() {
		GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name("ContactType");
		typeBuilder.value(ContactType.EMAIL.toString());
		typeBuilder.value(ContactType.LANDLINE.toString());
		typeBuilder.value(ContactType.MOBILE.toString());
		typeBuilder.value(ContactType.OTHER.toString()); 
		contactEnum = typeBuilder.build();
		return contactEnum;
	}

	/**
	 * @return
	 */
	private GraphQLType createPerson() {
		graphql.schema.GraphQLObjectType.Builder addressBuilder = GraphQLObjectType.newObject()
				.name("Person")
				.description("Obtainable person")
				.field(createField("firstName", new PropertyDataFetcher<String>("firstName"), String.class))
				.field(createField("lastName", new PropertyDataFetcher<String>("lastName"), String.class))
				.field(createReferenceField("address", new PropertyDataFetcher<String>("address"), GraphQLTypeReference.typeRef("Address")))
				.field(createReferenceField("contacts", new PropertyDataFetcher<String>("contacts"), GraphQLList.list(GraphQLTypeReference.typeRef("Contact"))));
			return addressBuilder.build();
	}

	/**
	 * @return
	 */
	private GraphQLObjectType createAddress() {
		
		graphql.schema.GraphQLObjectType.Builder addressBuilder = GraphQLObjectType.newObject()
			.name("Address")
			.description("Obtainable addresse")
			.field(createReferenceField("inhabitants", new PropertyDataFetcher<String>("inhabitants"), GraphQLList.list(GraphQLTypeReference.typeRef("Person"))))
			.field(createField("number", new PropertyDataFetcher<String>("number"), String.class))
			.field(createField("street", new PropertyDataFetcher<String>("street"), String.class))
			.field(createField("zipCode", new PropertyDataFetcher<String>("zipCode"), String.class))
			.field(createField("city", new PropertyDataFetcher<String>("city"), String.class));
		return addressBuilder.build();
	}

	/**
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createField(String name, DataFetcher<?> datafetcher, Class<?> type) {
		Builder builder = GraphQLFieldDefinition.newFieldDefinition()
			.name(name)
			.dataFetcher(datafetcher)
			.type((GraphQLOutputType) getGraphQLScalarType(type));
		return builder.build();
	}

	/**
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createReferenceField(String name, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.dataFetcher(datafetcher)
				.type(type);
		return builder.build();
	}

	private GraphQLFieldDefinition createOperation(String name, Map<String, GraphQLInputType> parameters, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		Builder builder = GraphQLFieldDefinition.newFieldDefinition()
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
