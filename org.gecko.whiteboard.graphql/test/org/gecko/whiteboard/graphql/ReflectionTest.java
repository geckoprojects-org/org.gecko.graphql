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
package org.gecko.whiteboard.graphql;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * 
 * @author jalbert
 * @since 8 Nov 2018
 */
public class ReflectionTest {

	public interface Test1{
		public void helloOne();
	}

	public interface Test2{
		public void helloTwo();
	}
	
	public class Test1Impl implements Test1{

		/* 
		 * (non-Javadoc)
		 * @see org.gecko.whiteboard.graphql.ReflectionTest.Test1#helloOne()
		 */
		@Override
		public void helloOne() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class Test2Impl extends Test1Impl implements Test2{

		/* 
		 * (non-Javadoc)
		 * @see org.gecko.whiteboard.graphql.ReflectionTest.Test2#helloTwo()
		 */
		@Override
		public void helloTwo() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Test
	public void test() {
		new Test2Impl();
		List<Class<?>> classes = getAllInterfaces(Test2Impl.class);
		assertEquals(2, classes.size());
	}
	
	List<Class<?>> getAllInterfaces(Class<?> clazz){
		
		List<Class<?>> result = new LinkedList<>();
		result.addAll(Arrays.asList(clazz.getInterfaces()));
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != null) {
			result.addAll(getAllInterfaces(superclass));
		}
		return result;
	}

}
