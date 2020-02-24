/**
 * Copyright (c) 2012 - 2020 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.example.impl;

import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.annotation.RequireGraphQLWhiteboard;
import org.gecko.whiteboard.graphql.example.api.MyTestGQLService;
import org.gecko.whiteboard.graphql.example.api.MyTestObject;
import org.gecko.whiteboard.graphql.example.api.MyTestService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * 
 * @author ilenia
 * @since Feb 20, 2020
 */
@Component(scope = ServiceScope.PROTOTYPE) // The prototype is useful here, because the Service can be called in parallel and every caller should get its exclusive instance
@GraphqlQueryService(value = MyTestGQLService.class, name = "MyTestQuery")
@RequireGraphQLWhiteboard
public class MyTestGQLServiceImpl implements MyTestGQLService {
	
	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private MyTestService testService;

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.example.api.MyTestService#getMyTestById(java.lang.String)
	 */
	@Override
	public MyTestObject getMyTestById(String objectId) {
		return testService.getMyTestById(objectId);
	}

}
