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
public class Country {
	
	

	private String name;
	private String language;
	private int sizeInSqurKm;
	private int inhabitants;
	private Continent continent;
	
	
	/**
	 * Returns the continent.
	 * @return the continent
	 */
	public Continent getContinent() {
		return continent;
	}


	/**
	 * Sets the continent.
	 * @param continent the continent to set
	 */
	public void setContinent(Continent continent) {
		this.continent = continent;
	}


	/**
	 * Returns the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * Sets the name.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Returns the language.
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}


	/**
	 * Sets the language.
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}


	/**
	 * Returns the sizeInSqurKm.
	 * @return the sizeInSqurKm
	 */
	public int getSizeInSqurKm() {
		return sizeInSqurKm;
	}


	/**
	 * Sets the sizeInSqurKm.
	 * @param sizeInSqurKm the sizeInSqurKm to set
	 */
	public void setSizeInSqurKm(int sizeInSqurKm) {
		this.sizeInSqurKm = sizeInSqurKm;
	}


	/**
	 * Returns the inhabitants.
	 * @return the inhabitants
	 */
	public int getInhabitants() {
		return inhabitants;
	}


	/**
	 * Sets the inhabitants.
	 * @param inhabitants the inhabitants to set
	 */
	public void setInhabitants(int inhabitants) {
		this.inhabitants = inhabitants;
	}


	
	public Country setCountry(Continent continent, String name, String language, int sizeInSqurKm, int inhabitants) {
		Country country = new Country();
		country.setContinent(continent);
		country.setName(name);
		country.setLanguage(language);
		country.setSizeInSqurKm(sizeInSqurKm);
		country.setInhabitants(inhabitants);

		return country;
	}
	
	

}
