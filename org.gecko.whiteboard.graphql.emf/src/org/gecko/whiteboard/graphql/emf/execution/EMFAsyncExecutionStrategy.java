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
package org.gecko.whiteboard.graphql.emf.execution;

import static graphql.execution.ExecutionStepInfo.newExecutionStepInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import graphql.collect.ImmutableKit;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherResult;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.FetchedValue;
import graphql.execution.MergedField;
import graphql.language.Argument;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.util.FpKit;

/**
 * 
 * @author Juergen Albert
 * @since 16 Nov 2018
 */
public class EMFAsyncExecutionStrategy extends AsyncExecutionStrategy {
	
	/*
	 * (non-Javadoc)
	 * @see graphql.execution.ExecutionStrategy#createExecutionStepInfo(graphql.execution.ExecutionContext, graphql.execution.ExecutionStrategyParameters, graphql.schema.GraphQLFieldDefinition, graphql.schema.GraphQLObjectType)
	 */
	@Override
	protected ExecutionStepInfo createExecutionStepInfo(ExecutionContext executionContext,
			ExecutionStrategyParameters parameters, GraphQLFieldDefinition fieldDefinition,
			GraphQLObjectType fieldContainer) {
		MergedField field = parameters.getField();
		ExecutionStepInfo parentStepInfo = parameters.getExecutionStepInfo();
		GraphQLOutputType fieldType = fieldDefinition.getType();
		List<GraphQLArgument> fieldArgDefs = fieldDefinition.getArguments();
		Supplier<Map<String, Object>> argumentValues = ImmutableKit::emptyMap;
		//
		// no need to create args at all if there are none on the field def
		//
		if (!fieldArgDefs.isEmpty()) {
			List<Argument> fieldArgs = field.getArguments();
			GraphQLCodeRegistry codeRegistry = executionContext.getGraphQLSchema().getCodeRegistry();
			argumentValues = FpKit.intraThreadMemoize(() -> EMFValuesResolver.getArgumentValues(codeRegistry, fieldArgDefs,
					fieldArgs, executionContext.getCoercedVariables()));
		}

		return newExecutionStepInfo().type(fieldType).fieldDefinition(fieldDefinition).fieldContainer(fieldContainer)
				.field(field).path(parameters.getPath()).parentInfo(parentStepInfo).arguments(argumentValues).build();
	}	
	
	/*
	 * (non-Javadoc)
	 * @see graphql.execution.ExecutionStrategy#unboxPossibleDataFetcherResult(graphql.execution.ExecutionContext, graphql.execution.ExecutionStrategyParameters, java.lang.Object)
	 */
	@Override
	protected FetchedValue unboxPossibleDataFetcherResult(ExecutionContext executionContext,
			ExecutionStrategyParameters parameters, Object result) {

		if (result instanceof DataFetcherResult) {
			DataFetcherResult<?> dataFetcherResult = (DataFetcherResult<?>) result;
			
			// @formatter:off
			dataFetcherResult.getErrors().stream()
				.map(relError -> new AbsoluteGraphQLError(parameters, relError))
				.forEach(executionContext::addError);
			// @formatter:off			
			
			executionContext.addErrors(dataFetcherResult.getErrors());

			Object localContext = dataFetcherResult.getLocalContext();
			if (localContext == null) {
				// if the field returns nothing then they get the context of their parent field
				localContext = parameters.getLocalContext();
			}
			return FetchedValue.newFetchedValue()
					.fetchedValue(executionContext.getValueUnboxer().unbox(dataFetcherResult.getData()))
					.rawFetchedValue(dataFetcherResult.getData()).errors(dataFetcherResult.getErrors())
					.localContext(localContext).build();
		} else {
			return FetchedValue.newFetchedValue().fetchedValue(executionContext.getValueUnboxer().unbox(result))
					.rawFetchedValue(result).localContext(parameters.getLocalContext()).build();
		}
	}	
}
