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
package org.gecko.whiteboard.graphql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gecko.whiteboard.graphql.GeckoGraphQLConstants;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.implementation.ImplementationNamespace;


/**
 * If this Annotation is used, the GraphQLWhiteboard will be required 
 * @author jalbert
 * @since 2 Nov 2018
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace=ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name=GeckoGraphQLConstants.OSGI_EMF_GRAPHQL_CAPABILITY_NAME, version="1.1.0")
@RequireGraphQLWhiteboard
public @interface RequireEMFGraphQLWhiteboard {

}
