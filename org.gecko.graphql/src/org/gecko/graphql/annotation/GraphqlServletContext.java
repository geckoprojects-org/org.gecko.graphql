package org.gecko.graphql.annotation;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME;

import org.osgi.service.component.annotations.ComponentPropertyType;

@RequiresGraphql
@ComponentPropertyType
public @interface GraphqlServletContext {

	public static final String PREFIX_ = "osgi.http.whiteboard.context.";
	public static final String NAME = "graphql";
	public static final String FILTER = "(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=" + NAME + ")";


	public String name() default NAME;


	public String path();
}
