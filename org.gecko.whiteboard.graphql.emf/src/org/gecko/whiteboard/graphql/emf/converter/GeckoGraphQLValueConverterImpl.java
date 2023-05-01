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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gecko.emf.osgi.model.info.EMFModelInfo;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.osgi.annotation.bundle.Capability;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;

/**
 * 
 * @author michal
 * @since Apr 25, 2023
 */
@Component(immediate = true)
@Capability(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name = OSGI_EMF_GRAPHQL_VALUE_CONVERTER_CAPABILITY_NAME, version = "1.0.0")
public class GeckoGraphQLValueConverterImpl implements GeckoGraphQLValueConverter {
	
	// Option to provide a list of EStructuralFeatures that can be ignored
	public static final String IGNORE_FEATURE_LIST = "ignore.feature.list";
	
	@Reference
	private EMFModelInfo emfModelInfo;	

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter#canHandle(graphql.schema.GraphQLInputType, java.lang.Class)
	 */
	@Override
	public boolean canHandle(GraphQLInputType inputType, Class<?> outputType) {
		return (inputType instanceof GraphQLInputObjectType && EObject.class.isAssignableFrom(outputType));
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter#convert(graphql.schema.GraphQLInputType, java.lang.Class, java.util.Map)
	 */
	@Override
	public Object convert(
			GraphQLInputType inputType, // TODO: remove `GraphQLInputType` as method argument if not needed
			Class<?> outputType, 
			Map<String, Object> toConvert) {
		
		if (toConvert != null && !toConvert.isEmpty()) {
			
			// TODO: clarify when should `_type` be used as opposed to `outputType` - `outputType` is always there (passed as method argument) or can it be null? while `_type` may or may not be there...
			// .. should `_type` then take precedence, and `outputType` be used as "fallback" ? 
			if (toConvert.containsKey("_type")) { // TODO: extract "_type" to constant, like in `org.gecko.search.util.DocumentUtil`, `com.playertour.backend.apis.common.LuceneIndexHelper`, etc.
				
				Object type = toConvert.get("_type");
				
				EClassifier typeEClassifierOptional = emfModelInfo.getEClassifierForClass(type.toString()).orElse(null);
				if (typeEClassifierOptional != null) {
					
					EClass typeEClass = typeEClassifierOptional.eClass();
					
					EObject typeEObject = EcoreUtil.create(typeEClassifierOptional.eClass());
					
					// TODO: clarify how type hierarchy is relevant here
					List<EClass> typeUpperTypeHierarchyForEClass = emfModelInfo.getUpperTypeHierarchyForEClass(typeEClassifierOptional.eClass());
					
					fillEObjectWithData(typeEClass, typeEObject, toConvert, Collections.emptyMap());
					
					return typeEObject;
					
				}
			}
			
			EClassifier outputTypeEClassifierOptional = emfModelInfo.getEClassifierForClass(outputType).orElse(null);
			if (outputTypeEClassifierOptional != null) {
				
				EClass outputTypeEClass = outputTypeEClassifierOptional.eClass();
				
				EObject outputTypeEObject = EcoreUtil.create(outputTypeEClassifierOptional.eClass());
				
				// TODO: clarify how type hierarchy is relevant here
				List<EClass> outputTypeUpperTypeHierarchyForEClass = emfModelInfo.getUpperTypeHierarchyForEClass(outputTypeEClassifierOptional.eClass());
				
				fillEObjectWithData(outputTypeEClass, outputTypeEObject, toConvert, Collections.emptyMap());
				
				return outputTypeEObject;
			}			
		} 
		
		return null;		
	}
	
	// TODO: rename this method 
	private Object fillEObjectWithData(EClass eClass, EObject eObject, Map<String, Object> toConvert, Map<Object, Object> options) {
		Predicate<EStructuralFeature> ignoredFeaturesPredicate = constructIgnoredFeaturesPredicate(options);

		eClass.getEAllAttributes().stream().filter(ignoredFeaturesPredicate).forEach(eAttribute -> {
			if(!eAttribute.isMany()) {
				
				Object value = toConvert.get(eAttribute.getName());
				
				eObject.eSet(eAttribute, value);
				
			} else {
				
				@SuppressWarnings("unchecked")
				Collection<Object> existingValues = (Collection<Object>) eObject.eGet(eAttribute);
				
				@SuppressWarnings("unchecked")
				Collection<Object> toConvertValues = (Collection<Object>) toConvert.get(eAttribute.getName());
				
				Iterator<Object> toConvertValuesIt = toConvertValues.iterator();
				while (toConvertValuesIt.hasNext()) {
					existingValues.add(toConvertValuesIt.next());
				}
				
				eObject.eSet(eAttribute, existingValues); // TODO: is this needed or by reference these are added ?
			}
		});
		
		eClass.getEAllReferences().stream().filter(ignoredFeaturesPredicate).forEach(eReference -> {
			if(!eReference.isMany()) {
				eObject.eSet(eReference, fillEObjectWithData(eReference.getEReferenceType(), eObject, toConvert, options));
			} else {
				
				@SuppressWarnings("unchecked")
				Collection<Object> existingValues = (Collection<Object>) eObject.eGet(eReference);
				
				@SuppressWarnings("unchecked")
				Collection<Object> toConvertValues = (Collection<Object>) toConvert.get(eReference.getName());
				
				Iterator<Object> toConvertValuesIt = toConvertValues.iterator();
				while (toConvertValuesIt.hasNext()) {
					existingValues.add(toConvertValuesIt.next());
				}
				
				eObject.eSet(eReference, existingValues); // TODO: is this needed or by reference these are added ?				
			}
		});	
		
		return eObject;
	}
	
	private Predicate<EStructuralFeature> constructIgnoredFeaturesPredicate(Map<Object, Object> options) {
		Predicate<EStructuralFeature> ignoreFeaturePred = r -> true;
		if(options.containsKey(IGNORE_FEATURE_LIST)) {
			@SuppressWarnings("unchecked")
			List<String> excludedFeatureNames = (List<String>) options.get(IGNORE_FEATURE_LIST);
			ignoreFeaturePred = r -> !excludedFeatureNames.contains(r.getName());
		}
		
		return ignoreFeaturePred;
	}	
}
