package org.gecko.whiteboard.graphql.emf.datafetcher;

import org.osgi.framework.ServiceObjects;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * A wrapped {@link DataFetcher} that delegates the call to a prototype service. After the call is finished the Call will be unget
 * @author jalbert
 * @since 16 Apr 2019
 */
public class PrototypeDataFetcher implements DataFetcher<Object> {
	private final ServiceObjects<DataFetcher<Object>> serviceObjects;

	public PrototypeDataFetcher(ServiceObjects<DataFetcher<Object>> serviceObjects) {
		this.serviceObjects = serviceObjects;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		DataFetcher<Object> dataFetcher = serviceObjects.getService();
		try {
			return dataFetcher.get(environment);
		} finally {
			serviceObjects.ungetService(dataFetcher);
		}
	}
	
	public static PrototypeDataFetcher newFetcher(ServiceObjects<DataFetcher<Object>> serviceObjects) {
		return new PrototypeDataFetcher(serviceObjects);
	}
}
