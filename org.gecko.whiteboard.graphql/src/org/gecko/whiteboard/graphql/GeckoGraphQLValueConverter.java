/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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

import org.osgi.annotation.versioning.ProviderType;

import graphql.schema.GraphQLInputType;

/**
 * @author Michal H. Siemaszko
 * @since Apr 21, 2023
 */

@ProviderType
public interface GeckoGraphQLValueConverter {

	boolean canHandle(GraphQLInputType inputType, Class<?> outputType, Object outputValue);
	
	Object convert(Class<?> outputType, Object toConvert);
}
