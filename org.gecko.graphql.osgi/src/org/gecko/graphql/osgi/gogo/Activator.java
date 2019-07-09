package org.gecko.graphql.osgi.gogo;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.service.command.CommandProcessor;
import org.gecko.graphql.SchemaContributor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	ServiceTracker<Object, Object> tracker;
	private BundleContext bundleContext;

	private final Map<ServiceReference<Object>, ServiceRegistration<SchemaContributor>> map = new ConcurrentHashMap<ServiceReference<Object>, ServiceRegistration<SchemaContributor>>();


	@Override
	public void start(BundleContext bundleContext) throws Exception {

		this.bundleContext = bundleContext;
		if (tracker == null) {

			final String filterString = String.format("(&(%s=*)(%s=*))",
					CommandProcessor.COMMAND_SCOPE, CommandProcessor.COMMAND_FUNCTION);

			final Filter filter = bundleContext.createFilter(filterString);
			tracker = new ServiceTracker<Object, Object>(bundleContext, filter, null) {

				@Override
				public Object addingService(ServiceReference<Object> reference) {
					final Object result = super.addingService(reference);

					register(reference);

					return result;
				}


				@Override
				public void removedService(ServiceReference<Object> reference,
						Object service) {

					unregister(reference);
					super.removedService(reference, service);

				}
			};
		}
		tracker.open();
	}


	protected void unregister(ServiceReference<Object> reference) {

		final ServiceRegistration<SchemaContributor> serviceRegistration = map.get(reference);
		serviceRegistration.unregister();
	}


	protected void register(ServiceReference<Object> reference) {

		final Map<String, Object> properties = Converters.standardConverter().convert(reference.getProperties()).to(new TypeReference<Map<String, Object>>() {
		});
		final Object service = bundleContext.getService(reference);

		final GoGoSchemaContributor goGoSchemaContributor = new GoGoSchemaContributor(service, properties);

		final Hashtable<String, Object> props = new Hashtable<>();
		final ServiceRegistration<SchemaContributor> serviceRegistration = bundleContext.registerService(SchemaContributor.class, goGoSchemaContributor, props);

		map.put(reference, serviceRegistration);

	}


	@Override
	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}
}
