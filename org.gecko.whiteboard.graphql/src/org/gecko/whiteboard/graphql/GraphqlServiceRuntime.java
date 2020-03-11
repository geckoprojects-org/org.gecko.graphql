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
package org.gecko.whiteboard.graphql;

import org.gecko.whiteboard.graphql.annotation.RequireGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.dto.RuntimeDTO;

/**
 * The GraphQLServiceRuntime service represents the runtime information of a
 * GraphQL Whiteboard implementation.
 * <p>
 * It provides access to DTOs representing the current state of the service.
 * <p>
 * The GraphQLServiceRuntime service must be registered with the
 * {@link GeckoGraphQLConstants#GRAPHQL_SERVICE_ENDPOINT} service
 * property.
 * @author Juergen Albert
 * @since 6 Nov 2018
 */
@RequireGraphQLWhiteboard
public interface GraphqlServiceRuntime {

	/**
	 * Return the runtime DTO representing the current state.
	 * 
	 * @return The runtime DTO.
	 */
	RuntimeDTO getRuntimeDTO();
	
}
