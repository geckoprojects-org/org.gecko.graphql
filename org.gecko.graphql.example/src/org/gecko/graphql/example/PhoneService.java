package org.gecko.graphql.example;

import java.util.Set;

import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Phone;

public interface PhoneService {

	public Set<Phone> allPhones();


	public Phone phoneById(Object id);


	public Set<Phone> contactPhones(Contact contact);
}
