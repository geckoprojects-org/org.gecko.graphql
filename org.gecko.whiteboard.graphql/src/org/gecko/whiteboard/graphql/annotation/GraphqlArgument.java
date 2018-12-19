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
package org.gecko.whiteboard.graphql.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ PARAMETER })
/**
 * An Annotation to carry argument names and additional Options
 * @author Juergen Albert
 * @since 7 Nov 2018
 */
public @interface GraphqlArgument {
	
	/**
	 * @return the name of the argument
	 */
	public String value();
	
	/**
	 * @return marks an argument as optional
	 */
	public boolean optional() default false;
}
