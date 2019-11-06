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
package org.gecko.whiteboard.graphql.emf.resolver;

import java.util.Map;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.TypeResolver;

/**
 * 
 * @author jalbert
 * @since 10 Nov 2018
 */
public class EMFTypeResolver implements TypeResolver {


	private Map<Object, GraphQLType> types;

	/**
	 * Creates a new instance.
	 */
	public EMFTypeResolver(Map<Object, GraphQLType> types) {
		this.types = types;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.schema.TypeResolver#getType(graphql.TypeResolutionEnvironment)
	 */
	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		return (GraphQLObjectType) types.get(env.getFieldType().getName() + "Impl");
	}

}
