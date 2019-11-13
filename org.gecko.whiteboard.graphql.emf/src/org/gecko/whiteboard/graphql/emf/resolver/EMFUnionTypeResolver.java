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

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

/**
 * A special resolver to resolve Union Types.
 * @author Juergen Albert
 * @since 10 Nov 2018
 */
public class EMFUnionTypeResolver implements TypeResolver {


	private Map<EClassifier, GraphQLObjectType> types;

	/**
	 * Creates a new instance.
	 */
	public EMFUnionTypeResolver(Map<EClassifier, GraphQLObjectType> types) {
		this.types = types;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.schema.TypeResolver#getType(graphql.TypeResolutionEnvironment)
	 */
	@Override
	public GraphQLObjectType getType(TypeResolutionEnvironment env) {
		return types.get(((EObject) env.getObject()).eClass());
	}

}
