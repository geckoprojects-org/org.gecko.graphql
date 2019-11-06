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
package org.gecko.whiteboard.graphql.emf.executation;

import static graphql.Assert.assertShouldNeverHappen;
import static graphql.schema.GraphQLTypeUtil.isList;
import static graphql.schema.GraphQLTypeUtil.isNonNull;
import static graphql.schema.GraphQLTypeUtil.unwrapAll;
import static graphql.schema.GraphQLTypeUtil.unwrapOne;
import static graphql.schema.visibility.DefaultGraphqlFieldVisibility.DEFAULT_FIELD_VISIBILITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gecko.whiteboard.graphql.emf.schema.GraphQLEMFInputObjectField;
import org.gecko.whiteboard.graphql.emf.schema.GraphQLEMFInputObjectType;

import graphql.execution.InputMapDefinesTooManyFieldsException;
import graphql.execution.NonNullableValueCoercedAsNullException;
import graphql.execution.TypeFromAST;
import graphql.execution.ValuesResolver;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.NullValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.Value;
import graphql.language.VariableDefinition;
import graphql.language.VariableReference;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeUtil;
import graphql.schema.GraphQLUnmodifiedType;
import graphql.schema.visibility.GraphqlFieldVisibility;

/**
 * 
 * @author jalbert
 * @since 19 Nov 2018
 */
@SuppressWarnings("rawtypes")
public class EMFValuesResolver extends ValuesResolver {

	
	/**
     * The http://facebook.github.io/graphql/#sec-Coercing-Variable-Values says :
     *
     * <pre>
     * 1. Let coercedValues be an empty unordered Map.
     * 2. Let variableDefinitions be the variables defined by operation.
     * 3. For each variableDefinition in variableDefinitions:
     *      a. Let variableName be the name of variableDefinition.
     *      b. Let variableType be the expected type of variableDefinition.
     *      c. Let defaultValue be the default value for variableDefinition.
     *      d. Let value be the value provided in variableValues for the name variableName.
     *      e. If value does not exist (was not provided in variableValues):
     *          i. If defaultValue exists (including null):
     *              1. Add an entry to coercedValues named variableName with the value defaultValue.
     *          ii. Otherwise if variableType is a Non‐Nullable type, throw a query error.
     *          iii. Otherwise, continue to the next variable definition.
     *      f. Otherwise, if value cannot be coerced according to the input coercion rules of variableType, throw a query error.
     *      g. Let coercedValue be the result of coercing value according to the input coercion rules of variableType.
     *      h. Add an entry to coercedValues named variableName with the value coercedValue.
     * 4. Return coercedValues.
     * </pre>
     *
     * @param schema              the schema
     * @param variableDefinitions the variable definitions
     * @param variableValues      the supplied variables
     *
     * @return coerced variable values as a map
     */
    public Map<String, Object> coerceArgumentValues(GraphQLSchema schema, List<VariableDefinition> variableDefinitions, Map<String, Object> variableValues) {
        GraphqlFieldVisibility fieldVisibility = schema.getFieldVisibility();
        Map<String, Object> coercedValues = new LinkedHashMap<>();
        for (VariableDefinition variableDefinition : variableDefinitions) {
            String variableName = variableDefinition.getName();
            GraphQLType variableType = TypeFromAST.getTypeFromAST(schema, variableDefinition.getType());

            // 3.e
            if (!variableValues.containsKey(variableName)) {
                Value defaultValue = variableDefinition.getDefaultValue();
                if (defaultValue != null) {
                    // 3.e.i
                    Object coercedValue = coerceValueAst(fieldVisibility, variableType, variableDefinition.getDefaultValue(), null);
                    coercedValues.put(variableName, coercedValue);
                } else if (isNonNull(variableType)) {
                    // 3.e.ii
                    throw new NonNullableValueCoercedAsNullException(variableDefinition, variableType);
                }
            } else {
                Object value = variableValues.get(variableName);
                // 3.f
                Object coercedValue = getVariableValue(fieldVisibility, variableDefinition, variableType, value);
                // 3.g
                coercedValues.put(variableName, coercedValue);
            }
        }

        return coercedValues;
    }

    private Object getVariableValue(GraphqlFieldVisibility fieldVisibility, VariableDefinition variableDefinition, GraphQLType variableType, Object value) {

        if (value == null && variableDefinition.getDefaultValue() != null) {
            return coerceValueAst(fieldVisibility, variableType, variableDefinition.getDefaultValue(), null);
        }

        return coerceValue(fieldVisibility, variableDefinition, variableDefinition.getName(), variableType, value);
    }

    public Map<String, Object> getArgumentValues(List<GraphQLArgument> argumentTypes, List<Argument> arguments, Map<String, Object> variables) {
        return getArgumentValues(DEFAULT_FIELD_VISIBILITY, argumentTypes, arguments, variables);
    }

    public Map<String, Object> getArgumentValues(GraphqlFieldVisibility fieldVisibility, List<GraphQLArgument> argumentTypes, List<Argument> arguments, Map<String, Object> variables) {
        if (argumentTypes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Argument> argumentMap = argumentMap(arguments);
        for (GraphQLArgument fieldArgument : argumentTypes) {
            String argName = fieldArgument.getName();
            Argument argument = argumentMap.get(argName);
            Object value;
            if (argument != null) {
                value = coerceValueAst(fieldVisibility, fieldArgument.getType(), argument.getValue(), variables);
            } else {
                value = fieldArgument.getDefaultValue();
            }
            // only put an arg into the result IF they specified a variable at all or
            // the default value ended up being something non null
            if (argumentMap.containsKey(argName) || value != null) {
                result.put(argName, value);
            }
        }
        return result;
    }


    private Map<String, Argument> argumentMap(List<Argument> arguments) {
        Map<String, Argument> result = new LinkedHashMap<>();
        for (Argument argument : arguments) {
            result.put(argument.getName(), argument);
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    private Object coerceValue(GraphqlFieldVisibility fieldVisibility, VariableDefinition variableDefinition, String inputName, GraphQLType graphQLType, Object value) {
        try {
            if (isNonNull(graphQLType)) {
                Object returnValue =
                        coerceValue(fieldVisibility, variableDefinition, inputName, unwrapOne(graphQLType), value);
                if (returnValue == null) {
                    throw new NonNullableValueCoercedAsNullException(variableDefinition, inputName, graphQLType);
                }
                return returnValue;
            }

            if (value == null) {
                return null;
            }

            if (graphQLType instanceof GraphQLScalarType) {
                return coerceValueForScalar((GraphQLScalarType) graphQLType, value);
            } else if (graphQLType instanceof GraphQLEnumType) {
                return coerceValueForEnum((GraphQLEnumType) graphQLType, value);
            } else if (graphQLType instanceof GraphQLList) {
                return coerceValueForList(fieldVisibility, variableDefinition, inputName, (GraphQLList) graphQLType, value);
            } else if (graphQLType instanceof GraphQLInputObjectType) {
                if (value instanceof Map) {
                    return coerceValueForInputObjectType(fieldVisibility, variableDefinition, (GraphQLInputObjectType) graphQLType, (Map<String, Object>) value);
                } else {
                    throw new CoercingParseValueException(
                            "Expected type 'Map' but was '" + value.getClass().getSimpleName() +
                                    "'. Variables for input objects must be an instance of type 'Map'."
                    );
                }
            } else {
                return assertShouldNeverHappen("unhandled type %s", graphQLType);
            }
        } catch (CoercingParseValueException e) {
            if (e.getLocations() != null) {
                throw e;
            }

            throw new CoercingParseValueException(
                    "Variable '" + inputName + "' has an invalid value. " + e.getMessage(),
                    e.getCause(),
                    variableDefinition.getSourceLocation()
            );
        }
    }

    private Object coerceValueForInputObjectType(GraphqlFieldVisibility fieldVisibility, VariableDefinition variableDefinition, GraphQLInputObjectType inputObjectType, Map<String, Object> input) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<GraphQLInputObjectField> fields = fieldVisibility.getFieldDefinitions(inputObjectType);
        List<String> fieldNames = fields.stream().map(GraphQLInputObjectField::getName).collect(Collectors.toList());
        for (String inputFieldName : input.keySet()) {
            if (!fieldNames.contains(inputFieldName)) {
                throw new InputMapDefinesTooManyFieldsException(inputObjectType, inputFieldName);
            }
        }

        for (GraphQLInputObjectField inputField : fields) {
            if (input.containsKey(inputField.getName()) || alwaysHasValue(inputField)) {
                Object value = coerceValue(fieldVisibility, variableDefinition,
                        inputField.getName(),
                        inputField.getType(),
                        input.get(inputField.getName()));
                result.put(inputField.getName(), value == null ? inputField.getDefaultValue() : value);
            }
        }
        return result;
    }

    private boolean alwaysHasValue(GraphQLInputObjectField inputField) {
        return inputField.getDefaultValue() != null
                || isNonNull(inputField.getType());
    }

    private Object coerceValueForScalar(GraphQLScalarType graphQLScalarType, Object value) {
        return graphQLScalarType.getCoercing().parseValue(value);
    }

    private Object coerceValueForEnum(GraphQLEnumType graphQLEnumType, Object value) {
        return graphQLEnumType.getCoercing().parseValue(value);
    }

    private List coerceValueForList(GraphqlFieldVisibility fieldVisibility, VariableDefinition variableDefinition, String inputName, GraphQLList graphQLList, Object value) {
        if (value instanceof Iterable) {
            List<Object> result = new ArrayList<>();
            for (Object val : (Iterable) value) {
                result.add(coerceValue(fieldVisibility, variableDefinition, inputName, graphQLList.getWrappedType(), val));
            }
            return result;
        } else {
            return Collections.singletonList(coerceValue(fieldVisibility, variableDefinition, inputName, graphQLList.getWrappedType(), value));
        }
    }

	@SuppressWarnings("unchecked")
	private Object coerceValueAst(GraphqlFieldVisibility fieldVisibility, GraphQLType type, Value inputValue, Map<String, Object> variables) {
		if (inputValue instanceof VariableReference) {
			GraphQLUnmodifiedType realType = unwrapAll(type) ;
        	if(realType instanceof GraphQLEMFInputObjectType) {
        		if(isWrappedList(type)) {
        			List<Map<String, Object>> valuesList = (List<Map<String, Object>>) variables.get(((VariableReference) inputValue).getName());
        			return valuesList.stream().map(map -> convertToEObject(fieldVisibility, map, (GraphQLEMFInputObjectType) realType)).collect(Collectors.toList());
        		}
        		return convertToEObject(fieldVisibility, (Map<String, Object>) variables.get(((VariableReference) inputValue).getName()), (GraphQLEMFInputObjectType) realType);
        	}
        	return variables.get(((VariableReference) inputValue).getName());
        }
		if (inputValue instanceof NullValue) {
            return null;
        }
        if (type instanceof GraphQLScalarType) {
            return parseLiteral(inputValue, ((GraphQLScalarType) type).getCoercing(), variables);
        }
        if (isNonNull(type)) {
            return coerceValueAst(fieldVisibility, unwrapOne(type), inputValue, variables);
        }
        if (type instanceof GraphQLInputObjectType) {
            return coerceValueAstForInputObject(fieldVisibility, (GraphQLInputObjectType) type, (ObjectValue) inputValue, variables);
        }
        if (type instanceof GraphQLEnumType) {
            return parseLiteral(inputValue, ((GraphQLEnumType) type).getCoercing(), variables);
        }
        if (isList(type)) {
            return coerceValueAstForList(fieldVisibility, (GraphQLList) type, inputValue, variables);
        }
        return null;
    }

    /**
	 * @param type
	 * @return
	 */
	private boolean isWrappedList(GraphQLType type) {
		if(isList(type)) {
			return true;
		}
		if(GraphQLTypeUtil.isWrapped(type)) {
			return isWrappedList(GraphQLTypeUtil.unwrapOne(type));
		}
		return false;
	}

	/**
	 * @param fieldVisibility 
     * @param object the map representing the object
	 * @param type the EMF type
	 * @return an EObject with the necessary value
	 */
	@SuppressWarnings("unchecked")
	private EObject convertToEObject(GraphqlFieldVisibility fieldVisibility, Map<String, Object> object, GraphQLEMFInputObjectType type) {
		EObject eObject = EcoreUtil.create(((GraphQLEMFInputObjectType) type).getEClass());
		List<GraphQLInputObjectField> inputFields = fieldVisibility.getFieldDefinitions(type);
		for (GraphQLInputObjectField inputTypeField : inputFields) {
			GraphQLEMFInputObjectField emfField = (GraphQLEMFInputObjectField) inputTypeField;
			Object fieldObject = object.get(inputTypeField.getName());
			if(fieldObject != null) {
				if(emfField.getEFeature().getEType() instanceof EEnum) {
					EEnum eEnum = ((EEnum) emfField.getEFeature().getEType());
					if(!emfField.getEFeature().isMany()) {
						EEnumLiteral eEnumLiteral = eEnum.getEEnumLiteral(fieldObject.toString());
						eObject.eSet(emfField.getEFeature(), eEnumLiteral.getInstance());
					} else {
						List<String> objectList = (List<String>) fieldObject;
						List<Enumerator> collect = objectList.stream().map(s -> eEnum.getEEnumLiteral(s).getInstance()).collect(Collectors.toList());
						eObject.eSet(emfField.getEFeature(), collect);
					}
				} else {
					if(emfField.getEFeature() instanceof EAttribute) {
						eObject.eSet(emfField.getEFeature(), fieldObject);
					} else {
						GraphQLEMFInputObjectType refType = (GraphQLEMFInputObjectType) unwrapAll(emfField.getType());
						EReference eReference = (EReference) emfField.getEFeature();
						
						if(eReference.isMany()) {
							List<Map<String, Object>> objectsList = (List<Map<String, Object>>) fieldObject;
							List<EObject> refList = objectsList.stream().map(map -> convertToEObject(fieldVisibility, map, refType)).collect(Collectors.toList());
							eObject.eSet(emfField.getEFeature(), refList);
						} else {
							EObject ref = convertToEObject(fieldVisibility, (Map<String, Object>) fieldObject, refType);
							eObject.eSet(emfField.getEFeature(), ref);
						}
					}
				}
			} else {
                assertNonNullInputField(inputTypeField);
            }
		}
		
		return eObject;
	}

	@SuppressWarnings("unchecked")
	private Object parseLiteral(Value inputValue, Coercing coercing, Map<String, Object> variables) {
        // the CoercingParseLiteralException exception that could happen here has been validated earlier via ValidationUtil
        return coercing.parseLiteral(inputValue,variables);
    }

    private Object coerceValueAstForList(GraphqlFieldVisibility fieldVisibility, GraphQLList graphQLList, Value value, Map<String, Object> variables) {
        if (value instanceof ArrayValue) {
            ArrayValue arrayValue = (ArrayValue) value;
            List<Object> result = new ArrayList<>();
            for (Value singleValue : arrayValue.getValues()) {
                result.add(coerceValueAst(fieldVisibility, graphQLList.getWrappedType(), singleValue, variables));
            }
            return result;
        } else {
            return Collections.singletonList(coerceValueAst(fieldVisibility, graphQLList.getWrappedType(), value, variables));
        }
    }

	private Object coerceValueAstForInputObject(GraphqlFieldVisibility fieldVisibility, GraphQLInputObjectType type, ObjectValue inputValue, Map<String, Object> variables) {
        Map<String, ObjectField> inputValueFieldsByName = mapObjectValueFieldsByName(inputValue);
        return coerceValueAstForInputObject(fieldVisibility, type, inputValueFieldsByName, variables);
    }

    
	private Object coerceValueAstForInputObject(GraphqlFieldVisibility fieldVisibility, GraphQLInputObjectType type, Map<String, ObjectField> inputValueFieldsByName, Map<String, Object> variables) {

    	EObject eObject = null;
    	
    	if(type instanceof GraphQLEMFInputObjectType) {
    		eObject = EcoreUtil.create(((GraphQLEMFInputObjectType) type).getEClass());
    	}
    	
    	Map<String, Object> result = new LinkedHashMap<>();
    
        List<GraphQLInputObjectField> inputFields = fieldVisibility.getFieldDefinitions(type);
        for (GraphQLInputObjectField inputTypeField : inputFields) {
            if (inputValueFieldsByName.containsKey(inputTypeField.getName())) {
                boolean putObjectInMap = true;

                ObjectField field = inputValueFieldsByName.get(inputTypeField.getName());
                Value fieldInputValue = field.getValue();

                Object fieldObject = null;
//                if (fieldInputValue instanceof VariableReference) {
//                    String varName = ((VariableReference) fieldInputValue).getName();
//                    if (!variables.containsKey(varName)) {
//                        putObjectInMap = false;
//                    } else {
//                    	if(inputTypeField instanceof GraphQLEMFInputObjectField && ((GraphQLEMFInputObjectField) inputTypeField).getEFeature() instanceof EReference) {
//                    		fieldObject = convertToEObject(fieldVisibility, (Map<String, Object>) variables.get(varName), (GraphQLEMFInputObjectType) inputTypeField.getType());
//                    	}
//                        fieldObject = variables.get(varName);
//                    }
//                } else {
                    fieldObject = coerceValueAst(fieldVisibility, inputTypeField.getType(), fieldInputValue, variables);
//                }

                if (fieldObject == null) {
                    if (!field.getValue().isEqualTo(NullValue.Null)) {
                        fieldObject = inputTypeField.getDefaultValue();
                    }
                }
                if (putObjectInMap) {
                	if(eObject != null) {
                		GraphQLEMFInputObjectField emfField = (GraphQLEMFInputObjectField) inputTypeField;
                		if(emfField.getEFeature().getEType() instanceof EEnum) {
                			EEnum eEnum = ((EEnum) emfField.getEFeature().getEType());
        					if(!emfField.getEFeature().isMany()) {
        						EEnumLiteral eEnumLiteral = eEnum.getEEnumLiteral(fieldObject.toString());
        						eObject.eSet(emfField.getEFeature(), eEnumLiteral.getInstance());
        					} else {
        						List<String> objectList = (List<String>) fieldObject;
        						List<Enumerator> collect = objectList.stream().map(s -> eEnum.getEEnumLiteral(s).getInstance()).collect(Collectors.toList());
        						eObject.eSet(emfField.getEFeature(), collect);
        					}
                		} else {
                			eObject.eSet(emfField.getEFeature(), fieldObject);
                		}
                	} else {
                		result.put(field.getName(), fieldObject);
                	}
                } else {
                    assertNonNullInputField(inputTypeField);
                }
            } else if (inputTypeField.getDefaultValue() != null) {
            	if(eObject == null) {
            		result.put(inputTypeField.getName(), inputTypeField.getDefaultValue());
            	}
            } else {
                assertNonNullInputField(inputTypeField);
            }
        }
        if(eObject != null) {
        	return eObject;
        }
        return result;
    }

    private void assertNonNullInputField(GraphQLInputObjectField inputTypeField) {
        if (isNonNull(inputTypeField.getType())) {
            throw new NonNullableValueCoercedAsNullException(inputTypeField);
        }
    }

    private Map<String, ObjectField> mapObjectValueFieldsByName(ObjectValue inputValue) {
        Map<String, ObjectField> inputValueFieldsByName = new LinkedHashMap<>();
        for (ObjectField objectField : inputValue.getObjectFields()) {
            inputValueFieldsByName.put(objectField.getName(), objectField);
        }
        return inputValueFieldsByName;
    }

}
