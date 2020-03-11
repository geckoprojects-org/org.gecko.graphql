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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import org.gecko.whiteboard.graphql.example.api.MyTestObject;
import org.gecko.whiteboard.graphql.example.api.MyTestService;

/**
 * 
 * @author ilenia
 * @since Feb 20, 2020
 */
@Component(scope = ServiceScope.PROTOTYPE)
public class MyTestServiceImpl implements MyTestService {

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.example.api.MyTestService#getMyTestById(java.lang.String)
	 */
	@Override
	public MyTestObject getMyTestById(String objectId) {
		MyTestObject o = new MyTestObject();
		o.setName("test-" + objectId);
		o.setDescription("This is a test");
		return o;
	}

}
