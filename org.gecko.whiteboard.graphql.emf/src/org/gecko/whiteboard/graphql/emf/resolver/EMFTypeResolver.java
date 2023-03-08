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

import org.gecko.whiteboard.graphql.GeckoGraphQLUtil;

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
	private Map<String, GraphQLType> types;
	private String sufix;

	/**
	 * Creates a new instance.
	 */
	public EMFTypeResolver(Map<String, GraphQLType> types) {
		this(types, "Impl");
	}
	/**
	 * Creates a new instance.
	 */
	public EMFTypeResolver(Map<String, GraphQLType> types, String sufix) {
		this.types = types;
		this.sufix = sufix;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.schema.TypeResolver#getType(graphql.TypeResolutionEnvironment)
	 */
	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		String name = GeckoGraphQLUtil.INSTANCE.getTypeName(env.getFieldType());
		if (!name.endsWith(sufix)) {
			name += sufix;
		}
		return (GraphQLObjectType) types.get(GeckoGraphQLUtil.INSTANCE.getTypeName(env.getFieldType()) + sufix);
	}
}

