package org.gecko.graphql.annotation;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface GraphqlSchemaProviderType {

	public static final String PROVIDER_TYPE_PROPERTY = "graphql.schema.provider.type";

	public static final String WHITEBOARD_TYPE = "graphql.whiteboard";


	public String value();
}
