package org.gecko.graphql.example;

import java.util.Optional;
import java.util.Set;

import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Presence;

public interface PresenceService {

	public Set<Presence> allPresence();


	public Presence presenceById(Object id);


	public Optional<Presence> personPresence(Person person);


	public Presence updatePresence(Person person, Presence presence);
}
