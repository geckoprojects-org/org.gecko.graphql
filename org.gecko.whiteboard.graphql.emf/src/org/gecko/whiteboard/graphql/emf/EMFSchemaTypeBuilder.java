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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.annotation.RequireGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.emf.datafetcher.EStructuralFeatureDataFetcher;
import org.gecko.whiteboard.graphql.emf.datafetcher.PrototypeDataFetcher;
import org.gecko.whiteboard.graphql.emf.resolver.EMFTypeResolver;
import org.gecko.whiteboard.graphql.emf.schema.GraphQLEMFFieldDefinition;
import org.gecko.whiteboard.graphql.emf.schema.GraphQLEMFInputObjectField;
import org.gecko.whiteboard.graphql.emf.schema.GraphQLEMFInputObjectType;
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
@Capability(namespace=ImplementationNamespace.IMPLEMENTATION_NAMESPACE, name= OSGI_EMF_GRAPHQL_CAPABILITY_NAME, version="1.0.0")
public class EMFSchemaTypeBuilder implements GraphqlSchemaTypeBuilder{

	
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
		return EObject.class.isAssignableFrom(clazz);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder#buildType(java.lang.reflect.Type, java.util.Map, boolean)
	 */
	@Override
	public GraphQLType buildType(Type type, Map<String, GraphQLType> typeMapping, boolean inputType) {

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
		
		GraphQLType qlType = buildTypeForEClassifier(eClassifier, typeMapping, inputType);
		GraphQLTypeReference typeRef = GraphQLTypeReference.typeRef(qlType.getName());
		if(isList) {
			return GraphQLList.list(typeRef);
		}
		return typeRef ;
	}

	/**	 
	 * 
	 * @param eClassifier
	 * @param typeMapping
	 * @param inputType
	 * @return
	 */
	private GraphQLType buildTypeForEClassifier(EClassifier eClassifier, Map<String, GraphQLType> typeMapping,
			boolean inputType) {
		
		String name = getName(eClassifier, inputType);
		
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
				//TODO Could this be a Case?
			}
			return scalarType;
		}
		
		
		EClass eClass = (EClass) eClassifier;
		
		return buildEClass(eClass, typeMapping, inputType);
	}

	/**
	 * @param eClassifier
	 * @param inputType
	 * @return
	 */
	private String getName(EClassifier eClassifier, boolean inputType) {
		return inputType && !(eClassifier instanceof EEnum) ? eClassifier.getName() + "Input" : eClassifier.getName();
	}

	/**
	 * @param eClassifier
	 * @param typeMapping
	 * @return
	 */
	private GraphQLType buildEnum(EEnum eEnum, Map<String, GraphQLType> typeMapping) {
		GraphQLEnumType.Builder typeBuilder = GraphQLEnumType.newEnum().name(eEnum.getName());
		eEnum.getELiterals().stream().forEach(literal ->{
			typeBuilder.value(literal.getName(), literal.getLiteral(), getDocumentation(eEnum));
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
			return eAnnotation.getDetails().get("documentation");
		}
		return null;
	}

	/**
	 * @param eClass
	 * @param typeMapping
	 * @param inputType
	 * @return
	 */
	private GraphQLType buildEClass(EClass eClass, Map<String, GraphQLType> typeMapping, boolean inputType) {
		if(!inputType) {
			List<EClass> upperTypeHierarchyForEClass = modelInfo.getUpperTypeHierarchyForEClass(eClass);
			if(!upperTypeHierarchyForEClass.isEmpty()) {
				return buildUnionTypeOutput(eClass, upperTypeHierarchyForEClass, typeMapping);
			} 
			GraphQLInterfaceType interfaceType = buildInterfacesAndObject(eClass, typeMapping);
			return interfaceType;
		} else {
			GraphQLInputType inputObjectType = buildInputObject(eClass, typeMapping);
			return inputObjectType;
		}
	}
	
	/**
	 * @param eClass
	 * @param upperTypeHierarchyForEClass
	 * @param typeMapping
	 * @return
	 */
	private GraphQLType buildUnionTypeOutput(EClass eClass, List<EClass> upperTypeHierarchyForEClass,
			Map<String, GraphQLType> typeMapping) {
		String unionName = eClass.getName() + "Union";
		if(typeMapping.containsKey(unionName)) {
			return typeMapping.get(unionName);
		}
		
		Builder unionType = GraphQLUnionType.newUnionType();
		
		unionType.name(unionName);

		upperTypeHierarchyForEClass.stream()
			.map(eC -> buildInterfacesAndObject(eC, typeMapping))
			.map(qlType -> GraphQLTypeReference.typeRef(qlType.getName() + "Impl"))
			.forEach(unionType::possibleType);
		GraphQLInterfaceType qlInterface = buildInterfacesAndObject(eClass, typeMapping);
		
		unionType.possibleType(GraphQLTypeReference.typeRef(qlInterface.getName() + "Impl"));
		unionType.typeResolver(new EMFTypeResolver(typeMapping, "Union"));
		GraphQLUnionType type = unionType.build();
		
		typeMapping.put(unionName, type);
		
		return type;
	}

	/**
	 * 
	 * @param eClass
	 * @param typeMapping
	 * @param inputType
	 * @return
	 */
	private GraphQLInterfaceType buildInterfacesAndObject(EClass eClass, Map<String, GraphQLType> typeMapping) {
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
				.map(eC -> buildInterfacesAndObject(eC, typeMapping))
				.collect(Collectors.toList())	
				);
		
		for (EAttribute eAttribute : eClass.getEAllAttributes()) {
			if(!isQueryFeature(eAttribute)) {
				continue;
			}
			EClassifier type = eAttribute.getEType();
			String fieldName = eAttribute.getName();
			String documentation = getDocumentation(eAttribute);
			GraphQLType createType = buildTypeForEClassifier(type, typeMapping, false);
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
			GraphQLType createType = buildInterfacesAndObject(type, typeMapping);
			List<EClass> upperTypeHierarchyForEClass = modelInfo.getUpperTypeHierarchyForEClass(type);
			upperTypeHierarchyForEClass.forEach(eC -> buildInterfacesAndObject(eC, typeMapping));
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
	private GraphQLInputType buildInputObject(EClass eClass, Map<String, GraphQLType> typeMapping) {
		String name = getName(eClass, true);
		if(typeMapping.containsKey(name)) {
			return (GraphQLInputType) typeMapping.get(name);
		}
		
		GraphQLEMFInputObjectType.Builder inputObjectBuilder =  GraphQLEMFInputObjectType.newEMFInputObject().name(name).eClass(eClass);
		GraphQLEMFInputObjectType inputObject = inputObjectBuilder.build();
		typeMapping.put(name, inputObject);
		inputObjectBuilder = GraphQLEMFInputObjectType.newEMFInputObject(inputObject);
		
		eClass.getEAllAttributes().stream()
		.filter(this::isMutationFeature)
		.map(eAttribute -> {
			EClassifier type = eAttribute.getEType();
			String fieldName = eAttribute.getName();
			String documentation = getDocumentation(eAttribute);
			GraphQLType createType = buildTypeForEClassifier(type, typeMapping, true);
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
			GraphQLType createType = buildInputObject(type, typeMapping);
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
		GraphQLType result = eFeature instanceof EReference ? GraphQLTypeReference.typeRef(type.getName()) : type; 
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
		GraphQLType result = eAttribute instanceof EReference ? GraphQLTypeReference.typeRef(type.getName()) : type; 
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
	private GraphQLEMFFieldDefinition createField(String name, DataFetcher<?> datafetcher, GraphQLType type, String documentation, EStructuralFeature feature) {
		GraphQLEMFFieldDefinition.Builder builder = GraphQLEMFFieldDefinition.newEMFFieldDefinition()
			.name(name)
			.description(documentation)
			.dataFetcher(datafetcher)
			.type((GraphQLOutputType) type)
			.feature(feature);
		return builder.build();
	}

	/**
	 * @param documentation 
	 * @param string
	 * @return
	 */
	private GraphQLEMFInputObjectField createInputField(String name, GraphQLType type, String documentation, EStructuralFeature eFeature) {
		GraphQLEMFInputObjectField.Builder builder = GraphQLEMFInputObjectField.newEMFInputObjectField()
				.name(name)
				.description(documentation)
				.eFeature(eFeature)
				.type((GraphQLInputType) type);
		if(!eFeature.isMany()) {
			builder = builder.defaultValue(eFeature.getDefaultValueLiteral());
		}
		return builder.build();
	}

}
