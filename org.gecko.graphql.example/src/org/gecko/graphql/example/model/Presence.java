package org.gecko.graphql.example.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class Presence {

	public static final String NAME = "presence";

	private PresenceType status;

	private LocalDateTime since;

	private String message;


	public static Presence fromMap(Map<String, Object> input) {
		System.out.println("Presence:\n"
				+ input.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue() + " (" + e.getValue().getClass() + ")")
						.collect(Collectors.joining("\n\t")));

		final String message = input.get(Fields.MESSAGE) instanceof String ? String.class.cast(input.get(Fields.MESSAGE)) : null;
		final LocalDateTime since = input.get(Fields.SINCE) instanceof LocalDateTime ? LocalDateTime.class.cast(input.get(Fields.SINCE)) : null;
		final PresenceType status = input.get(Fields.STATUS) instanceof String
				? PresenceType.valueOf(String.class.cast(input.get(Fields.STATUS)))
				: PresenceType.OFFLINE;

		return builder()
				.message(message)
				.since(since)
				.status(status)
				.build();
	}

	private Presence(Builder builder) {
		status = builder.status;
		since = builder.since;
		message = builder.message;
	}


	public Presence() {
	}


	public PresenceType getStatus() {
		return status;
	}


	public LocalDateTime getSince() {
		return since;
	}


	public void setSince(LocalDateTime since) {
		this.since = since;
	}


	public String getMessage() {
		return message;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb
				.append(getClass().getSimpleName())
				.append(" [status=").append(status)
				.append(", since=").append(since)
				.append(", message=").append(message)
				.append("]");

		return sb.toString();
	}


	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private PresenceType status;
		private LocalDateTime since;
		private String message;


		private Builder() {
		}


		public Builder status(PresenceType status) {
			this.status = status;
			return this;
		}


		public Builder since(LocalDateTime since) {
			this.since = since;
			return this;
		}


		public Builder message(String message) {
			this.message = message;
			return this;
		}


		public Presence build() {
			return new Presence(this);
		}
	}

	public static interface Fields {

		public static final String MESSAGE = "message";
		public static final String SINCE = "since";
		public static final String STATUS = "status";

	}
}
