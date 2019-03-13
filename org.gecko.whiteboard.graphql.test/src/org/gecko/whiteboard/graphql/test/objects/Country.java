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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * POJO for testing purposes.
 * @author ChristophDockhorn
 * @since 05.12.2018
 */
public class Country {
	
	

	public String name;
	private String language;
	private int sizeInSqurKm;
	private long inhabitants;
	public boolean isMemberOfUnSecurityCouncil;
	private Continent continent;
	private City capital;
	private byte numberOfOfficialLanguages;
	private short embassiesWorldwide;
	private float avgAge;
	private double avgWageInDollars;
	private List<City> majorCities;
	public Date nationalHoliday;
	
	
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
	public long getInhabitants() {
		return inhabitants;
	}


	/**
	 * Sets the inhabitants.
	 * @param inhabitants the inhabitants to set
	 */
	public void setInhabitants(long inhabitants) {
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
	
	public List<Country> returnListOfCountries () {
		return new ArrayList<Country>();
	}


	/**
	 * Returns the isMemberOfUnSecurityCouncil.
	 * @return the isMemberOfUnSecurityCouncil
	 */
	public boolean isMemberOfUnSecurityCouncil() {
		return isMemberOfUnSecurityCouncil;
	}


	/**
	 * Sets the isMemberOfUnSecurityCouncil.
	 * @param isMemberOfUnSecurityCouncil the isMemberOfUnSecurityCouncil to set
	 */
	public void setMemberOfUnSecurityCouncil(boolean isMemberOfUnSecurityCouncil) {
		this.isMemberOfUnSecurityCouncil = isMemberOfUnSecurityCouncil;
	}


	/**
	 * Returns the embassiesWorldwide.
	 * @return the embassiesWorldwide
	 */
	public short getEmbassiesWorldwide() {
		return embassiesWorldwide;
	}


	/**
	 * Sets the embassiesWorldwide.
	 * @param embassiesWorldwide the embassiesWorldwide to set
	 */
	public void setEmbassiesWorldwide(short embassiesWorldwide) {
		this.embassiesWorldwide = embassiesWorldwide;
	}


	/**
	 * Returns the avgAge.
	 * @return the avgAge
	 */
	public float getAvgAge() {
		return avgAge;
	}


	/**
	 * Sets the avgAge.
	 * @param avgAge the avgAge to set
	 */
	public void setAvgAge(float avgAge) {
		this.avgAge = avgAge;
	}


	/**
	 * Returns the avgWageInDollars.
	 * @return the avgWageInDollars
	 */
	public double getAvgWageInDollars() {
		return avgWageInDollars;
	}


	/**
	 * Sets the avgWageInDollars.
	 * @param avgWageInDollars the avgWageInDollars to set
	 */
	public void setAvgWageInDollars(double avgWageInDollars) {
		this.avgWageInDollars = avgWageInDollars;
	}


	/**
	 * Returns the numberOfOfficialLanguages.
	 * @return the numberOfOfficialLanguages
	 */
	public byte getNumberOfOfficialLanguages() {
		return numberOfOfficialLanguages;
	}


	/**
	 * Sets the numberOfOfficialLanguages.
	 * @param numberOfOfficialLanguages the numberOfOfficialLanguages to set
	 */
	public void setNumberOfOfficialLanguages(byte numberOfOfficialLanguages) {
		this.numberOfOfficialLanguages = numberOfOfficialLanguages;
	}


	/**
	 * Returns the nationalHoliday.
	 * @return the nationalHoliday
	 */
	public Date getNationalHoliday() {
		return nationalHoliday;
	}


	/**
	 * Sets the nationalHoliday.
	 * @param nationalHoliday the nationalHoliday to set
	 */
	public void setNationalHoliday(Date nationalHoliday) {
		this.nationalHoliday = nationalHoliday;
	}


	/**
	 * Returns the capital.
	 * @return the capital
	 */
	public City getCapital() {
		return capital;
	}


	/**
	 * Sets the capital.
	 * @param capital the capital to set
	 */
	public void setCapital(City capital) {
		this.capital = capital;
	}


	/**
	 * Returns the majorCities.
	 * @return the majorCities
	 */
	public List<City> getMajorCities() {
		return majorCities;
	}


	/**
	 * Sets the majorCities.
	 * @param majorCities the majorCities to set
	 */
	public void setMajorCities(List<City> majorCities) {
		this.majorCities = majorCities;
	}

	
	
}
