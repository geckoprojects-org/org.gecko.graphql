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
import java.util.UUID;
import java.util.stream.Collectors;

import org.gecko.whiteboard.graphql.annotation.GraphqlMutationService;
import org.gecko.whiteboard.graphql.annotation.GraphqlQueryService;
import org.gecko.whiteboard.graphql.test.dto.Address;
import org.gecko.whiteboard.graphql.test.dto.Contact;
import org.gecko.whiteboard.graphql.test.dto.ContactType;
import org.gecko.whiteboard.graphql.test.dto.Person;
import org.gecko.whiteboard.graphql.test.service.api.AddressBookService;
import org.gecko.whiteboard.graphql.test.service.api.AnotherInterface;
import org.gecko.whiteboard.graphql.test.service.api.MyQuery;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author jalbert
 * @since 2 Nov 2018
 */
//@Component(
//		)
//@GraphqlQueryServiceName("TestQuery")
//@GraphqlMutationServiceName("TestMutation")
//@GraphqlQueryService(value ="org.gecko.whiteboard.graphql.test.service.api.AddressBookService")
//@GraphqlMutationService(value = "org.gecko.whiteboard.graphql.test.service.api.AnotherInterface")
public class AddressBookServiceImpl implements AddressBookService, AnotherInterface{

	List<Address> addresses = new LinkedList<Address>();
	List<Person> persons = new LinkedList<Person>();
	
	@Activate
	public void activate() {
		Address gera = createAddress("1", "Markt", "1", "07545", "Gera");
		Address jena = createAddress("2", "Markt", "2", "07743", "Jena");

		addresses.add(gera);
		addresses.add(jena);
		
		persons.add(createPerson("1", "Olaf", "Tester", gera, createContact(ContactType.EMAIL, "o.tester@web.de"), createContact(ContactType.MOBILE, "01772222222")));
		persons.add(createPerson("2","Olafine", "Tester", gera, createContact(ContactType.EMAIL, "ola.tester@web.de"), createContact(ContactType.MOBILE, "01772222223")));

		persons.add(createPerson("3", "Kerstin", "Schmidt", jena, createContact(ContactType.EMAIL, "k.schmidt@web.de"), createContact(ContactType.MOBILE, "01772222224")));
		persons.add(createPerson("4", "Jürgen", "Biernot", jena, createContact(ContactType.EMAIL, "juergen.b@web.de"), createContact(ContactType.LANDLINE, "03641490000")));
		persons.add(createPerson("5", "Claudia", "Klautnicht", jena, createContact(ContactType.EMAIL, "c.cn@web.de"), createContact(ContactType.MOBILE, "01772222226")));
	}
	
	/**
	 * @param string
	 * @param string2
	 * @param gera
	 * @param createContact
	 * @param createContact2
	 * @return
	 */
	private Person createPerson(String id, String fn, String ln, Address a, Contact... contacts) {
		Person p = new Person();
		p.setId(id);
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

	private Address createAddress(String id, String street, String number, String zipCode, String city) {
		Address a = new Address();
		a.setId(id);
		a.setStreet(street);
		a.setNumber(number);
		a.setZipCode(zipCode);
		a.setCity(city);
		return a;
	}

	private Optional<Address> getAddressById(String id) {
		return addresses.stream().filter(a -> id.equals(a.getId())).findFirst();
	}

	private Optional<Person> getPersonById(String id) {
		return persons.stream().filter(p -> id.equals(p.getId())).findFirst();
	}
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAllAdresses()
	 */
	@Override
	public List<Address> getAllAddresses() {
		return addresses;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAdressesByStreet(java.lang.String)
	 */
	@Override
	public List<Address> getAddressesByStreet(String name) {
		return addresses.stream().filter(a -> a.getStreet().toLowerCase().equals(name.toLowerCase())).collect(Collectors.toList());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AddressBookService#getAdressesByQuery(org.gecko.whiteboard.graphql.test.service.api.Query)
	 */
	@Override
	public List<Address> getAddressesByQuery(MyQuery query) {
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
		if(person.getAddress() != null) {
			Optional<Address> addressById = getAddressById(person.getAddress().getId());
			addressById.ifPresent(person::setAddress);
		}
		if(person.getId() == null){
			person.setId(UUID.randomUUID().toString());
		}
		persons.add(person);
		return person;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.whiteboard.graphql.test.service.api.AnotherInterface#halloWorld(java.lang.String)
	 */
	@Override
	public String halloWorld(String name) {
		return "Hallo " + name;
	}

}
