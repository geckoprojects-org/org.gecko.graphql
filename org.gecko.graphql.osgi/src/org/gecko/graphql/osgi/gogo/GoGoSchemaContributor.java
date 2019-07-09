package org.gecko.graphql.osgi.gogo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.felix.service.command.Descriptor;
import org.gecko.graphql.DataFetcherCoordinates;
import org.gecko.graphql.SchemaContributor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;

public class GoGoSchemaContributor implements SchemaContributor {

	DefaultGraphqlTypeBuilder defaultGraphqlTypeBuilder = new DefaultGraphqlTypeBuilder();
	private static final Logger LOG = LoggerFactory.getLogger(GoGoSchemaContributor.class);

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Set<GraphQLFieldDefinition> queries = new HashSet<>();
	private final Set<GraphQLFieldDefinition> mutations = new HashSet<>();

	private final Set<DataFetcherCoordinates<?>> dataFetcherCoordinates = new HashSet<>();
	private final Set<GraphQLType> additionalTypes = new HashSet<>();


	private final Map<String, Object> properties;

	private final Object service;


	public GoGoSchemaContributor(Object service, Map<String, Object> properties) {

		this.service = service;
		this.properties = properties;

	}


	@Override
	public Set<GraphQLFieldDefinition> queries() {
		if (!initialized.get()) {
			init();
		}

		// TODO: List gogo Commands
		return queries;
	}


	@Override
	public Set<GraphQLFieldDefinition> mutations() {
		if (!initialized.get()) {
			init();
		}
		// TODO: Execute gogo Commands
		return mutations;
	}


	@Override
	public Set<DataFetcherCoordinates<?>> dataFetcherCoordinates() {
		if (!initialized.get()) {
			init();
		}

		return dataFetcherCoordinates;
	}


	@Override
	public Set<GraphQLType> additionalTypes() {
		if (!initialized.get()) {
			init();
		}

		return additionalTypes;
	}


	private synchronized void init() {

		final Map<Object, GraphQLType> typeMapping = new HashMap<>();
		final String scope = (String) properties.get("osgi.command.scope");
		final String[] functions = (String[]) properties.get("osgi.command.function");
		System.out.println(functions);

		if (functions != null) {

			for (final String function : functions) {

				final Method[] methods = service.getClass().getMethods();

				if (methods != null) {

					final List<Method> functionMethods = Stream.of(methods).filter(m -> m.getName().equals(function)).collect(Collectors.toList());
					int i = 0;
					for (final Method method : functionMethods) {

						String qualifyer = "";
						if (functionMethods.size() > 1) {
							qualifyer = "_" + qualifyer + i++;
						}
						final String NAME_GOGO = scope + "_" + function + qualifyer;
							System.out.println(method);
							final Descriptor annotationDescriptorMethod = method.getAnnotation(org.apache.felix.service.command.Descriptor.class);

							String methodDescription = "";
							if (annotationDescriptorMethod != null) {

								methodDescription = annotationDescriptorMethod.value();
							}

						final GraphQLOutputType returnType = (GraphQLOutputType) defaultGraphqlTypeBuilder.buildType(method.getGenericReturnType(),
								typeMapping,
								false);

							final graphql.schema.GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition()
									.name(NAME_GOGO)
									.description(methodDescription)
								.type(returnType);// pojo
							final Parameter[] parameters = method.getParameters();

							if (parameters != null) {

								for (final Parameter parameter : parameters) {

									final Descriptor annotationDescriptorParameter = parameter.getAnnotation(org.apache.felix.service.command.Descriptor.class);

									String parameterDescription = "";
								if (annotationDescriptorParameter != null) {

									parameterDescription = annotationDescriptorParameter.value();
									}

									String absentValue = org.apache.felix.service.command.Parameter.UNSPECIFIED;
									String presentValue = org.apache.felix.service.command.Parameter.UNSPECIFIED;
									final String pName = genParamName(parameter);

									final org.apache.felix.service.command.Parameter annotationParameter = parameter
											.getAnnotation(org.apache.felix.service.command.Parameter.class);
									if (annotationParameter != null) {

										absentValue = annotationParameter.absentValue();
										presentValue = annotationParameter.presentValue();
									}

									builder.argument(GraphQLArgument.newArgument()
											.name(pName)
											.description(parameterDescription)
											.type(Scalars.GraphQLString));
								}

							}
							mutations.add(builder.build());
							final DataFetcher<Object> gogoDF = new DataFetcher<Object>() {
								@Override
								public Object get(DataFetchingEnvironment environment) {

									final List<String> otherValues = new ArrayList<String>();
									if (method.getParameters() != null) {

										for (final Parameter param : method.getParameters()) {

											final String pName = genParamName(param);

											if (environment.containsArgument(pName)) {
												// todo absend value
												final String val = environment.getArgument(pName);
												otherValues.add(val);
											}

										}
									}

									try {
										Object o = null;
										if (otherValues.isEmpty()) {
											o = method.invoke(service);
										} else {

											o = method.invoke(service, otherValues);
										}
										return o;
									} catch (final Throwable e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return null;

								}

							};

							dataFetcherCoordinates.add(new DataFetcherCoordinates<Object>(
									FieldCoordinates.coordinates(SchemaContributor.MUTATION, NAME_GOGO),
									gogoDF));


					}
				}

			}
		}

		additionalTypes.addAll(typeMapping.values());
		System.out.println(typeMapping);
		initialized.getAndSet(true);

	}


	@Activate
	private void activate() {
		LOG.debug("{} activated", getClass().getSimpleName());

	}


	@Deactivate
	private void deactivate() {
		LOG.debug("{} deactivated", getClass().getSimpleName());
	}


	private static String genParamName(Parameter parameter) {
		final org.apache.felix.service.command.Parameter annotationParameter = parameter.getAnnotation(org.apache.felix.service.command.Parameter.class);
		String name = parameter.getName();
		if (annotationParameter != null) {
			final String[] names = annotationParameter.names();

			if (names != null && names.length > 0) {
				String longestName = "";
				for (final String n : names) {
					longestName = n.length() > longestName.length() ? n : longestName;
				}

				longestName = longestName.replaceAll("-", "").trim();

				if (!longestName.isEmpty()) {
					name = longestName;

				}

			}

		}
		return name;
	}
}
