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

/**
 * @author Michal H. Siemaszko
 * @since May 12, 2023
 */
public enum GeckoGraphQLValueConverterUtil {
	INSTANCE;
	
	// Option to provide a list of EStructuralFeatures that can be ignored
	public static final String IGNORE_FEATURE_LIST = "ignore.feature.list";	
	
	public Object convertEMFObject(EMFModelInfo emfModelInfo, Class<?> outputType, Map<String, Object> toConvert) {
		EClassifier outputTypeEClassifierOptional = getEClassifier(emfModelInfo, outputType, toConvert);

		if (outputTypeEClassifierOptional != null) {

			EClass outputTypeEClass = (EClass) outputTypeEClassifierOptional;

			EObject outputTypeEObject = EcoreUtil.create(outputTypeEClass);

			fillWithData(outputTypeEClass, outputTypeEObject, toConvert, Collections.emptyMap());

			return outputTypeEObject;
		}

		return null;
	}

	private EClassifier getEClassifier(EMFModelInfo emfModelInfo, Class<?> outputType, Map<String, Object> toConvert) {
		// @formatter:off
		return (toConvert.containsKey("_type")) ? 
				emfModelInfo.getEClassifierForClass(toConvert.get("_type").toString()).orElse(null) : 
					emfModelInfo.getEClassifierForClass(outputType).orElse(null);
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	private Object fillWithData(final EClass eClass, final EObject eObject, final Map<String, Object> toConvert,
			final Map<Object, Object> options) {

		Predicate<EStructuralFeature> ignoredFeaturesPredicate = constructIgnoredFeaturesPredicate(options);

		eClass.getEAllAttributes().stream().filter(ignoredFeaturesPredicate).forEach(eAttribute -> {

			if (!eAttribute.isMany()) {

				Object value = toConvert.get(eAttribute.getName());

				eObject.eSet(eAttribute, value);

			} else {

				Collection<Object> existingValues = (Collection<Object>) eObject.eGet(eAttribute);

				Collection<Object> toConvertValues = (Collection<Object>) toConvert.get(eAttribute.getName());

				if (toConvertValues != null && !toConvertValues.isEmpty()) {

					Iterator<Object> toConvertValuesIt = toConvertValues.iterator();
					while (toConvertValuesIt.hasNext()) {
						existingValues.add(toConvertValuesIt.next());
					}

					eObject.eSet(eAttribute, existingValues);
				}
			}
		});

		eClass.getEAllReferences().stream().filter(ignoredFeaturesPredicate).forEach(eReference -> {

			if (toConvert.containsKey(eReference.getName())) {

				if (!eReference.isMany()) {
					eObject.eSet(eReference, fillWithData(eReference.getEReferenceType(), eObject, toConvert, options));
				} else {

					Collection<Object> existingValues = (Collection<Object>) eObject.eGet(eReference);

					Collection<Object> toConvertValues = (Collection<Object>) toConvert.get(eReference.getName());

					if (toConvertValues != null && !toConvertValues.isEmpty()) {

						Iterator<Object> toConvertValuesIt = toConvertValues.iterator();
						while (toConvertValuesIt.hasNext()) {
							existingValues.add(toConvertValuesIt.next());
						}

						eObject.eSet(eReference, existingValues);
					}
				}
			}
		});

		return eObject;
	}

	private Predicate<EStructuralFeature> constructIgnoredFeaturesPredicate(Map<Object, Object> options) {
		Predicate<EStructuralFeature> ignoreFeaturePred = r -> true;
		if (options.containsKey(IGNORE_FEATURE_LIST)) {
			@SuppressWarnings("unchecked")
			List<String> excludedFeatureNames = (List<String>) options.get(IGNORE_FEATURE_LIST);
			ignoreFeaturePred = r -> !excludedFeatureNames.contains(r.getName());
		}

		return ignoreFeaturePred;
	}	
}
