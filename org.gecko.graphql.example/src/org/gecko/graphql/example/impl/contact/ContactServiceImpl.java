package org.gecko.graphql.example.impl.contact;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.graphql.example.ContactService;
import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Realm;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		service = ContactService.class)
public class ContactServiceImpl implements ContactService {

	private static final Logger LOG = LoggerFactory.getLogger(ContactServiceImpl.class);

	@Override
	public Set<Contact> allContacts() {
		return Fixture.INSTANCE.contacts().entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toSet());
	}


	@Override
	public Contact contactById(Object id) {
		Objects.nonNull(id);

		final Optional<Contact> any = Fixture.INSTANCE.contacts().entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream())
				.filter(contact -> id.toString().equals(String.valueOf(contact.getId())))
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such contact - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Set<Contact> personContacts(Person person) {
		return personContacts(person, null);
	}


	@Override
	public Set<Contact> personContacts(Person person, Realm realm) {
		Objects.nonNull(person);

		final Set<Contact> set = Fixture.INSTANCE.contacts().get(person.getId());

		if (set == null) {
			return Collections.emptySet();
		}

		if (realm == null) {
			return set;
		}

		return set.stream().filter(c -> realm.equals(c.getRealm())).collect(Collectors.toSet());
	}


	@Activate
	private void activate() {
		LOG.debug("{} activated", getClass().getSimpleName());
	}


	@Deactivate
	private void deactivate() {
		LOG.debug("{} deactivated", getClass().getSimpleName());
	}
}
