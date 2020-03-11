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
package org.gecko.whiteboard.graphql.test;

/**
 * 
 * @author jalbert
 * @since 5 Dec 2018
 */
public class ToDoTest {
	
	/**
	 * Tests that a method parameters of the following are not nullable and marked as such in the schema. 
	 * Additionally it must produce a comprehencive error response, when a invalid query is fired.
	 * The scalars to test are:
	 * 
	 * int, float, short, long, boolean, byte, char
	 */
	public void testScalarTypesAsMethodParametersAreMandatory() {
		
	}

	/**
	 * We need a Service that throws an exception, when a method is called. We have to make sure, that the error 
	 * message will be transported as an error response. This will most likely right now. 
	 */
	public void testExceptionHandling() {
		
	}
	
}
