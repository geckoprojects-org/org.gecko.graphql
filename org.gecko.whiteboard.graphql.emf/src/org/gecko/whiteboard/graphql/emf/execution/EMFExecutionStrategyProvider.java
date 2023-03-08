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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.kickstart.execution.config.ExecutionStrategyProvider;

/**
 * 
 * @author Juergen Albert
 * @since 16 Nov 2018
 */
@Component(scope = ServiceScope.PROTOTYPE)
public class EMFExecutionStrategyProvider implements ExecutionStrategyProvider {

    private final ExecutionStrategy queryExecutionStrategy = new EMFAsyncExecutionStrategy();
    private final ExecutionStrategy mutationExecutionStrategy = queryExecutionStrategy;
    private final ExecutionStrategy subscriptionExecutionStrategy = new SubscriptionExecutionStrategy();

    @Override
    public ExecutionStrategy getQueryExecutionStrategy() {
        return queryExecutionStrategy;
    }

    @Override
    public ExecutionStrategy getMutationExecutionStrategy() {
        return mutationExecutionStrategy;
    }

    @Override
    public ExecutionStrategy getSubscriptionExecutionStrategy() {
        return subscriptionExecutionStrategy;
    }
}
