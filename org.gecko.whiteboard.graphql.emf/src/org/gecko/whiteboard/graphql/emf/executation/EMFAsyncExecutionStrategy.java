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

import static graphql.schema.DataFetchingEnvironmentBuilder.newDataFetchingEnvironment;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import graphql.execution.Async;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherResult;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionId;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.ValuesResolver;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.DataFetchingFieldSelectionSetImpl;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.visibility.GraphqlFieldVisibility;

/**
 * 
 * @author Juergen Albert
 * @since 16 Nov 2018
 */
@SuppressWarnings("rawtypes")
public class EMFAsyncExecutionStrategy extends AsyncExecutionStrategy {

	protected ValuesResolver valuesResolver = new EMFValuesResolver();

	private static final Logger LOG = Logger.getLogger(EMFAsyncExecutionStrategy.class.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see graphql.execution.ExecutionStrategy#fetchField(graphql.execution.
	 * ExecutionContext, graphql.execution.ExecutionStrategyParameters)
	 */
	protected CompletableFuture<Object> fetchField(ExecutionContext executionContext,
			ExecutionStrategyParameters parameters) {
		Field field = parameters.getField().get(0);
		GraphQLObjectType parentType = (GraphQLObjectType) parameters.getExecutionStepInfo().getUnwrappedNonNullType();
		GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(), parentType, field);

		GraphqlFieldVisibility fieldVisibility = executionContext.getGraphQLSchema().getFieldVisibility();
		Map<String, Object> argumentValues = valuesResolver.getArgumentValues(fieldVisibility, fieldDef.getArguments(),
				field.getArguments(), executionContext.getVariables());

		GraphQLOutputType fieldType = fieldDef.getType();
		DataFetchingFieldSelectionSet fieldCollector = DataFetchingFieldSelectionSetImpl.newCollector(executionContext,
				fieldType, parameters.getField());
		ExecutionStepInfo executionStepInfo = createExecutionStepInfo(executionContext, parameters, fieldDef);

		DataFetchingEnvironment environment = newDataFetchingEnvironment(executionContext)
				.source(parameters.getSource()).arguments(argumentValues).fieldDefinition(fieldDef)
				.fields(parameters.getField()).fieldType(fieldType).executionStepInfo(executionStepInfo)
				.parentType(parentType).selectionSet(fieldCollector).build();

		Instrumentation instrumentation = executionContext.getInstrumentation();

		InstrumentationFieldFetchParameters instrumentationFieldFetchParams = new InstrumentationFieldFetchParameters(
				executionContext, fieldDef, environment, parameters);
		InstrumentationContext<Object> fetchCtx = instrumentation.beginFieldFetch(instrumentationFieldFetchParams);

		CompletableFuture<Object> fetchedValue;
		DataFetcher dataFetcher = fieldDef.getDataFetcher();
		dataFetcher = instrumentation.instrumentDataFetcher(dataFetcher, instrumentationFieldFetchParams);
		ExecutionId executionId = executionContext.getExecutionId();
		try {
			LOG.fine(String.format("'%s' fetching field '%s' using data fetcher '%s'...", executionId, executionStepInfo.getPath(),
					dataFetcher.getClass().getName()));
			Object fetchedValueRaw = dataFetcher.get(environment);
			LOG.fine(String.format("'%s' field '%s' fetch returned '%s'", executionId, executionStepInfo.getPath(),
					fetchedValueRaw == null ? "null" : fetchedValueRaw.getClass().getName()));

			fetchedValue = Async.toCompletableFuture(fetchedValueRaw);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, String.format("'%s', field '%s' fetch threw exception: ", executionId, executionStepInfo.getPath()), e);

			fetchedValue = new CompletableFuture<>();
			fetchedValue.completeExceptionally(e);
		}
		fetchCtx.onDispatched(fetchedValue);
		return fetchedValue.handle((result, exception) -> {
			fetchCtx.onCompleted(result, exception);
			if (exception != null) {
				handleFetchingException(executionContext, parameters, field, fieldDef, argumentValues, environment,
						exception);
				return null;
			} else {
				return result;
			}
		}).thenApply(result -> unboxPossibleDataFetcherResult(executionContext, parameters, result))
				.thenApply(this::unboxPossibleOptional);
	}

	protected Object unboxPossibleDataFetcherResult(ExecutionContext executionContext, ExecutionStrategyParameters parameters,
			Object result) {
		if (result instanceof DataFetcherResult) {
			//noinspection unchecked
			DataFetcherResult<?> dataFetcherResult = (DataFetcherResult) result;
			dataFetcherResult.getErrors().stream().map(relError -> new AbsoluteGraphQLError(parameters, relError))
					.forEach(executionContext::addError);
			return dataFetcherResult.getData();
		} else {
			return result;
		}
	}

	private void handleFetchingException(ExecutionContext executionContext, ExecutionStrategyParameters parameters,
			Field field, GraphQLFieldDefinition fieldDef, Map<String, Object> argumentValues,
			DataFetchingEnvironment environment, Throwable e) {
		DataFetcherExceptionHandlerParameters handlerParameters = DataFetcherExceptionHandlerParameters
				.newExceptionParameters().executionContext(executionContext).dataFetchingEnvironment(environment)
				.argumentValues(argumentValues).field(field).fieldDefinition(fieldDef).path(parameters.getPath())
				.exception(e).build();

		dataFetcherExceptionHandler.accept(handlerParameters);

		parameters.deferredErrorSupport().onFetchingException(parameters, e);
	}

}
