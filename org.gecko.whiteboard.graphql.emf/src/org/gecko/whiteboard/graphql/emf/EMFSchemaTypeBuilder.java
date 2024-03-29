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
package org.gecko.whiteboard.graphql.emf;

import static org.gecko.whiteboard.graphql.GeckoGraphQLConstants.OSGI_EMF_GRAPHQL_CAPABILITY_NAME;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.gecko.emf.osgi.model.info.EMFModelInfo;
import org.gecko.whiteboard.graphql.GeckoGraphQLUtil;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.annotation.GraphqlUnionType;
import org.gecko.whiteboard.graphql.annotation.RequireGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.emf.datafetcher.EStructuralFeatureDataFetcher;
import org.gecko.whiteboard.graphql.emf.datafetcher.PrototypeDataFetcher;
import org.gecko.whiteboard.graphql.emf.resolver.EMFTypeResolver;
import org.gecko.whiteboard.graphql.emf.resolver.EMFUnionTypeResolver;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;
import graphql.schema.GraphQLUnionType.Builder;

@Component
@RequireGraphQLWhiteboard
@Capability(namespace=ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name= OSGI_EMF_GRAPHQL_CAPABILITY_NAME, version="2.0.0")
public class EMFSchemaTypeBuilder implements GraphqlSchemaTypeBuilder {
	
	@Reference(cardinality=ReferenceCardinality.MANDATORY)
	EMFModelInfo modelInfo;
	
	private List<ServiceReference<DataFetcher<Object>>> dataFetchers;
	
	private BundleContext bundleContext;
	
	@Activate
	public EMFSchemaTypeBuilder(@Reference(policyOption = ReferencePolicyOption.GREEDY) List<ServiceReference<DataFetcher<Object>>> dataFetchers, ComponentContext ctx) {
		this.dataFetchers = dataFetchers;
		bundleContext = ctx.getBundleContext();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#canHandle(java.lang.reflect.Type)
	 */
	@Override
	public boolean canHandle(Type type, boolean inputType) {
		Class<?> clazz;
		if(type instanceof Class) {
			clazz = (Class<?>) type;
		} else if(type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			clazz =  (Class<?>) parameterizedType.getActualTypeArguments()[0];
		} else {
			return false;
		}
		return Enumerator.class.isAssignableFrom(clazz) || EObject.class.isAssignableFrom(clazz);
	}
	
	@Override
	public GraphQLType buildType(
			Type type, 
			Map<String, GraphQLType> typeMapping, 
			boolean inputType,
			List<Annotation> annotations) {
		
		Class<?> clazz;
		boolean isList = false;
		if(type instanceof Class) {
			clazz = (Class<?>) type;
		} else if(type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			clazz =  (Class<?>) parameterizedType.getActualTypeArguments()[0];
			if(Collection.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
				isList = true;
			}
		} else {
			throw new IllegalArgumentException("Unknown Type " + type);
		}
		
		EClassifier eClassifier = modelInfo.getEClassifierForClass(clazz).orElse(null);

		if(eClassifier == null) {
			throw new IllegalArgumentException("No EClass found for " + ((Class<?>) type).getName());
		}
		
		GraphQLType qlType = buildTypeForEClassifier(eClassifier, typeMapping, inputType, annotations);
		GraphQLTypeReference typeRef = GraphQLTypeReference.typeRef(GeckoGraphQLUtil.INSTANCE.getTypeName(qlType));
		if(isList) {
			return GraphQLList.list(typeRef);
		}
		return typeRef;		
	}

	/**	 
	 * 
	 * @param eClassifier the {@link EClassifier} to build a {@link GraphQLType} for
	 * @param typeMapping
	 * @param inputType
	 * @param annotations 
	 * @return
	 */
	private GraphQLType buildTypeForEClassifier(EClassifier eClassifier, Map<String, GraphQLType> typeMapping,
			boolean inputType, List<Annotation> annotations) {
		
		String name = getName(eClassifier, inputType, annotations);
		
		if(typeMapping.containsKey(name)) {
			return typeMapping.get(name);
		}
		
		if(eClassifier instanceof EEnum) {
			return buildEnum((EEnum) eClassifier, typeMapping);
		}
		
		if(eClassifier instanceof EDataType) {
			EDataType dataType = (EDataType) eClassifier;
			GraphQLType scalarType = GraphqlSchemaTypeBuilder.getGraphQLScalarType(dataType.getInstanceClass());
			if(scalarType == null) {
				// TODO Could this be a Case?
			}
			return scalarType;
		}
		
		
		EClass eClass = (EClass) eClassifier;
		
		return buildEClass(eClass, typeMapping, inputType, annotations);
	}

	/**
	 * @param eClassifier
	 * @param inputType
	 * @return
	 */
	private String getName(EClassifier eClassifier, boolean inputType, List<Annotation> annotations) {
		if (inputType && !(eClassifier instanceof EEnum)) {
			return eClassifier.getName() + "Input";
		} else if (getUnionTypeAnnotation(annotations) != null) {
			return eClassifier.getName() + "Union";
		}
		return eClassifier.getName();
	}

	/**
	 * @param eClassifier
	 * @param typeMapping
	 * @return
	 */
	private GraphQLType buildEnum(EEnum eEnum, Map<String, GraphQLType> typeMapping) {
		GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name(eEnum.getName());
		eEnum.getELiterals().stream().forEach(literal ->{
			typeBuilder.value(literal.getName(), literal.getInstance(), getDocumentation(eEnum));
		});
		GraphQLEnumType theEnum = typeBuilder.build();
		typeMapping.put(eEnum.getName(), theEnum);
		return theEnum;
	}

	/**
	 * @param eEnum
	 * @return
	 */
	private String getDocumentation(EModelElement eClassifier) {
		EAnnotation eAnnotation = eClassifier.getEAnnotation("http://www.eclipse.org/emf/2002/GenModel");
		if(eAnnotation != null) {
			// FIXME: check if "documentation" key actually exists
			return eAnnotation.getDetails().get("documentation");
		}
		return null;
	}

	/**
	 * @param eClass
	 * @param typeMapping
	 * @param inputType
	 * @param annotations 
	 * @return
	 */
	private GraphQLType buildEClass(EClass eClass, Map<String, GraphQLType> typeMapping, boolean inputType, List<Annotation> annotations) {
		if(!inputType) {
			List<EClass> upperTypeHierarchyForEClass = modelInfo.getUpperTypeHierarchyForEClass(eClass);
			GraphqlUnionType unionTypeAnnotation = getUnionTypeAnnotation(annotations);
			if(!upperTypeHierarchyForEClass.isEmpty() && unionTypeAnnotation != null) {
				return buildUnionTypeOutput(eClass, upperTypeHierarchyForEClass, typeMapping, annotations);
			} 
			GraphQLInterfaceType interfaceType = buildInterfacesAndObject(eClass, typeMapping, annotations);
			return interfaceType;
		} else {
			GraphQLInputType inputObjectType = buildInputObject(eClass, typeMapping, annotations);
			return inputObjectType;
		}
	}
	
	/**
	 * @param annotations
	 * @return
	 */
	private GraphqlUnionType getUnionTypeAnnotation(List<Annotation> annotations) {
		return Optional
		.ofNullable(annotations)
		.orElseGet(Collections::emptyList)
		.stream()
		.filter(annotation -> annotation instanceof GraphqlUnionType)
		.map(annotation -> (GraphqlUnionType) annotation)
		.findFirst()
		.orElseGet(() -> null);
	}

	/**
	 * @param eClass
	 * @param upperTypeHierarchyForEClass
	 * @param typeMapping
	 * @param annotations 
	 * @return
	 */
	private GraphQLType buildUnionTypeOutput(EClass eClass, List<EClass> upperTypeHierarchyForEClass,
			Map<String, GraphQLType> typeMapping, List<Annotation> annotations) {
		String unionName = getName(eClass, false, annotations);
		if(typeMapping.containsKey(unionName)) {
			return typeMapping.get(unionName);
		}
		
		GraphqlUnionType unionTypeAnnotation = getUnionTypeAnnotation(annotations);
		
		Builder unionType = GraphQLUnionType.newUnionType();
		
		unionType.name(unionName);

		Map<EClassifier, GraphQLObjectType> resolverContent = new HashMap<>();
		
		upperTypeHierarchyForEClass.stream()
			.filter(eC -> isMemberOfUnionType(eC, unionTypeAnnotation))
			.map(eC -> {
				GraphQLInterfaceType interfaceType = buildInterfacesAndObject(eC, typeMapping, Collections.emptyList());
				
				resolverContent.put(eC, (GraphQLObjectType) typeMapping.get(interfaceType.getName() + "Impl"));
				
				return interfaceType;
			})
			.map(qlType -> GraphQLTypeReference.typeRef(qlType.getName() + "Impl"))
			.forEach(unionType::possibleType);
		GraphQLInterfaceType qlInterface = buildInterfacesAndObject(eClass, typeMapping, Collections.emptyList());
		if(isMemberOfUnionType(eClass, unionTypeAnnotation)) {
			resolverContent.put(eClass, (GraphQLObjectType) typeMapping.get(qlInterface.getName() + "Impl"));
			unionType.possibleType(GraphQLTypeReference.typeRef(qlInterface.getName() + "Impl"));
		}
		unionType.typeResolver(new EMFUnionTypeResolver(resolverContent));
		GraphQLUnionType type = unionType.build();
		
		typeMapping.put(unionName, type);
		
		return type;
	}
	
	/**
	 * @param eC
	 * @param unionTypeAnnotation
	 * @return
	 */
	private boolean isMemberOfUnionType(EClass eC, GraphqlUnionType unionTypeAnnotation) {
		
		if(unionTypeAnnotation.value().length == 0) {
			return true;
		}
		
		for(Class<?> clazz : unionTypeAnnotation.value()) {
			if(clazz == eC.getInstanceClass()) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 
	 * @param eClass
	 * @param typeMapping
	 * @param inputType
	 * @return
	 */
	private GraphQLInterfaceType buildInterfacesAndObject(EClass eClass, Map<String, GraphQLType> typeMapping, List<Annotation> annotations) {
		if(typeMapping.containsKey(eClass.getName())) {
			return (GraphQLInterfaceType) typeMapping.get(eClass.getName());
		}
		GraphQLInterfaceType.Builder interfaceBuilder = GraphQLInterfaceType.newInterface().name(eClass.getName());
		GraphQLObjectType.Builder objectBuilder = null;
		objectBuilder = GraphQLObjectType.newObject().name(eClass.getName() + "Impl");
		GraphQLInterfaceType interfaceType = interfaceBuilder.typeResolver(new EMFTypeResolver(typeMapping)).build();
		typeMapping.put(eClass.getName(), interfaceType);
		interfaceBuilder = GraphQLInterfaceType.newInterface(interfaceType);
		List<GraphQLInterfaceType> hirachy = new LinkedList<GraphQLInterfaceType>();
		
		hirachy.addAll(
				eClass.getEAllSuperTypes().stream()
				.map(eC -> buildInterfacesAndObject(eC, typeMapping, annotations))
				.collect(Collectors.toList())	
				);
		
		for (EAttribute eAttribute : eClass.getEAllAttributes()) {
			if(!isQueryFeature(eAttribute)) {
				continue;
			}
			EClassifier type = eAttribute.getEType();
			String fieldName = eAttribute.getName();
			String documentation = getDocumentation(eAttribute);
			GraphQLType createType = buildTypeForEClassifier(type, typeMapping, false, annotations);
			createType = wrapReferenceProperties(eAttribute, createType);
			DataFetcher<Object> datafetcher = getDataFetcher(eAttribute);
			interfaceBuilder.field(createField(fieldName, datafetcher, createType, documentation, eAttribute));
			objectBuilder.field(createField(fieldName, datafetcher, createType, documentation, eAttribute));
		}
		
		for(EReference reference : eClass.getEAllReferences()) {
			if(!isQueryFeature(reference)) {
				continue;
			}
			EClass type = (EClass) reference.getEType();
			String fieldName = reference.getName();
			String documentation = getDocumentation(reference);
			GraphQLType createType = buildInterfacesAndObject(type, typeMapping, annotations);
			List<EClass> upperTypeHierarchyForEClass = modelInfo.getUpperTypeHierarchyForEClass(type);
			upperTypeHierarchyForEClass.forEach(eC -> buildInterfacesAndObject(eC, typeMapping, annotations));
			createType = wrapReferenceProperties(reference, createType);
			DataFetcher<Object> datafetcher = getDataFetcher(reference);
			interfaceBuilder.field(createField(fieldName, datafetcher, createType, documentation, reference));
			objectBuilder.field(createField(fieldName, datafetcher, createType, documentation, reference));
		}
		
		interfaceType = interfaceBuilder.build();
		hirachy.add(0, interfaceType);
		
		objectBuilder.withInterfaces(
				hirachy
				.stream()
				.map(iT -> GraphQLTypeReference.typeRef(iT.getName()))
				.toArray(GraphQLTypeReference[]::new)
				);
		typeMapping.put(eClass.getName(), interfaceType);
		typeMapping.put(eClass.getName() + "Impl", objectBuilder.build());
		return interfaceType;
	}

	/**
	 * @param eAttribute
	 * @return
	 */
	private DataFetcher<Object> getDataFetcher(EStructuralFeature feature) {
		EAnnotation eAnnotation = feature.getEAnnotation("GraphQLContext");
		
		if(eAnnotation != null) {
			String target = eAnnotation.getDetails().get("dataFetcherTarget");
			if(target != null) {
				try {
					Filter f = FrameworkUtil.createFilter(target);
					DataFetcher<Object> dataFetcher = dataFetchers.stream()
							.filter(f::match)
							.sorted(ServiceReference::compareTo)
							.findFirst()
							.map(bundleContext::getServiceObjects)
							.map(PrototypeDataFetcher::newFetcher)
							.orElse(null);
					if(dataFetcher == null) {
						throw new RuntimeException(String.format("no Datafetcher found for dataFetcherTarget %s for Feature %s on %s is invalid", target, feature.getName(), feature.getEContainingClass().getName()));
					}
					return dataFetcher;
				} catch (InvalidSyntaxException e) {
					throw new RuntimeException(String.format("dataFetcherTarget %s for Feature %s on %s is invalid. Message %s", target, feature.getName(), feature.getEContainingClass().getName(), e.getMessage()), e);
				}
			}
		}
		
		return new EStructuralFeatureDataFetcher(feature);
	}

	/**
	 * Creates a {@link GraphQLInputType} for the given {@link EClass}. The type will be named EClass
	 * @param eClass
	 * @param typeMapping
	 * @param upperTypeHierarchyForEClass 
	 * @param inputType
	 * @return
	 */
	private GraphQLInputType buildInputObject(EClass eClass, Map<String, GraphQLType> typeMapping, List<Annotation> annotations) {
		String name = getName(eClass, true, annotations);
		if(typeMapping.containsKey(name)) {
			return (GraphQLInputType) typeMapping.get(name);
		}
		
		GraphQLInputObjectType.Builder inputObjectBuilder =  GraphQLInputObjectType.newInputObject().name(name);
		
		GraphQLInputObjectType inputObject = inputObjectBuilder.build();
		typeMapping.put(name, inputObject);
		inputObjectBuilder = GraphQLInputObjectType.newInputObject(inputObject);
		
		eClass.getEAllAttributes().stream()
		.filter(this::isMutationFeature)
		.map(eAttribute -> {
			EClassifier type = eAttribute.getEType();
			String fieldName = eAttribute.getName();
			String documentation = getDocumentation(eAttribute);
			GraphQLType createType = buildTypeForEClassifier(type, typeMapping, true, annotations);
			createType = wrapReferenceInputAttribute(eAttribute, createType);
			return createInputField(fieldName, createType, documentation, eAttribute);
		})
		.forEach(inputObjectBuilder::field);

		eClass.getEAllReferences().stream()
		.filter(this::isMutationFeature)
		.map(reference -> {
			EClass type = (EClass) reference.getEType();
			String fieldName = reference.getName();
			String documentation = getDocumentation(reference);
			GraphQLType createType = buildInputObject(type, typeMapping, annotations);
			createType = wrapReferenceProperties(reference, createType);
			return createInputField(fieldName, createType, documentation, reference);
		})
		.forEach(inputObjectBuilder::field);
		
		inputObject = inputObjectBuilder.build();
		typeMapping.put(name, inputObject);
		return inputObject;
	}
	
	public boolean isMutationFeature(EStructuralFeature feature) {
		boolean result = feature.getEAnnotation("QueryOnly") == null;
		if(result && feature instanceof EReference) {
			result = feature.getEAnnotation("MutationOnly") != null ? true : ((EReference) feature).isContainment();
		}
		return result;
	}

	public boolean isQueryFeature(EStructuralFeature feature) {
		return feature.getEAnnotation("MutationOnly") == null;
	}
	
	/**
	 * @param eAttribute
	 * @param createType
	 * @return
	 */
	private GraphQLType wrapReferenceProperties(EStructuralFeature eFeature, GraphQLType type) {
		GraphQLType result = eFeature instanceof EReference ? GraphQLTypeReference.typeRef(GeckoGraphQLUtil.INSTANCE.getTypeName(type)) : type; 
		result = wrap(eFeature.isRequired(), GraphQLNonNull::nonNull, result);
		result = wrap(eFeature.isMany(), GraphQLList::list, result);
		return result;
	}

	/**
	 * @param eAttribute
	 * @param createType
	 * @return
	 */
	private GraphQLType wrapReferenceInputAttribute(EAttribute eAttribute, GraphQLType type) {
		GraphQLType result = eAttribute instanceof EReference ? GraphQLTypeReference.typeRef(GeckoGraphQLUtil.INSTANCE.getTypeName(type)) : type; 
		result = wrap(eAttribute.isRequired() && !eAttribute.isID(), GraphQLNonNull::nonNull, result);
		result = wrap(eAttribute.isMany(), GraphQLList::list, result);
		return result;
	}

	/**
	 * @param required
	 * @param object
	 * @param createType
	 * @return
	 */
	private GraphQLType wrap(boolean required, Function<GraphQLType, GraphQLType> function, GraphQLType type) {
		return required ? function.apply(type) : type;
	}

	/**
	 * @param documentation 
	 * @param string
	 * @return
	 */
	private GraphQLFieldDefinition createField(
			String name, 
			DataFetcher<?> datafetcher, 
			GraphQLType type, 
			String documentation, 
			EStructuralFeature feature) { // TODO: remove `EStructuralFeature` as method argument if not needed
		
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
			.name(name)
			.description(documentation)
			.dataFetcher(datafetcher)
			.type((GraphQLOutputType) type);
		
		return builder.build();
	}

	/**
	 * @param documentation 
	 * @param string
	 * @return
	 */
	private GraphQLInputObjectField createInputField(
			String name, 
			GraphQLType type, 
			String documentation, 
			EStructuralFeature eFeature) {
		
		GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField()
				.name(name)
				.description(documentation)
				.type((GraphQLInputType) type);
		
		if (!eFeature.isMany()) {
			builder = builder.defaultValue(eFeature.getDefaultValue());
		}

		return builder.build();
	}
}
