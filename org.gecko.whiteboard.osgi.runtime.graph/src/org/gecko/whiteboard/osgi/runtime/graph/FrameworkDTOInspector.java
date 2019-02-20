/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.osgi.runtime.graph;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.osgi.dto.DTO;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import graphql.schema.StaticDataFetcher;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;

@Component(name = "FrameworkDTOInspector", service = {}, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class FrameworkDTOInspector implements GraphQLQueryProvider, GraphQLTypesProvider, ServiceTrackerCustomizer<Object, Object>{

	private ServiceTracker<Object, Object> serviceTracker;
	
	Map<ServiceReference<Object>, List<GraphQLFieldDefinition>> runtimeServices = new HashMap<>();
	
	List<GraphQLFieldDefinition> fieldsToAdd = new LinkedList<GraphQLFieldDefinition>();

	private ComponentContext ctx;
	
	AtomicLong changeCount = new AtomicLong(0);

	private ServiceRegistration<?> registerService;
	
	Map<Class<?>, GraphQLType> cache = new HashMap<Class<?>, GraphQLType>();

	@Activate
	public void activate(ComponentContext ctx) {
		this.ctx = ctx;
		try {
			serviceTracker = new ServiceTracker<Object, Object>(ctx.getBundleContext(), FrameworkUtil.createFilter("(service.id=*)"), this);
			serviceTracker.open();
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		Dictionary<String, Object> props = getServiceProps();
		registerService = ctx.getBundleContext().registerService(new String[] {GraphQLQueryProvider.class.getName(), GraphQLTypesProvider.class.getName()}, this, props );
	}
	
	/**
	 * @return
	 */
	private Dictionary<String, Object> getServiceProps() {
		Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
		
		hashtable.put(Constants.SERVICE_CHANGECOUNT, changeCount.incrementAndGet());
		
		return hashtable;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.servlet.GraphQLTypesProvider#getTypes()
	 */
	@Override
	public Collection<GraphQLType> getTypes() {
		return cache.values();
	}
	
	public void updateChangeCount() {
		if(registerService != null) {
			registerService.setProperties(getServiceProps());
		}
	}

	@Deactivate
	public void deactivate() {
		serviceTracker.close();
		registerService.unregister();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see graphql.servlet.GraphQLQueryProvider#getQueries()
	 */
	@Override
	public Collection<GraphQLFieldDefinition> getQueries() {
		return fieldsToAdd;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public Object addingService(ServiceReference<Object> reference) {
		List<GraphQLFieldDefinition> fields = analyizePotentialRuntimeService(reference);
		
		if(fields.isEmpty()) {
			return null;
		}
		
		fieldsToAdd.addAll(fields);
		runtimeServices.put(reference, fields);
		updateChangeCount();
		return ctx.getBundleContext().getService(reference);
	}
	
	/**
	 * Tries to get all declared object classes
	 * @param serviceReference the
	 * @return
	 */
	private List<Class<?>> getDeclaredObjectClasses(ServiceReference<Object> serviceReference) {
		String[] objectClasses = (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
		List<Class<?>> interfaces = new ArrayList<Class<?>>(objectClasses.length);
		for(String objectClass : objectClasses) {
			try {
				BundleWiring bundleWiring = serviceReference.getBundle().adapt(BundleWiring.class);
				interfaces.add(bundleWiring.getClassLoader().loadClass(objectClass));
			} catch (ClassNotFoundException e) {
				System.out.println("============== Could not load " + objectClass);
//				throw new RuntimeException("Cant load class for " + objectClass, e);
			}
		}
		return interfaces;
	}

	/**
	 * @param reference
	 */
	private List<GraphQLFieldDefinition> analyizePotentialRuntimeService(ServiceReference<Object> reference) {
		List<Class<?>> declaredObjectClasses = getDeclaredObjectClasses(reference);
		
		List<GraphQLFieldDefinition> objectTypes = new LinkedList<GraphQLFieldDefinition>();
		
		DTOTypeBuilder typeBuidler = new DTOTypeBuilder(cache);
		
		for(Class<?> clazz : declaredObjectClasses) {
		
			GraphQLObjectType.Builder objectBuilder = null;
			List<Method> methods = getDTOMethods(clazz);
			if(!methods.isEmpty()) {
				if(objectBuilder == null) {
					objectBuilder = GraphQLObjectType.newObject();
				}
				methods.stream()
				.map(m -> this.createReferenceField(m.getName(), new DataFetcherImplementation(m), (GraphQLOutputType) typeBuidler.buildType(m.getReturnType())))
				.forEach(objectBuilder::field);
				objectTypes.add(createReferenceField(clazz.getSimpleName(), new StaticDataFetcher(ctx.getBundleContext().getServiceObjects(reference)), objectBuilder.name(clazz.getSimpleName()).build()));
			}
		}

		return objectTypes;
	}
	
	private final class DataFetcherImplementation implements DataFetcher<Object> {
	
		/** method */
		private final Method method;

		/**
		 * Creates a new instance.
		 * @param method
		 */
		private DataFetcherImplementation(Method method) {
			this.method = method;
		}

		@Override
		public Object get(DataFetchingEnvironment environment) throws Exception {
			ServiceObjects<Object> serviceObjects = environment.getSource();
			Object[] parameters = new Object[method.getParameterCount()];
			Object toInvokeOn = serviceObjects.getService();
			try {
				Object result = method.invoke(toInvokeOn, parameters);
				return result;
			} catch (InvocationTargetException  e) {
				throw (Exception) e.getTargetException();
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw e;
			} finally {
				serviceObjects.ungetService(toInvokeOn);
			}
		}
	}

	public GraphQLFieldDefinition createReferenceField(String name, DataFetcher<?> datafetcher, GraphQLOutputType type) {
		GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
				.name(name)
				.dataFetcher(datafetcher)
				.type(type);
		return builder.build();
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	private List<Method> getDTOMethods(Class<?> clazz) {
		return Arrays.asList(clazz.getMethods()).stream()
				.filter(m -> DTO.class.isAssignableFrom(m.getReturnType()))
				.filter(m -> m.getParameterCount() == 0)
				.collect(Collectors.toList());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference<Object> reference, Object service) {
		// TODO Auto-generated method stub
		
	}

	/* 
	 * (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference<Object> reference, Object service) {
		ctx.getBundleContext().ungetService(reference);
		List<GraphQLFieldDefinition> remove = runtimeServices.remove(reference);
		if(remove != null) {
			fieldsToAdd.removeAll(remove);
		}
		updateChangeCount();
	}

	// TODO: class provided by template

}
