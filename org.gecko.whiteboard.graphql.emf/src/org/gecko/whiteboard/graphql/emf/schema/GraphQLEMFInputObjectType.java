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
package org.gecko.whiteboard.graphql.emf.schema;

import static graphql.Assert.assertNotNull;
import static graphql.util.FpKit.getByName;
import static graphql.util.FpKit.valuesToList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.eclipse.emf.ecore.EClass;

import graphql.PublicApi;
import graphql.language.InputObjectTypeDefinition;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;

/**
 * 
 * @author jalbert
 * @since 19 Nov 2018
 */
public class GraphQLEMFInputObjectType extends GraphQLInputObjectType {

	private EClass eClass = null;

	/**
	 * Creates a new instance.
	 * @param name
	 * @param description
	 * @param fields
	 * @param directives
	 * @param definition
	 */
	public GraphQLEMFInputObjectType(String name, String description, List<GraphQLInputObjectField> fields,
			List<GraphQLDirective> directives, InputObjectTypeDefinition definition, EClass eClass) {
		super(name, description, fields, directives, definition);
		this.eClass = eClass;
	}

	/**
	 * Returns the eClass.
	 * @return the eClass
	 */
	public EClass getEClass() {
		return eClass;
	}
	
	public static Builder newEMFInputObject(GraphQLEMFInputObjectType existing) {
        return new Builder(existing);
    }

    public static Builder newEMFInputObject() {
        return new Builder();
    }

    @PublicApi
    public static class Builder {
        private String name;
        private String description;
        private InputObjectTypeDefinition definition;
        private final Map<String, GraphQLInputObjectField> fields = new LinkedHashMap<>();
        private final Map<String, GraphQLDirective> directives = new LinkedHashMap<>();
		private EClass eClass;

        
        
        public Builder() {
        }

        public Builder(GraphQLEMFInputObjectType existing) {
            this.name = existing.getName();
            this.description = existing.getDescription();
            this.definition = existing.getDefinition();
            this.fields.putAll(getByName(existing.getFields(), GraphQLInputObjectField::getName));
            this.directives.putAll(getByName(existing.getDirectives(), GraphQLDirective::getName));
            this.eClass = existing.getEClass();
        }

        public Builder eClass(EClass eClass) {
        	this.eClass = eClass;
        	return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder definition(InputObjectTypeDefinition definition) {
            this.definition = definition;
            return this;
        }

        public Builder field(GraphQLInputObjectField field) {
            assertNotNull(field, "field can't be null");
            fields.put(field.getName(), field);
            return this;
        }

        /**
         * Take a field builder in a function definition and apply. Can be used in a jdk8 lambda
         * e.g.:
         * <pre>
         *     {@code
         *      field(f -> f.name("fieldName"))
         *     }
         * </pre>
         *
         * @param builderFunction a supplier for the builder impl
         *
         * @return this
         */
        public Builder field(UnaryOperator<GraphQLInputObjectField.Builder> builderFunction) {
            assertNotNull(builderFunction, "builderFunction should not be null");
            GraphQLInputObjectField.Builder builder = GraphQLInputObjectField.newInputObjectField();
            builder = builderFunction.apply(builder);
            return field(builder);
        }

        /**
         * Same effect as the field(GraphQLFieldDefinition). Builder.build() is called
         * from within
         *
         * @param builder an un-built/incomplete GraphQLFieldDefinition
         *
         * @return this
         */
        public Builder field(GraphQLInputObjectField.Builder builder) {
            return field(builder.build());
        }

        public Builder fields(List<GraphQLInputObjectField> fields) {
            fields.forEach(this::field);
            return this;
        }

        public boolean hasField(String fieldName) {
            return fields.containsKey(fieldName);
        }

        /**
         * This is used to clear all the fields in the builder so far.
         *
         * @return the builder
         */
        public Builder clearFields() {
            fields.clear();
            return this;
        }

        public Builder withDirectives(GraphQLDirective... directives) {
            for (GraphQLDirective directive : directives) {
                withDirective(directive);
            }
            return this;
        }

        public Builder withDirective(GraphQLDirective directive) {
            assertNotNull(directive, "directive can't be null");
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

        public GraphQLEMFInputObjectType build() {
            return new GraphQLEMFInputObjectType(name, description, valuesToList(fields), valuesToList(directives), definition, eClass);
        }
    }
}
