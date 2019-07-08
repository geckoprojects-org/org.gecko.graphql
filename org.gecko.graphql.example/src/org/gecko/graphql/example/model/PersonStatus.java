package org.gecko.graphql.example.model;

import java.time.LocalDate;

public class PersonStatus {

	private Object id;

	private LocalDate end;
	private LocalDate start;

	private String title;

	private PersonStatusType type;


	private PersonStatus(Builder builder) {
		id = builder.id;
		end = builder.end;
		start = builder.start;
		title = builder.title;
		type = builder.type;
	}


	public PersonStatus() {
	}


	public Object getId() {
		return id;
	}


	public LocalDate getEnd() {
		return end;
	}


	public LocalDate getStart() {
		return start;
	}


	public String getTitle() {
		return title;
	}


	public PersonStatusType getType() {
		return type;
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private LocalDate end;
		private LocalDate start;
		private String title;
		private PersonStatusType type;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder end(LocalDate end) {
			this.end = end;
			return this;
		}


		public Builder start(LocalDate start) {
			this.start = start;
			return this;
		}


		public Builder title(String title) {
			this.title = title;
			return this;
		}


		public Builder type(PersonStatusType type) {
			this.type = type;
			return this;
		}


		public PersonStatus build() {
			return new PersonStatus(this);
		}
	}

	public static interface Fields {

		public static final String END = "end";
		public static final String ID = "id";
		public static final String START = "start";
		public static final String TITLE = "title";
		public static final String TYPE = "type";

	}
}
