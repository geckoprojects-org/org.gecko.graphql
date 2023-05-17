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
package org.gecko.whiteboard.graphql.emf.converter;

import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.OSGI_EMF_GRAPHQL_VALUE_CONVERTER_CAPABILITY_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.gecko.emf.osgi.model.info.EMFModelInfo;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.osgi.annotation.bundle.Capability;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;

/**
 * @author Michal H. Siemaszko
 * @since May 12, 2023
 */
@Component(immediate = true, name = "GeckoGraphQLEMFObjectListValueConverter")
@Capability(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name = OSGI_EMF_GRAPHQL_VALUE_CONVERTER_CAPABILITY_NAME, version = "2.0.0")
public class GeckoGraphQLEMFObjectListValueConverter implements GeckoGraphQLValueConverter {

	@Reference
	private EMFModelInfo emfModelInfo;	
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter#canHandle(graphql.schema.GraphQLInputType, java.lang.Class, java.lang.Object)
	 */
	@Override
	public boolean canHandle(GraphQLInputType inputType, Class<?> outputType, Object outputValue) {
		// @formatter:off
		boolean canHandleInputType = (
				(inputType != null) &&
				(inputType instanceof GraphQLNonNull) && 
				(((GraphQLNonNull) inputType).getOriginalWrappedType() instanceof GraphQLList)
		);
		// @formatter:on
		
		// @formatter:off
		boolean canHandleOutputType = (				
				(outputType != null) && 
				(outputValue != null) &&				
				EObject.class.isAssignableFrom(outputType) && 
				(outputValue instanceof List)
		);
		// @formatter:on
				
		return (canHandleInputType && canHandleOutputType);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter#convert(java.lang.Class, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Class<?> outputType, Object toConvert) {
		if ((outputType != null) && (toConvert != null) && EObject.class.isAssignableFrom(outputType)
				&& (toConvert instanceof List)) {
			return convertEMFObjects(outputType, (List<Map<String, Object>>) toConvert);
		}

		return null;
	}
	
	private Object convertEMFObjects(Class<?> outputType, List<Map<String, Object>> toConvert) {
		List<Object> convertedObjects = new ArrayList<>();

		for (Map<String, Object> toConvertItem : toConvert) {
			convertedObjects.add(convertEMFObject(outputType, toConvertItem));
		}

		return convertedObjects;
	}
	
	private Object convertEMFObject(Class<?> outputType, Map<String, Object> toConvert) {
		return GeckoGraphQLValueConverterUtil.INSTANCE.convertEMFObject(emfModelInfo, outputType, toConvert);
	}
}
