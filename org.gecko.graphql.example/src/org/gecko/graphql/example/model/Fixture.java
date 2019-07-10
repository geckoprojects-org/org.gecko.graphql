package org.gecko.graphql.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public enum Fixture {

	INSTANCE;

	private final Map<Object, Set<Anniversary>> anniversaries = new HashMap<>();
	private final Map<Object, Set<Contact>> contacts = new HashMap<>();
	private final Map<Object, Set<Phone>> phones = new HashMap<>();
	private final Map<Object, Presence> presences = new HashMap<>();
	private final Map<Object, Set<PersonStatus>> status = new HashMap<>();

	private final Set<Person> persons = new HashSet<>();

	private final AnniversaryType birthday = AnniversaryType.builder()
			.id(UUID.randomUUID())
			.realm(Realm.PRIVATE)
			.title("Geburtstag")
			.build();
	private final AnniversaryType jubilee = AnniversaryType.builder()
			.id(UUID.randomUUID())
			.realm(Realm.PUBLIC)
			.title("Dienstjubiläum")
			.build();

	private final Person person1 = Person.builder()
			.id(Long.valueOf(1))
			.gender(Gender.DIVERSE)
			.preferredName("")
			.surname("Example")
			.userName("Example")
			.build();
	private final Person person2 = Person.builder()
			.id(Long.valueOf(2))
			.gender(Gender.FEMALE)
			.preferredName("Karin")
			.surname("Muster")
			.userName("MusterK")
			.build();
	private final Person person3 = Person.builder()
			.id(Long.valueOf(3))
			.gender(Gender.MALE)
			.preferredName("Max")
			.surname("Mustermann")
			.userName("MustermM")
			.build();

	private final Contact contact1Private = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("example@gmail.com")
			.recipient("Example")
			.street("Löbdergraben 1")
			.realm(Realm.PRIVATE)
			.build();
	private final Contact contact1Public = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("example@jena.de")
			.recipient("Example")
			.street("Am Anger 13")
			.realm(Realm.PUBLIC)
			.build();
	private final Contact contact2Private = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("karin.muster@web.de")
			.recipient("Karin Muster")
			.street("Löbdergraben 3")
			.realm(Realm.PRIVATE)
			.build();
	private final Contact contact2Public = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("karin.muster@jena.de")
			.recipient("Karin Muster")
			.street("Am Anger 26")
			.realm(Realm.PUBLIC)
			.build();
	private final Contact contact3Private = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("max.mustermann@gmx.de")
			.recipient("Max Mustermann")
			.street("Löbdergraben 5")
			.realm(Realm.PRIVATE)
			.build();
	private final Contact contact3Public = Contact.builder()
			.id(UUID.randomUUID())
			.city("Jena")
			.country("Deutschland")
			.postalCode("07743")
			.email("max.mustermann@jena.de")
			.recipient("Max Mustermann")
			.street("Am Anger 15")
			.realm(Realm.PUBLIC)
			.build();


	private Fixture() {
		persons.addAll(List.of(person1, person2, person3));

		anniversaries.put(
				person1.getId(),
				Set.of(
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(1994, Month.MAY, 8))
								.type(birthday)
								.build(),
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(2019, Month.JUNE, 1))
								.description("Diensteintritt")
								.type(jubilee)
								.build()));
		anniversaries.put(
				person2.getId(),
				Set.of(
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(1980, Month.DECEMBER, 6))
								.type(birthday)
								.build(),
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(2009, Month.SEPTEMBER, 1))
								.description("Diensteintritt")
								.type(jubilee)
								.build()));
		anniversaries.put(
				person3.getId(),
				Set.of(
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(1981, Month.MARCH, 19))
								.type(birthday)
								.build(),
						Anniversary.builder()
								.id(UUID.randomUUID())
								.date(LocalDate.of(2016, Month.JULY, 1))
								.description("Diensteintritt")
								.type(jubilee)
								.build()));


		contacts.put(person1.getId(), Set.of(contact1Private, contact1Public));
		contacts.put(person2.getId(), Set.of(contact2Private, contact2Public));
		contacts.put(person3.getId(), Set.of(contact3Private, contact3Public));

		phones.put(
				contact1Public.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Festnetz")
								.number("03641495555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Fax")
								.number("03641495556")
								.type(PhoneType.FAX)
								.build()));
		phones.put(
				contact1Private.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Festnetz")
								.number("03641555555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Handy")
								.number("0151555555")
								.type(PhoneType.MOBILE)
								.build()));

		phones.put(
				contact2Public.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Festnetz")
								.number("03641495555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Fax")
								.number("03641495556")
								.type(PhoneType.FAX)
								.build()));
		phones.put(
				contact2Private.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Festnetz")
								.number("03641555555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Handy")
								.number("0151555555")
								.type(PhoneType.MOBILE)
								.build()));

		phones.put(
				contact3Public.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Festnetz")
								.number("03641495555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Fax")
								.number("03641495556")
								.type(PhoneType.FAX)
								.build()));
		phones.put(
				contact3Private.getId(),
				Set.of(
						Phone.builder()
								.id(UUID.randomUUID())
								.label("Festnetz")
								.number("03641555555")
								.type(PhoneType.LANDLINE)
								.build(),
						Phone.builder()
								.id(UUID.randomUUID())
								.primary(true)
								.label("Handy")
								.number("0151555555")
								.type(PhoneType.MOBILE)
								.build()));

		presences.put(
				person1.getId(),
				Presence.builder()
						.message("AFK")
						.since(LocalDateTime.now())
						.status(PresenceType.AWAY)
						.build());
		presences.put(
				person2.getId(),
				Presence.builder()
						.message("Abwesend")
						.since(LocalDateTime.now())
						.status(PresenceType.OFFLINE)
						.build());
		presences.put(
				person3.getId(),
				Presence.builder()
						.message("Anwesend")
						.since(LocalDateTime.now())
						.status(PresenceType.ONLINE)
						.build());

		status.put(
				person1.getId(),
				Set.of(
						PersonStatus.builder()
								.id(UUID.randomUUID())
								.start(LocalDate.of(2019, Month.JUNE, 1))
								.end(LocalDate.of(2020, Month.MAY, 31))
								.type(PersonStatusType.PRESENT)
								.build(),
						PersonStatus.builder()
								.id(UUID.randomUUID())
								.start(LocalDate.of(2020, Month.JUNE, 1))
								.end(LocalDate.of(2021, Month.MAY, 31))
								.title("Sabbatical")
								.type(PersonStatusType.ABSENT)
								.build()));
		status.put(
				person2.getId(),
				Set.of(
						PersonStatus.builder()
								.id(UUID.randomUUID())
								.start(LocalDate.of(2018, Month.NOVEMBER, 11))
								.end(LocalDate.of(2020, Month.JANUARY, 1))
								.title("Babypause")
								.type(PersonStatusType.ABSENT)
								.build(),
						PersonStatus.builder()
								.id(UUID.randomUUID())
								.start(LocalDate.of(2020, Month.JANUARY, 2))
								.type(PersonStatusType.ACTIVE)
								.build()));
		status.put(
				person3.getId(),
				Set.of(
						PersonStatus.builder()
								.id(UUID.randomUUID())
								.start(LocalDate.of(2016, Month.JULY, 1))
								.type(PersonStatusType.PRESENT)
								.build()));
	}


	public Map<Object, Set<Anniversary>> anniversaries() {
		return anniversaries;
	}


	public Map<Object, Set<Contact>> contacts() {
		return contacts;
	}


	public Map<Object, Set<Phone>> phones() {
		return phones;
	}


	public Map<Object, Set<PersonStatus>> status() {
		return status;
	}


	public Person person1() {
		return person1;
	}


	public Person person2() {
		return person2;
	}


	public Person person3() {
		return person3;
	}


	public Set<Person> persons() {
		return persons;
	}


	public Map<Object, Presence> presences() {
		return presences;
	}
}
