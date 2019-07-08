package org.gecko.graphql.example.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Person {

	public static final String NAME = "person";
	public static final String NAME_COLLECTION = "persons";

	private Object id;

	private String preferredName;
	private String surname;
	private String userName;

	private Gender gender;

	private PersonStatus status;

	private List<Anniversary> anniversaries;
	private Set<Contact> contacts;


	private Person(Builder builder) {
		id = builder.id;
		preferredName = builder.preferredName;
		surname = builder.surname;
		userName = builder.userName;
		gender = builder.gender;
		status = builder.status;
		anniversaries = builder.anniversaries;
		contacts = builder.contacts;
	}


	public Person() {
	}


	public Object getId() {
		return id;
	}


	public String getPreferredName() {
		return preferredName;
	}


	public String getSurname() {
		return surname;
	}


	public String getUserName() {
		return userName;
	}


	public Gender getGender() {
		return gender;
	}


	public PersonStatus getStatus() {
		return status;
	}


	public void setStatus(PersonStatus status) {
		this.status = status;
	}


	public List<Anniversary> getAnniversaries() {
		return anniversaries;
	}


	public void setAnniversaries(List<Anniversary> anniversaries) {
		this.anniversaries = anniversaries;
	}


	public Set<Contact> getContacts() {
		return contacts;
	}


	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private String preferredName;
		private String surname;
		private String userName;
		private Gender gender;
		private PersonStatus status;
		private List<Anniversary> anniversaries;
		private Set<Contact> contacts;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder preferredName(String preferredName) {
			this.preferredName = preferredName;
			return this;
		}


		public Builder surname(String surname) {
			this.surname = surname;
			return this;
		}


		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}


		public Builder gender(Gender gender) {
			this.gender = gender;
			return this;
		}


		public Builder status(PersonStatus status) {
			this.status = status;
			return this;
		}


		public Builder anniversary(Anniversary anniversary) {
			if (anniversaries == null) {
				anniversaries = new ArrayList<>();
			}

			anniversaries.add(anniversary);

			return this;
		}


		public Builder anniversaries(List<Anniversary> anniversaries) {
			this.anniversaries = anniversaries;
			return this;
		}


		public Builder contact(Contact contact) {
			if (contacts == null) {
				contacts = new HashSet<>();
			}

			contacts.add(contact);

			return this;
		}


		public Builder contacts(Set<Contact> contacts) {
			this.contacts = contacts;
			return this;
		}


		public Person build() {
			return new Person(this);
		}
	}

	public static interface Fields {

		public static final String ANNIVERSARIES = "anniversaries";
		public static final String CONTACTS = "contacts";
		public static final String GENDER = "gender";
		public static final String ID = "id";
		public static final String PREFERRED_NAME = "preferredName";
		public static final String STATUS = "status";
		public static final String SURNAME = "surname";
		public static final String USER_NAME = "userName";

	}
}
