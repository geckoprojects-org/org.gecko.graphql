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
package org.gecko.whiteboard.graphql.example.api;

/**
 * 
 * @author ilenia
 * @since Feb 20, 2020
 */
public class MyTestObject {

	private String name;
	private String description;

	/**
	 * Sets the description.
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Returns the description.
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
