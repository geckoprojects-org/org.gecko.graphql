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

import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLType;

public enum GeckoGraphQLUtil {
	INSTANCE;

	public String getTypeName(GraphQLType type) {
		if (type != null && type instanceof GraphQLNamedSchemaElement) {
			return ((GraphQLNamedSchemaElement) type).getName();
		} else {
			throw new IllegalArgumentException("Unsupported GraphQLType");
		}
	}
}
