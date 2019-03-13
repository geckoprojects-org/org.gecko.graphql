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
package org.gecko.whiteboard.graphql.test.objects;

/**
 * Pojo without complex Objects.
 * @author ChristophDockhorn
 * @since 13.03.2019
 */
public class City {

	String name;
	int inhabitants;
	boolean isCapitalCity;
	byte famousSights;
	short districts;
	float avgAge;
	double avgWageInDollars;
	long touristsInTotal;
	
	
	
	/**
	 * Creates a new instance.
	 */
	public City() {
		// TODO Auto-generated constructor stub
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



	/**
	 * Returns the isCapitalCity.
	 * @return the isCapitalCity
	 */
	public boolean isCapitalCity() {
		return isCapitalCity;
	}



	/**
	 * Sets the isCapitalCity.
	 * @param isCapitalCity the isCapitalCity to set
	 */
	public void setCapitalCity(boolean isCapitalCity) {
		this.isCapitalCity = isCapitalCity;
	}



	/**
	 * Returns the famousSights.
	 * @return the famousSights
	 */
	public byte getFamousSights() {
		return famousSights;
	}



	/**
	 * Sets the famousSights.
	 * @param famousSights the famousSights to set
	 */
	public void setFamousSights(byte famousSights) {
		this.famousSights = famousSights;
	}



	/**
	 * Returns the districts.
	 * @return the districts
	 */
	public short getDistricts() {
		return districts;
	}



	/**
	 * Sets the districts.
	 * @param districts the districts to set
	 */
	public void setDistricts(short districts) {
		this.districts = districts;
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
	 * Returns the touristsInTotal.
	 * @return the touristsInTotal
	 */
	public long getTouristsInTotal() {
		return touristsInTotal;
	}



	/**
	 * Sets the touristsInTotal.
	 * @param touristsInTotal the touristsInTotal to set
	 */
	public void setTouristsInTotal(long touristsInTotal) {
		this.touristsInTotal = touristsInTotal;
	}
	
	

}
