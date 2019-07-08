package org.gecko.graphql.annotation;

import org.osgi.annotation.bundle.Capability;

@Capability(
		namespace = GraphqlEndpoint.NAMESPACE,
		name = GraphqlEndpoint.NAME,
		version = GraphqlEndpoint.VERSION)
public @interface GraphqlEndpoint {

	public static final String NAMESPACE = "graphql";
	public static final String NAME = "endpoint";
	public static final String VERSION = "12.0";
}
