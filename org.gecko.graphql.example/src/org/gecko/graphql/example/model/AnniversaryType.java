package org.gecko.graphql.example.model;

public class AnniversaryType {

	private Object id;

	private String title;

	private Realm realm;


	private AnniversaryType(Builder builder) {
		id = builder.id;
		title = builder.title;
		realm = builder.realm;
	}


	public AnniversaryType() {
	}


	public Object getId() {
		return id;
	}


	public String getTitle() {
		return title;
	}


	public Realm getRealm() {
		return realm;
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private String title;
		private Realm realm;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder title(String title) {
			this.title = title;
			return this;
		}


		public Builder realm(Realm realm) {
			this.realm = realm;
			return this;
		}


		public AnniversaryType build() {
			return new AnniversaryType(this);
		}
	}

	public static interface Fields {

		public static final String ID = "id";
		public static final String REALM = "realm";
		public static final String TITLE = "title";

	}
}
