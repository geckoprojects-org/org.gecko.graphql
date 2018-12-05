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
package org.gecko.whiteboard.graphql.test.objects;

/**
 * 
 * @author ChristophDockhorn
 * @since 05.12.2018
 */
public enum Continent {
	
	AFRICA("Africa"), AMERICA("America"), ANTARCTICA("Antarctica"), ASIA("Asia"), AUSTRALIA("Australia"), EUROPE("Europe");
	
	String key;
	
	Continent (String key) {
		this.key = key;
	}
	
	public Continent getContinent(String continent) {
		if ("Africa".equalsIgnoreCase(continent)) {
			return AFRICA;
		}
		if ("America".equalsIgnoreCase(continent)) {
			return AMERICA;
		}
		if ("Asia".equalsIgnoreCase(continent)) {
			return ASIA;
		}
		if ("Australia".equalsIgnoreCase(continent)) {
			return AUSTRALIA;
		}
		if ("Europe".equalsIgnoreCase(continent)) {
			return EUROPE;
		}
		else {
			return ANTARCTICA;
		}
		
		
	}
	
}
