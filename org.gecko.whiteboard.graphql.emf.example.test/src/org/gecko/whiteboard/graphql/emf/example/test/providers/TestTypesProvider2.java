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
package org.gecko.whiteboard.graphql.emf.example.test.providers;

import java.util.ArrayList;
import java.util.Collection;

import graphql.kickstart.servlet.osgi.GraphQLTypesProvider;
import graphql.schema.GraphQLType;

public class TestTypesProvider2 implements GraphQLTypesProvider {

	/* 
	 * (non-Javadoc)
	 * @see graphql.kickstart.servlet.osgi.GraphQLTypesProvider#getTypes()
	 */
	@Override
	public Collection<GraphQLType> getTypes() {
		return new ArrayList<GraphQLType>();
	}
}