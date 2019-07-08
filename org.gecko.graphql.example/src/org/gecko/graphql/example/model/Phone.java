package org.gecko.graphql.example.model;

public class Phone {

	private Object id;

	private boolean primary;

	private String label;
	private String number;

	private PhoneType type;


	private Phone(Builder builder) {
		id = builder.id;
		primary = builder.primary;
		label = builder.label;
		number = builder.number;
		type = builder.type;
	}


	public Phone() {
	}


	public Object getId() {
		return id;
	}


	public boolean isPrimary() {
		return primary;
	}


	public String getLabel() {
		return label;
	}


	public String getNumber() {
		return number;
	}


	public PhoneType getType() {
		return type;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb
				.append(getClass().getSimpleName())
				.append(" [id=").append(id)
				.append(", primary=").append(primary)
				.append(", label=").append(label)
				.append(", number=").append(number)
				.append(", type=").append(type)
				.append("]");

		return sb.toString();
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object id;
		private boolean primary;
		private String label;
		private String number;
		private PhoneType type;


		private Builder() {
		}


		public Builder id(Object id) {
			this.id = id;
			return this;
		}


		public Builder primary(boolean primary) {
			this.primary = primary;
			return this;
		}


		public Builder label(String label) {
			this.label = label;
			return this;
		}


		public Builder number(String number) {
			this.number = number;
			return this;
		}


		public Builder type(PhoneType type) {
			this.type = type;
			return this;
		}


		public Phone build() {
			return new Phone(this);
		}
	}

	public static interface Fields {

		public static final String ID = "id";
		public static final String LABEL = "label";
		public static final String NUMBER = "number";
		public static final String PRIMARY = "primary";
		public static final String TYPE = "type";

	}
}
