package org.gecko.graphql.example.model;

import java.time.LocalDateTime;

public class Presence {

	private PresenceType status;

	private LocalDateTime since;

	private String message;


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
}
