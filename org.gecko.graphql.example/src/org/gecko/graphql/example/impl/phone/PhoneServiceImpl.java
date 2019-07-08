package org.gecko.graphql.example.impl.phone;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.graphql.example.PhoneService;
import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Phone;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		service = PhoneService.class)
public class PhoneServiceImpl implements PhoneService {

	private static final Logger LOG = LoggerFactory.getLogger(PhoneServiceImpl.class);


	@Override
	public Set<Phone> allPhones() {
		return Fixture.INSTANCE.phones().entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toSet());
	}


	@Override
	public Phone phoneById(Object id) {
		Objects.nonNull(id);

		final Optional<Phone> any = Fixture.INSTANCE.phones().entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream())
				.filter(phone -> id.toString().equals(String.valueOf(phone.getId())))
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such phone - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Set<Phone> contactPhones(Contact contact) {
		Objects.nonNull(contact);

		final Set<Phone> set = Fixture.INSTANCE.phones().get(contact.getId());

		if (set == null) {
			return Collections.emptySet();
		}

		return set;
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
