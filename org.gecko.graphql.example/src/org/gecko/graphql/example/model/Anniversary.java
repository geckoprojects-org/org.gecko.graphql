package org.gecko.graphql.example.model;

import java.time.LocalDate;

public class Anniversary {

	public static final String NAME = "anniversary";
	public static final String NAME_COLLECTION = "anniversaries";

	private Object id;

	private LocalDate date;

	private String description;

	private AnniversaryType type;


	private Anniversary(Builder builder) {
		id = builder.id;
		date = builder.date;
		description = builder.description;
		type = builder.type;
	}


	public Anniversary() {
	}


	public Object getId() {
		return id;
	}


	public LocalDate getDate() {
		return date;
	}


	public String getDescription() {
		return description;
	}


	public AnniversaryType getType() {
		return type;
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private LocalDate date;
		private String description;
		private AnniversaryType type;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}


		public Builder description(String description) {
			this.description = description;
			return this;
		}


		public Builder type(AnniversaryType type) {
			this.type = type;
			return this;
		}


		public Anniversary build() {
			return new Anniversary(this);
		}
	}

	public static interface Fields {

		public static final String DATE = "date";
		public static final String DESCRIPTION = "description";
		public static final String ID = "id";
		public static final String TYPE = "type";

	}
}
