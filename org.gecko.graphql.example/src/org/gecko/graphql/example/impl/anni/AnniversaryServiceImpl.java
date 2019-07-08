package org.gecko.graphql.example.impl.anni;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.gecko.graphql.example.AnniversaryService;
import org.gecko.graphql.example.model.Anniversary;
import org.gecko.graphql.example.model.Fixture;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Realm;
import org.osgi.service.component.annotations.Component;

@Component(
		service = AnniversaryService.class)
public class AnniversaryServiceImpl implements AnniversaryService {

	@Override
	public Set<Anniversary> allAnniversaries() {
		return Fixture.INSTANCE.anniversaries().entrySet()
				.stream()
				.flatMap(e -> e.getValue().stream())
				.collect(Collectors.toSet());
	}


	@Override
	public Anniversary anniversaryById(Object id) {
		Objects.nonNull(id);

		final Optional<Anniversary> any = Fixture.INSTANCE.anniversaries().entrySet()
				.stream()
				.flatMap(entry -> entry.getValue().stream())
				.filter(anniversary -> id.toString().equals(String.valueOf(anniversary.getId())))
				.findAny();

		if (any.isEmpty()) {
			throw new IllegalArgumentException(String.format("No such anniversary - ID: %s", id.toString()));
		}

		return any.get();
	}


	@Override
	public Set<Anniversary> personAnniversaries(Person person) {
		return personAnniversaries(person, null);
	}


	@Override
	public Set<Anniversary> personAnniversaries(Person person, Realm realm) {
		Objects.nonNull(person);

		final Set<Anniversary> set = Fixture.INSTANCE.anniversaries().get(person.getId());

		if (set == null) {
			return Collections.emptySet();
		}

		if (realm == null) {
			return set;
		}

		return set.stream().filter(a -> realm.equals(a.getType().getRealm())).collect(Collectors.toSet());
	}
}
