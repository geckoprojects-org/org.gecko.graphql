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
package org.gecko.whiteboard.graphql.annotation.v2;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
/**
 * Marks a Class, Interface or Method to be part of a query or mutation
 * @author Juergen Albert
 * @since 19 Dec 2018
 */
public @interface GraphQLSchemaType {

	enum Type {
		
		QUERY("query"),
		MUTATION("mutation");

		private final String name;
		
		Type(String name) {
			this.name = name;
		}
		
		/**
		 * Returns the name.
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
	}
	
	/**
	 * @return
	 */
	public Type value();
	
	
	public String parentName() default "";
	
}
