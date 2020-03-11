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
package org.gecko.whiteboard.graphql.example.api;

import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.annotation.GraphqlDocumentation;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface MyTestGQLService{

	@GraphqlDocumentation("Here you can put the method description and this will be displayed in GraphQL")
	MyTestObject getMyTestById(
			@GraphqlDocumentation("This is the first argument")
			@GraphqlArgument(value = "objectId", optional = false) 
			String objectId);
	
}
