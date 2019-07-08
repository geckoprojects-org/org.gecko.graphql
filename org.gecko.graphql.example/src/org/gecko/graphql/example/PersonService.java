package org.gecko.graphql.example;

import java.util.Optional;
import java.util.Set;

import org.gecko.graphql.example.model.Person;

public interface PersonService {

	public Set<Person> allPersons();


	public Person personById(Object id);


	public Optional<Person> personByUserName(String userName);
}
