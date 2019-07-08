package org.gecko.graphql.example;

import java.util.Set;

import org.gecko.graphql.example.model.Anniversary;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.Realm;

public interface AnniversaryService {

	public Set<Anniversary> allAnniversaries();


	public Anniversary anniversaryById(Object id);


	public Set<Anniversary> personAnniversaries(Person person);


	public Set<Anniversary> personAnniversaries(Person person, Realm realm);
}
