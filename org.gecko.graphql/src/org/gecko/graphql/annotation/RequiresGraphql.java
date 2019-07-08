package org.gecko.graphql.annotation;

import org.osgi.annotation.bundle.Requirement;

@Requirement(
		namespace = GraphqlEndpoint.NAMESPACE,
		name = GraphqlEndpoint.NAME,
		version = GraphqlEndpoint.VERSION)
public @interface RequiresGraphql {

}
