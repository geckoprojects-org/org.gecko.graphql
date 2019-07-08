package org.gecko.graphql;

import java.util.Objects;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;

public class DataFetcherCoordinates<T> {

	private final FieldCoordinates coordinates;

	private final DataFetcher<T> dataFetcher;


	public DataFetcherCoordinates(FieldCoordinates coordinates, DataFetcher<T> dataFetcher) {
		Objects.nonNull(coordinates);
		Objects.nonNull(dataFetcher);

		this.coordinates = coordinates;
		this.dataFetcher = dataFetcher;
	}


	public FieldCoordinates coordinates() {
		return coordinates;
	}


	public DataFetcher<T> dataFetcher() {
		return dataFetcher;
	}
}
