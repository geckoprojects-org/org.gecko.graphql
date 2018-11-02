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
package org.gecko.whiteboard.graphql.test.dto;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public class Contact {

	private ContactType type;
	private String value;

	/**
	 * Returns the value.
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the value.
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the type.
	 * @return the type
	 */
	public ContactType getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 * @param type the type to set
	 */
	public void setType(ContactType type) {
		this.type = type;
	}
	
}
