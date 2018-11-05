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
package org.gecko.whiteboard.graphql.test.service.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gecko.whiteboard.graphql.test.dto.Address;
import org.gecko.whiteboard.graphql.test.dto.Contact;
import org.gecko.whiteboard.graphql.test.dto.ContactType;
import org.gecko.whiteboard.graphql.test.dto.Person;
import org.gecko.whiteboard.graphql.test.service.api.AddressBookService;
import org.gecko.whiteboard.graphql.test.service.api.MyQuery;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
@Component(
		property="graphql.service.name=TestService"
		)
public class AddressBookServiceImpl implements AddressBookService{

	List<Address> addresses = new LinkedList<Address>();
	List<Person> persons = new LinkedList<Person>();
	
	@Activate
	public void activate() {
		Address gera = createAddress("Mark", "1", "07545", "Gera");
		Address jena = createAddress("Mark", "2", "07743", "Jena");

		addresses.add(gera);
		addresses.add(jena);
		
		persons.add(createPerson("Olaf", "Tester", gera, createContact(ContactType.EMAIL, "o.tester@web.de"), createContact(ContactType.MOBILE, "01772222222")));
		persons.add(createPerson("Olafine", "Tester", gera, createContact(ContactType.EMAIL, "ola.tester@web.de"), createContact(ContactType.MOBILE, "01772222223")));

		persons.add(createPerson("Kerstin", "Schmidt", jena, createContact(ContactType.EMAIL, "k.schmidt@web.de"), createContact(ContactType.MOBILE, "01772222224")));
		persons.add(createPerson("Jürgen", "Biernot", jena, createContact(ContactType.EMAIL, "juergen.b@web.de"), createContact(ContactType.LANDLINE, "03641490000")));
		persons.add(createPerson("Claudia", "Klautnicht", jena, createContact(ContactType.EMAIL, "c.cn@web.de"), createContact(ContactType.MOBILE, "01772222226")));
	}
	
	/**
	 * @param string
	 * @param string2
	 * @param gera
	 * @param createContact
	 * @param createContact2
	 * @return
	 */
	private Person createPerson(String fn, String ln, Address a, Contact... contacts) {
		Person p = new Person();
		p.setFirstName(fn);
		p.setLastName(ln);
		p.setAddress(a);
		a.getInhabitants().add(p);
		p.getContacts().addAll(Arrays.asList(contacts));
		return p;
	}

	/**
	 * @param type
	 * @param value
	 * @return
	 */
	private Contact createContact(ContactType type, String value) {
		Contact c = new Contact();
		c.setType(type);
		c.setValue(value);
		return c;
	}

	private Address createAddress(String street, String number, String zipCode, String city) {
		Address a = new Address();
		a.setStreet(street);
		a.setNumber(number);
		a.setZipCode(zipCode);
		a.setCity(city);
		return a;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAllAdresses()
	 */
	@Override
	public List<Address> getAllAdresses() {
		return addresses;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAdressesByStreet(java.lang.String)
	 */
	@Override
	public List<Address> getAdressesByStreet(String name) {
		return addresses.stream().filter(a -> a.getStreet().toLowerCase().equals(name.toLowerCase())).collect(Collectors.toList());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAdressesByQuery(org.gecko.whiteboard.graphql.test.service.api.Query)
	 */
	@Override
	public List<Address> getAdressesByQuery(MyQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getPersonByName(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Person> getPersonByName(String firstName, String lastName) {
		return persons.stream().filter((p) -> (p.getFirstName().toLowerCase().equals(firstName.toLowerCase()) && p.getLastName().toLowerCase().equals(lastName.toLowerCase()))).findFirst();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAllPersons()
	 */
	@Override
	public List<Person> getAllPersons() {
		return persons;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#saveAddress(org.gecko.whiteboard.graphql.test.dto.Address)
	 */
	@Override
	public Address saveAddress(Address toSave) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#savePerson(org.gecko.whiteboard.graphql.test.dto.Person)
	 */
	@Override
	public Person savePerson(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

}
