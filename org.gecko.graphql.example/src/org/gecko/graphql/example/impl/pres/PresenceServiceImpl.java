package org.gecko.graphql.example.impl.pres;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.graphql.example.PresenceService;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Presence;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresenceServiceImpl implements PresenceService {

	private static final Logger LOG = LoggerFactory.getLogger(PresenceServiceImpl.class);

	@Override
	public Set<Presence> allPresence() {
		return Fixture.INSTANCE.presences().entrySet()
				.stream()
				.map(e -> e.getValue())
				.collect(Collectors.toSet());
	}


	@Override
	public Presence presenceById(Object id) {
		Objects.nonNull(id);

		final Optional<Presence> any = Fixture.INSTANCE.presences().entrySet()
				.stream()
				.filter(e -> id.equals(e.getKey()))
				.map(e -> e.getValue())
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such presence - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Optional<Presence> personPresence(Person person) {
		Objects.nonNull(person);

		final Presence presence = Fixture.INSTANCE.presences().get(person.getId());

		if (presence == null) {
			return Optional.empty();
		}

		return Optional.of(presence);
	}


	@Override
	public Presence updatePresence(Person person, Presence presence) {
		Objects.nonNull(person);
		Objects.nonNull(presence);

		return Fixture.INSTANCE.presences().put(person.getId(), presence);
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
