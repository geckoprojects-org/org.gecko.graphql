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
package org.gecko.whiteboard.graphql.test.service.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public class MyQuery {
	private Map<String, String> parameters = new HashMap<>();

	/**
	 * Returns the parameters.
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters.clear();
		this.parameters.putAll(parameters);
	}
}
