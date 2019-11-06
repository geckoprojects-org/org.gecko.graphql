package org.gecko.whiteboard.graphql.emf.datafetcher;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class EStructuralFeatureDataFetcher implements DataFetcher<Object> {

	private final EStructuralFeature feature;

	public EStructuralFeatureDataFetcher(EStructuralFeature feature) {
		this.feature = feature;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		if (!(environment.getSource() instanceof EObject)) {
			return null;
		}
		EObject context = (EObject) environment.getSource();
//		try {
		Object value = context.eGet(feature);
//			EClassifier eType = feature.getEType();
//			if (value != null && eType instanceof EDataType) {
//				EDataType dataType = (EDataType) eType;
//				if (dataType instanceof EEnum || dataType == EcorePackage.eINSTANCE.getEString() || SchemaGenerator.getGraphQLScalarType(dataType) != Scalars.GraphQLString) {
//					return value;
//				}
//				if (feature.isMany()) {
//					Collection<?> values = (EList<?>) value;
//					Collection<?> strings = new ArrayList<>();
//					for (Object v : values) {
//						strings.add(EcoreUtil.convertToString(dataType, v));
//					}
//					value = strings;
//				} else {
//					value = EcoreUtil.convertToString(dataType, value); 
//				}
//			}
			return value;
//		} catch (Exception e) {
//			
//			return null;
//		}
	}
}
