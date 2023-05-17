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

/**
 * A Collection of constants for the GraphQL Whiteboard
 * 
 * @author Juergen Albert
 * @since 5 Nov 2018
 */
public class GeckoGraphQLConstants {

    /** The name of the  Implementation capability*/
	public static final String OSGI_GRAPHQL_CAPABILITY_NAME = "osgi.graphql";

	/** The name of the EMF Implementation capability*/
	public static final String OSGI_EMF_GRAPHQL_CAPABILITY_NAME = "osgi.graphql.emf";
	
	/** The name of the EMF Value Converter Implementation capability*/
	public static final String OSGI_EMF_GRAPHQL_VALUE_CONVERTER_CAPABILITY_NAME = "osgi.graphql.emf.converter";	
	
	/** The name of the Component that can be used in the ConfigurationAdmin */
	public static final String GECKO_GRAPHQL_WHITEBOARD_COMPONENT_NAME = "GeckoGraphQLWhiteboard";
	
	/** the default pattern for the whiteboard servlet, using /graphql/* */
	public static final String DEFAULT_SERVLET_PATTERN = "/graphql/*";
	
	public static final String GRAPHQL_SERVICE_ENDPOINT = "osgi.graphql.endpoint";

	/** Defines a whiteboard target filter, that decides if a service will be applied to the target  */
	public static final String GRAPHQL_WHITEBOARD_TARGET = "osgi.graphql.target";

	public static final String GRAPHQL_QUERY_SERVICE_PREFIX = "osgi.graphql.query.";

	/** Identifies a Graphql  Service, that should act as a query */
	public static final String GRAPHQL_WHITEBOARD_QUERY_SERVICE = GRAPHQL_QUERY_SERVICE_PREFIX + "graphql.query.service";
	
	/** Defines the name of the Service in the GraphQL whiteboard, communicated to the outside world as part of the query or mutation  */
	public static final String GRAPHQL_QUERY_SERVICE_NAME = GRAPHQL_QUERY_SERVICE_PREFIX + "name";

	/** Marks the Service as a possible Query Service */
	public static final String GRAPHQL_QUERY_SERVICE_MARKER = GRAPHQL_QUERY_SERVICE_PREFIX + "marker";
	
	
	public static final String GRAPHQL_MUTATION_SERVICE_PREFIX = "osgi.graphql.mutation.";

	/** Defines the name of the Service in the GraphQL whiteboard, communicated to the outside world as part of the query or mutation  */
	public static final String GRAPHQL_MUTATION_SERVICE_NAME = GRAPHQL_MUTATION_SERVICE_PREFIX + "name";

	/** Marks the Service as a possible Mutation Service */
	public static final String GRAPHQL_MUTATION_SERVICE_MARKER = GRAPHQL_MUTATION_SERVICE_PREFIX + "marker";

	/** Identifies a Graphql  Service, that should act as a mutation */
	public static final String GRAPHQL_WHITEBOARD_MUTATION_SERVICE = GRAPHQL_MUTATION_SERVICE_PREFIX + "graphql.mutation.service";

	/** Enables request tracing in a GraphQL Whiteboard */
	public static final String SERVICE_PROPERTY_TRACING_ENABLED = "osgi.graphql.tracing.enabled";

	/** Prefix for additoinal response headers */
	public static final String SERVICE_PROPERTY_RESPONSE_HEADER_PREFIX = "osgi.graphql.response.header.";
	
	
}
