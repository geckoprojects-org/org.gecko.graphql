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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public class Address {

	private List<Person> inhabitants = new LinkedList<Person>();
	private String number;
	private String street;
	private String city;
	private String zipCode;

	/**
	 * Returns the inhabitants.
	 * 
	 * @return the inhabitants
	 */
	public List<Person> getInhabitants() {
		return inhabitants;
	}

	/**
	 * Sets the inhabitants.
	 * 
	 * @param inhabitants the inhabitants to set
	 */
	public void setInhabitants(List<Person> inhabitants) {
		this.inhabitants.clear();
		this.inhabitants.addAll(inhabitants);
	}

	/**
	 * Returns the number.
	 * 
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Returns the street.
	 * 
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Sets the street.
	 * 
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Returns the city.
	 * 
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the zipCode.
	 * 
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zipCode.
	 * 
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

}
