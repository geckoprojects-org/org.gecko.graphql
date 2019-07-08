package org.gecko.graphql.example.impl.person;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.gecko.graphql.example.PersonService;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Person;
import org.osgi.service.component.annotations.Component;

@Component(
		service = PersonService.class)
public class PersonServiceImpl implements PersonService {

	@Override
	public Set<Person> allPersons() {
		return Fixture.INSTANCE.persons();
	}


	@Override
	public Person personById(Object id) {
		Objects.nonNull(id);

		final Optional<Person> any = Fixture.INSTANCE.persons()
				.stream()
				.filter(person -> id.equals(person.getId()))
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such user - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Optional<Person> personByUserName(String userName) {
		Objects.nonNull(userName);

		final Optional<Person> any = Fixture.INSTANCE.persons().stream().filter(p -> userName.equals(p.getUserName())).findAny();

		if (any.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(personById(any.get().getId()));
	}
}
