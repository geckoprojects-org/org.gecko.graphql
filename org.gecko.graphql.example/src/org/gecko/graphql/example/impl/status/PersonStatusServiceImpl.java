package org.gecko.graphql.example.impl.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.graphql.example.PersonStatusService;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.PersonStatus;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		service = PersonStatusService.class)
public class PersonStatusServiceImpl implements PersonStatusService {

	private static final Logger LOG = LoggerFactory.getLogger(PersonStatusServiceImpl.class);

	@Override
	public Set<PersonStatus> allStatus() {
		return Fixture.INSTANCE.status().entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toSet());
	}


	@Override
	public PersonStatus statusById(Object id) {
		Objects.nonNull(id);

		final Optional<PersonStatus> any = Fixture.INSTANCE.status().entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream())
				.filter(status -> id.toString().equals(String.valueOf(status.getId())))
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such status - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Set<PersonStatus> personStatus(Person person) {
		Objects.nonNull(person);

		final Set<PersonStatus> set = Fixture.INSTANCE.status().get(person.getId());

		if (set == null) {
			return Collections.emptySet();
		}

		return set;
	}


	@Override
	public Optional<PersonStatus> personStatus(Person person, LocalDate at) {
		Objects.nonNull(person);
		Objects.nonNull(at);

		return personStatus(person)
				.stream()
				.filter(status -> isCurrent(status, at))
				.findFirst();
	}


	private boolean isCurrent(PersonStatus status, LocalDate at) {
		final boolean beforeStart = status.getStart() != null && at.isBefore(status.getStart());
		final boolean afterEnd = status.getEnd() != null && at.isAfter(status.getEnd());

		return !beforeStart && !afterEnd;
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
