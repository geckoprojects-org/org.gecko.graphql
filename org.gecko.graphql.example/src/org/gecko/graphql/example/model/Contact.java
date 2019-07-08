package org.gecko.graphql.example.model;

import java.util.HashSet;
import java.util.Set;

public class Contact {

	public static final String NAME = "contact";
	public static final String NAME_COLLECTION = "contacts";

	private Object id;

	private String city;
	private String country;
	private String email;
	private String postalCode;
	private String recipient;
	private String street;

	private Realm realm;

	private Set<Phone> phones;


	private Contact(Builder builder) {
		id = builder.id;
		city = builder.city;
		country = builder.country;
		email = builder.email;
		postalCode = builder.postalCode;
		recipient = builder.recipient;
		street = builder.street;
		realm = builder.realm;
		phones = builder.phones;
	}


	public Contact() {
	}


	public Object getId() {
		return id;
	}


	public String getCity() {
		return city;
	}


	public String getCountry() {
		return country;
	}


	public String getEmail() {
		return email;
	}


	public String getPostalCode() {
		return postalCode;
	}


	public String getRecipient() {
		return recipient;
	}


	public String getStreet() {
		return street;
	}


	public Realm getRealm() {
		return realm;
	}


	public Set<Phone> getPhones() {
		return phones;
	}


	public void setPhones(Set<Phone> phones) {
		this.phones = phones;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(getClass().getSimpleName())
				.append(" [id=").append(id)
				.append(", city=").append(city)
				.append(", country=").append(country)
				.append(", email=").append(email)
				.append(", postalCode=").append(postalCode)
				.append(", recipient=").append(recipient)
				.append(", street=").append(street)
				.append(", realm=").append(realm)
				.append(", phones=").append(phones)
				.append("]");

		return sb.toString();
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private String city;
		private String country;
		private String email;
		private String postalCode;
		private String recipient;
		private String street;
		private Realm realm;
		private Set<Phone> phones;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder city(String city) {
			this.city = city;
			return this;
		}


		public Builder country(String country) {
			this.country = country;
			return this;
		}


		public Builder email(String email) {
			this.email = email;
			return this;
		}


		public Builder postalCode(String postalCode) {
			this.postalCode = postalCode;
			return this;
		}


		public Builder recipient(String recipient) {
			this.recipient = recipient;
			return this;
		}


		public Builder street(String street) {
			this.street = street;
			return this;
		}


		public Builder realm(Realm realm) {
			this.realm = realm;
			return this;
		}


		public Builder phone(Phone phone) {
			if (phones == null) {
				phones = new HashSet<>();
			}

			phones.add(phone);

			return this;
		}


		public Builder phones(Set<Phone> phones) {
			this.phones = phones;
			return this;
		}


		public Contact build() {
			return new Contact(this);
		}
	}

	public static interface Fields {

		public static final String CITY = "city";
		public static final String COUNTRY = "country";
		public static final String EMAIL = "email";
		public static final String ID = "id";
		public static final String PHONES = "phones";
		public static final String POSTAL_CODE = "postalCode";
		public static final String REALM = "realm";
		public static final String RECIPIENT = "recipient";
		public static final String STREET = "street";

	}
}
