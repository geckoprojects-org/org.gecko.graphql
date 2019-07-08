package org.gecko.graphql.example;

import java.util.Set;

import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Realm;

public interface ContactService {

	public Set<Contact> allContacts();


	public Contact contactById(Object id);


	public Set<Contact> personContacts(Person person);


	public Set<Contact> personContacts(Person person, Realm realm);
}
