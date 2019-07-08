package org.gecko.graphql.example;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.PersonStatus;

public interface PersonStatusService {

	public Set<PersonStatus> allStatus();


	public PersonStatus statusById(Object id);


	public Set<PersonStatus> personStatus(Person person);


	public Optional<PersonStatus> personStatus(Person person, LocalDate at);
}
