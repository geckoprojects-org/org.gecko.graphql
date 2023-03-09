package org.gecko.whiteboard.graphql.emf.schema;

import static graphql.Assert.assertNotNull;
import static graphql.util.FpKit.getByName;
import static graphql.util.FpKit.valuesToList;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.eclipse.emf.ecore.EStructuralFeature;

import graphql.PublicApi;
import graphql.language.FieldDefinition;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.DataFetcherFactory;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLUnionType;
import graphql.schema.PropertyDataFetcher;

public class GraphQLEMFFieldDefinition extends GraphQLFieldDefinition {

	private EStructuralFeature feature;
	
	// @formatter:off
	public GraphQLEMFFieldDefinition(String name, 
									 String description, 
									 GraphQLOutputType type,
									 DataFetcherFactory<?> dataFetcherFactory, 
									 List<GraphQLArgument> arguments, 
									 String deprecationReason,
									 List<GraphQLDirective> directives, 
									 FieldDefinition definition, 
									 EStructuralFeature feature) {
		super(name,
			  description,
			  type,
			  dataFetcherFactory,
			  arguments,
			  deprecationReason,
			  directives,
			  Collections.emptyList(), // List<GraphQLAppliedDirective>
			  definition);

		this.feature = feature;
	}
	// @formatter:off

    /**
	 * Returns the feature.
	 * @return the feature
	 */
	public EStructuralFeature getFeature() {
		return feature;
	}

    public static Builder newEMFFieldDefinition(GraphQLEMFFieldDefinition existing) {
        return new Builder(existing);
    }

    public static Builder newEMFFieldDefinition() {
        return new Builder();
    }

    @PublicApi
    public static class Builder {

        private String name;
        private String description;
        private GraphQLOutputType type;
        private DataFetcherFactory<?> dataFetcherFactory;
        private String deprecationReason;
        private FieldDefinition definition;
        private EStructuralFeature feature;
        private final Map<String, GraphQLArgument> arguments = new LinkedHashMap<>();
        private final Map<String, GraphQLDirective> directives = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder(GraphQLEMFFieldDefinition existing) {
            this.name = existing.getName();
            this.description = existing.getDescription();
            this.type = existing.getType();
            this.dataFetcherFactory = DataFetcherFactories.useDataFetcher(new PropertyDataFetcher<>(name));
            this.deprecationReason = existing.getDeprecationReason();
            this.definition = existing.getDefinition();
            this.feature = existing.getFeature();
            this.arguments.putAll(getByName(existing.getArguments(), GraphQLArgument::getName));
            this.directives.putAll(getByName(existing.getDirectives(), GraphQLDirective::getName));
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder definition(FieldDefinition definition) {
            this.definition = definition;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder feature(EStructuralFeature feature) {
        	this.feature = feature;
        	return this;
        }

        public Builder type(GraphQLObjectType.Builder builder) {
            return type(builder.build());
        }

        public Builder type(GraphQLInterfaceType.Builder builder) {
            return type(builder.build());
        }

        public Builder type(GraphQLUnionType.Builder builder) {
            return type(builder.build());
        }

        public Builder type(GraphQLOutputType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the {@link graphql.schema.DataFetcher} to use with this field.
         *
         * @param dataFetcher the data fetcher to use
         *
         * @return this builder
         */
        public Builder dataFetcher(DataFetcher<?> dataFetcher) {
            assertNotNull(dataFetcher, () -> "dataFetcher must be not null");
            this.dataFetcherFactory = DataFetcherFactories.useDataFetcher(dataFetcher);
            return this;
        }

        /**
         * Sets the {@link graphql.schema.DataFetcherFactory} to use with this field.
         *
         * @param dataFetcherFactory the factory to use
         *
         * @return this builder
         */
        public Builder dataFetcherFactory(DataFetcherFactory<?> dataFetcherFactory) {
            assertNotNull(dataFetcherFactory, () -> "dataFetcherFactory must be not null");
            this.dataFetcherFactory = dataFetcherFactory;
            return this;
        }

        /**
         * This will cause the data fetcher of this field to always return the supplied value
         *
         * @param value the value to always return
         *
         * @return this builder
         */
        public Builder staticValue(final Object value) {
            this.dataFetcherFactory = DataFetcherFactories.useDataFetcher(environment -> value);
            return this;
        }

        public Builder argument(GraphQLArgument argument) {
            assertNotNull(argument, () -> "argument can't be null");
            this.arguments.put(argument.getName(), argument);
            return this;
        }

        /**
         * Take an argument builder in a function definition and apply. Can be used in a jdk8 lambda
         * e.g.:
         * <pre>
         *     {@code
         *      argument(a -> a.name("argumentName"))
         *     }
         * </pre>
         *
         * @param builderFunction a supplier for the builder impl
         *
         * @return this
         */
        public Builder argument(UnaryOperator<GraphQLArgument.Builder> builderFunction) {
            GraphQLArgument.Builder builder = GraphQLArgument.newArgument();
            builder = builderFunction.apply(builder);
            return argument(builder);
        }

        /**
         * Same effect as the argument(GraphQLArgument). Builder.build() is called
         * from within
         *
         * @param builder an un-built/incomplete GraphQLArgument
         *
         * @return this
         */
        public Builder argument(GraphQLArgument.Builder builder) {
            argument(builder.build());
            return this;
        }

        public Builder argument(List<GraphQLArgument> arguments) {
            assertNotNull(arguments, () -> "arguments can't be null");
            for (GraphQLArgument argument : arguments) {
                argument(argument);
            }
            return this;
        }

        /**
         * This is used to clear all the arguments in the builder so far.
         *
         * @return the builder
         */
        public Builder clearArguments() {
            arguments.clear();
            return this;
        }


        public Builder deprecate(String deprecationReason) {
            this.deprecationReason = deprecationReason;
            return this;
        }

        public Builder withDirectives(GraphQLDirective... directives) {
            assertNotNull(directives, () -> "directives can't be null");
            for (GraphQLDirective directive : directives) {
                withDirective(directive);
            }
            return this;
        }

        public Builder withDirective(GraphQLDirective directive) {
            assertNotNull(directive, () -> "directive can't be null");
            directives.put(directive.getName(), directive);
            return this;
        }

        public Builder withDirective(GraphQLDirective.Builder builder) {
            return withDirective(builder.build());
        }

        /**
         * This is used to clear all the directives in the builder so far.
         *
         * @return the builder
         */
        public Builder clearDirectives() {
            directives.clear();
            return this;
        }

        public GraphQLEMFFieldDefinition build() {
            if (dataFetcherFactory == null) {
                dataFetcherFactory = DataFetcherFactories.useDataFetcher(new PropertyDataFetcher<>(name));
            }
            return new GraphQLEMFFieldDefinition(name, description, type, dataFetcherFactory,
                    valuesToList(arguments), deprecationReason, valuesToList(directives), definition, feature);
        }
    }
}
