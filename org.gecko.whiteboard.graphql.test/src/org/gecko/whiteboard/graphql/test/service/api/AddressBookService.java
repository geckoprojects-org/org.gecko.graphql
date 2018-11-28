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

import java.util.List;
import java.util.Optional;

import org.gecko.whiteboard.graphql.annotation.GraphqlArgument;
import org.gecko.whiteboard.graphql.test.dto.Address;
import org.gecko.whiteboard.graphql.test.dto.Person;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
public interface AddressBookService {
	
	public Address saveAddress(Address toSave);
	
	public List<Address> getAllAddresses();
	public List<Address> getAddressesByStreet(String name);
	public List<Address> getAddressesByQuery(MyQuery query);
	
	public Optional<Person> getPersonByName(@GraphqlArgument("firstName") String firstName, String lastName);
	public List<Person> getAllPersons();
	public Person savePerson(Person person);
	
}
