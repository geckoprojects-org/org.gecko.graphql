package org.gecko.graphql.osgi.framework;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.context.ServletContextHelper;

@Component(
		scope = ServiceScope.SINGLETON,
		service = ServletContextHelper.class)
@org.gecko.graphql.annotation.GraphqlServletContext(
		path = "/graphql")
public class GraphqlServletContext extends ServletContextHelper {
// TODO: why not in org.gecko.graphql?
}
