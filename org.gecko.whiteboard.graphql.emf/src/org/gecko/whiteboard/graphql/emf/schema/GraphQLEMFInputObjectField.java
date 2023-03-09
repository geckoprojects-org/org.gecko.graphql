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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;

import graphql.language.InputValueDefinition;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.InputValueWithState;

/**
 * 
 * @author jalbert
 * @since 20 Nov 2018
 */
public class GraphQLEMFInputObjectField extends GraphQLInputObjectField {

	private EStructuralFeature eFeature;
	
	// @formatter:off
	public GraphQLEMFInputObjectField(String name, 
									  String description, 
									  GraphQLInputType type, 
									  Object defaultValue,
									  List<GraphQLDirective> directives, 
									  InputValueDefinition definition, 
									  EStructuralFeature eFeature) {
		super(name, 
			  description, 
			  type, 
			  InputValueWithState.newInternalValue(defaultValue), 
			  directives, 
			  Collections.emptyList(), // List<GraphQLAppliedDirective>
			  definition, 
			  null); // String deprecationReason
		
		this.eFeature = eFeature;
	}
	// @formatter:off
	
	/**
	 * Returns the eFeature.
	 * @return the eFeature
	 */
	public EStructuralFeature getEFeature() {
		return eFeature;
	}
	
    public static Builder newEMFInputObjectField(GraphQLInputObjectField existing) {
        return new Builder(existing);
    }


    public static Builder newEMFInputObjectField() {
        return new Builder();
    }
	
	public static class Builder {
        private String name;
        private String description;
        private InputValueWithState defaultValue = InputValueWithState.NOT_SET;
        private GraphQLInputType type;
        private InputValueDefinition definition;
        private EStructuralFeature eFeature;
        private final Map<String, GraphQLDirective> directives = new LinkedHashMap<>();

        public Builder() {
        }

        public Builder(GraphQLInputObjectField existing) {
            this.name = existing.getName();
            this.description = existing.getDescription();
            this.defaultValue = existing.getInputFieldDefaultValue();
            this.type = existing.getType();
            this.definition = existing.getDefinition();
            this.directives.putAll(getByName(existing.getDirectives(), GraphQLDirective::getName));
            if(existing instanceof GraphQLEMFInputObjectField) {
            	this.eFeature = ((GraphQLEMFInputObjectField) existing).getEFeature();
            }
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder definition(InputValueDefinition definition) {
            this.definition = definition;
            return this;
        }

        public Builder type(GraphQLInputObjectType.Builder type) {
            return type(type.build());
        }

        public Builder type(GraphQLInputType type) {
            this.type = type;
            return this;
        }

        public Builder eFeature(EStructuralFeature eFeature) {
        	this.eFeature = eFeature;
        	return this;
        }

        public Builder defaultValue(Object defaultValue) {
            this.defaultValue = InputValueWithState.newInternalValue(defaultValue);
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

        public GraphQLEMFInputObjectField build() {
            return new GraphQLEMFInputObjectField(name, description, type, defaultValue, valuesToList(directives), definition, eFeature);
        }
    }
}
