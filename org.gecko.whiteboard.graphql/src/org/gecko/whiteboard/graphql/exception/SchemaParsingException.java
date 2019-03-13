/**
 * Copyright (c) 2012 - 2019 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.exception;

/**
 * 
 * @author ChristophDockhorn
 * @since 12.03.2019
 */
public class SchemaParsingException extends Exception {

	/**
	 * Creates a new instance.
	 */
	public SchemaParsingException(String stack, Throwable t ) {
		super(stack + " -> " + t.getMessage(), t);
	}

}
