package org.gecko.graphql.osgi.framework;

import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

public class FrameworkTypes {

	public static final GraphQLOutputType MAP = Scalars.GraphQLString;

	public static final GraphQLOutputType BUNDLE = GraphQLObjectType.newObject()
			.name(BundleDTO.class.getSimpleName())
			.description("")
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("id")
							.description("The bundle's unique identifier.")
							.type(Scalars.GraphQLLong)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("lastModified")
							.description("The time when the bundle was last modified.")
							.type(Scalars.GraphQLLong)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("state")
							.description("The bundle's state.")
							.type(Scalars.GraphQLInt)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("symbolicName")
							.description("The bundle's symbolic name.")
							.type(Scalars.GraphQLString)
							.build())

			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("version")
							.description("The bundle's version.")
							.type(Scalars.GraphQLString)
							.build())
			.build();

	public static final GraphQLOutputType SERVICE_REFERENCE = GraphQLObjectType.newObject()
			.name(ServiceReferenceDTO.class.getSimpleName())
			.description("")
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("id")
							.description("The id of the service.")
							.type(Scalars.GraphQLLong)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("bundle")
							.description("The id of the bundle that registered the service.")
							.type(Scalars.GraphQLLong)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("properties")
							.description("The properties for the service.")
							.type(MAP)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("usingBundles")
							.description("The ids of the bundles that are using the service.")
							.type(GraphQLList.list(Scalars.GraphQLString))
							.build())
			.build();

	public static final GraphQLOutputType FRAMEWORK = GraphQLObjectType.newObject()
			.name(FrameworkDTO.class.getSimpleName())
			.description("Data Transfer Object for a Framework.")
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("bundles")
							.description("The bundles that are installed in the framework.")
							.type(GraphQLList.list(BUNDLE))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("properties")
							.description("The launch properties of the framework.")
							.type(MAP)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name("services")
							.description("The services that are registered in the framework.")
							.type(GraphQLList.list(SERVICE_REFERENCE))
							.build())
			.build();

}
