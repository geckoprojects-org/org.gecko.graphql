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
public class Person {

	private Address address;
	private String firstName;
	private String lastName;
	private List<Contact> contacts = new LinkedList<>();
	/**
	 * Returns the address.
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}
	/**
	 * Sets the address.
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	/**
	 * Returns the firstName.
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * Sets the firstName.
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * Returns the lastName.
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * Sets the lastName.
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * Returns the contacts.
	 * @return the contacts
	 */
	public List<Contact> getContacts() {
		return contacts;
	}
	
	/**
	 * Sets the contacts.
	 * @param contacts the contacts to set
	 */
	public void setContacts(List<Contact> contacts) {
		this.contacts.clear();
		this.contacts.addAll(contacts);
	}
	
	
	
}
