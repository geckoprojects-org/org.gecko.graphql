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

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.osgi.service.component.annotations.ComponentPropertyType;

@Documented
@Retention(CLASS)
@Target({ TYPE, PACKAGE })
/**
 * Sets the {@link GeckoGraphQLConstants#GRAPHQL_WHITEBOARD_SERVICE_NAME} constant, and thus sets a name for the Service in the schema.
 * @author Juergen Albert
 * @since 5 Nov 2018
 */
@ComponentPropertyType
public @interface GraphqlServiceName {

	/**
	 * Prefix for the property name. This value is prepended to each property
	 * name.
	 */
	String PREFIX_ = "osgi.";

	public String value();
	
}
