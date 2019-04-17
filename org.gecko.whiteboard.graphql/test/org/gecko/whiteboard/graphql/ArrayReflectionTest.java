/**
 * Copyright (c) 2012 - 2019 Data In Motion and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author ChristophDockhorn
 * @param <T>
 * @since 27.03.2019
 */
public class ArrayReflectionTest {
	
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	

	/**
	 * Creates a new instance.
	 */
	public ArrayReflectionTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testIntListToArray() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<Integer> intList = new ArrayList<>();

		intList.add(1);
		intList.add(2);

		TestClassInt test = new TestClassInt();

		Method m = test.getClass().getDeclaredMethods()[0];

		System.out.println(m.getName());

		int[] arg = intList.stream().mapToInt(i -> i).toArray();
		assertEquals(2, arg.length);
		System.out.println(arg.getClass().getSimpleName());

		m.invoke(test, new Object[] {arg} );
		
//		System.out.println(arg);
		
	}
//
//	@Test
//	public <E> void testIntArrayReflective()
//			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, ClassNotFoundException {
//
//		List<Integer> intList = new ArrayList<>();
//
//		intList.add(1);
//		intList.add(2);
//
//		TestClass test = new TestClass();
//		Method m = test.getClass().getDeclaredMethods()[0];
////		Class<?> clazz = null;
//		
//
//		System.out.println(m.getName());
//
//		int[] arg = toNewArray(intList, int[].class);
//		assertEquals(2, arg.length);
//		System.out.println(arg.getClass().getSimpleName());
//		System.out.println(arg);
//		
//		m.invoke(test, arg);
//		
//	}
//	
//	

	/*
	 * Test class for integer arrays.
	 */
	public static class TestClassInt {
		public void read(int[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for double arrays.
	 */
	public static class TestClassDouble {
		public void read(double[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for byte arrays.
	 */
	public static class TestClassByte {
		public void read(byte[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for short arrays.
	 */
	public static class TestClassShort {
		public void read(short[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for long arrays.
	 */
	public static class TestClassLong {
		public void read(long[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for float arrays.
	 */
	public static class TestClassFloat {
		public void read(float[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for char arrays.
	 */
	public static class TestClassChar {
		public void read(char[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for String arrays.
	 */
	public static class TestClassString {
		public void read(String[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}

	/*
	 * Test class for boolean arrays.
	 */
	public static class TestClassBoolean {
		public void read(boolean[] in) {
			System.out.println(in);
			Arrays.asList(in).forEach(System.out::println);
		}
	}
	
	
	/**
	 * Test for converting List<Integer> to int[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntegerArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		
		TestClassInt test = new TestClassInt();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, int[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
		
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), int[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1, ((int[])arg)[0]);
				assertEquals(2, ((int[])arg)[1]);
				System.out.println(((int[])arg)[0]);
				System.out.println(((int[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Tests that converting an empty List<Integer> returns an empty int[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntegerEmptyArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Integer> list = new ArrayList<>();
		
		TestClassInt test = new TestClassInt();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, int[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), int[].class);
			m.invoke(test, arg);
			assertTrue(arg.getClass().isArray());
		}
		
	}

	
	/**
	 * Checks that a List that is null causes a NullPointerException when trying to convert it to an array.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testIntegerListNullReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Integer> list = null;
		
		TestClassInt test = new TestClassInt();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, int[].class);
		
		if (list == null) {
			assertNull(list);
			exception.expect(NullPointerException.class);
			Object arg = toNewArray(list, int[].class);
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), int[].class);
			m.invoke(test, arg);
			assertTrue(arg.getClass().isArray());
		}
		
	}
	
	/**
	 * Test for converting List<Double> to double[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testDoubleArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Double> list = new ArrayList<>();
		list.add(1.5);
		list.add(2.5);
		
		TestClassDouble test = new TestClassDouble();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, double[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
		
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), double[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1.5, ((double[])arg)[0], 1);
				assertEquals(2.5, ((double[])arg)[1], 1);
				System.out.println(((double[])arg)[0]);
				System.out.println(((double[])arg)[1]);
			}
		}
		
	}
	
	/**
	 * Test for converting List<Byte> to byte[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testByteArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Byte> list = new ArrayList<>();
		list.add((byte)1);
		list.add((byte)2);
		
		TestClassByte test = new TestClassByte();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, byte[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
		
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), byte[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1, ((byte[])arg)[0]);
				assertEquals(2, ((byte[])arg)[1]);
				System.out.println(((byte[])arg)[0]);
				System.out.println(((byte[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Test for converting List<Short> to short[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testShortArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Short> list = new ArrayList<>();
		list.add((short)1);
		list.add((short)2);
		
		TestClassShort test = new TestClassShort();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, short[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), short[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1, ((short[])arg)[0]);
				assertEquals(2, ((short[])arg)[1]);
				System.out.println(((short[])arg)[0]);
				System.out.println(((short[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Test for converting List<Long> to long[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testLongArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Long> list = new ArrayList<>();
		list.add(1L);
		list.add(2L);
		
		TestClassLong test = new TestClassLong();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, long[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), long[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1, ((long[])arg)[0]);
				assertEquals(2, ((long[])arg)[1]);
				System.out.println(((long[])arg)[0]);
				System.out.println(((long[])arg)[1]);
			}
		}
		
	}
	
	
	/**
	 * Test for converting List<Float> to float[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testFloatArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Float> list = new ArrayList<>();
		list.add(1.5F);
		list.add(2.5F);
		
		TestClassFloat test = new TestClassFloat();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, float[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
		
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), float[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(1.5, ((float[])arg)[0], 1);
				assertEquals(2.5, ((float[])arg)[1], 1);
				System.out.println(((float[])arg)[0]);
				System.out.println(((float[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Test for converting List<Character> to char[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testCharArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Character> list = new ArrayList<>();
		list.add('a');
		list.add('b');
		
		TestClassChar test = new TestClassChar();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, char[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), char[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals('a', ((char[])arg)[0]);
				assertEquals('b', ((char[])arg)[1]);
				System.out.println(((char[])arg)[0]);
				System.out.println(((char[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Test for converting List<String> to String[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStringArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		
		TestClassString test = new TestClassString();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, String[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), String[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals("a", ((String[])arg)[0]);
				assertEquals("b", ((String[])arg)[1]);
				System.out.println(((String[])arg)[0]);
				System.out.println(((String[])arg)[1]);
			}
		}
		
	}

	
	/**
	 * Test for converting List<Boolean> to boolean[].
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testBooleanArrayReflective()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		List<Boolean> list = new ArrayList<>();
		list.add(true);
		list.add(false);
		
		TestClassBoolean test = new TestClassBoolean();
		Method m = test.getClass().getDeclaredMethods()[0];
		System.out.println(m.getName());
		assertEquals("read", m.getName());
		Class<?>[] params = m.getParameterTypes();
		Class<?> clazz = params[0];
		assertEquals(clazz, boolean[].class);
		
		if (list == null) {
			// nothing to do
		} else {
			Object arg = convertListToArray(list, clazz);
			
			System.out.println(arg.getClass().getSimpleName());
			assertEquals(arg.getClass(), boolean[].class);
			m.invoke(test, arg);
			if (!list.isEmpty()) {
				assertEquals(true, ((boolean[])arg)[0]);
				assertEquals(false, ((boolean[])arg)[1]);
				System.out.println(((boolean[])arg)[0]);
				System.out.println(((boolean[])arg)[1]);
			}
		}
		
	}
	
	
	
//	/**
//	 * Checks that an IllegalArgumentException is thrown when trying to invoke the method of the TestClassLong
//	 * with an int[]. 
//	 * @throws IllegalAccessException
//	 * @throws IllegalArgumentException
//	 * @throws InvocationTargetException
//	 */
//	@Test
//	public void testDifferentTypes()
//			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		
//		
//		
//		List<Integer> list = new ArrayList<>();
//		list.add(1);
//		list.add(2);
//		
//		TestClassLong test = new TestClassLong();
//		Method m = test.getClass().getDeclaredMethods()[0];
//		System.out.println(m.getName());
//		assertEquals("read", m.getName());
//		Class<?>[] params = m.getParameterTypes();
//		Class<?> clazz = params[0];
//		assertFalse(clazz.equals(int[].class));
//		assertTrue(clazz.equals(long[].class));
//		
//		if (list == null) {
//			// nothing to do
//		} else {
//			Object arg = convertListToArray(list, clazz);
//		
//			System.out.println(arg.getClass().getSimpleName());
//			assertEquals(arg.getClass(), int[].class);
//			System.out.println("Invoking Method to check for IllegalArgumentException.");
//			exception.expect(IllegalArgumentException.class);
//			m.invoke(test, arg);
//			
//		}
//		
//	}


	
	
	
	
//	private <T> T[] convertListToArray (List<?> inList) {
//		Class<?> resultArray = getArrayType(inList.get(0).getClass());
//		T[] arg = (resultArray) toNewArray(inList, resultArray);
//		return arg;
//		
//	}
	
	
	/*
	 * Converts List to array.
	 */
	private Object convertListToArray (List<?> inList, Class<?> clazz) {
		Object arg = null;
		if (!inList.isEmpty()) {
			Class<?> resultArray = getArrayType(inList.get(0).getClass());
			assertTrue(resultArray.equals(clazz));
			arg = toNewArray(inList, resultArray);
			
		} else {
			List<?> list = new ArrayList<>();
			arg = toNewArray(list, clazz);
		}
		return arg;
		
	}
	


	/*
	 * Takes a class as parameter and returns the corresponding 
	 * array type.
	 */
	private Class<?> getArrayType(Class<?> type) {

//		Class<?> componentType = type.getComponentType();

		if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
			return boolean[].class;
		} else if (byte.class.isAssignableFrom(type) || (Byte.class.isAssignableFrom(type))) {
			return byte[].class;
		} else if (char.class.isAssignableFrom(type) || (Character.class.isAssignableFrom(type))) {
			return char[].class;
		} else if (double.class.isAssignableFrom(type) || (Double.class.isAssignableFrom(type))) {
			return double[].class;
		} else if (float.class.isAssignableFrom(type) || (Float.class.isAssignableFrom(type))) {
			return float[].class;
		} else if (int.class.isAssignableFrom(type) || (Integer.class.isAssignableFrom(type))) {
			return int[].class;
		} else if (long.class.isAssignableFrom(type) || (Long.class.isAssignableFrom(type))) {
			return long[].class;
		} else if (short.class.isAssignableFrom(type) || (Short.class.isAssignableFrom(type))) {
			return short[].class;
		} else if (String.class.isAssignableFrom(type)) {
			return String[].class;
		}

		return type;

	}
	

	/*
	 * Takes in list and Class of the preferred array type. Converts the list
	 * into an array of the specified type.
	 */
	public static <P> P toNewArray(List<?> list, Class<P> arrayType) {
		if (!arrayType.isArray()) {
			throw new IllegalArgumentException(arrayType.toString());
		}
		Class<?> cType = arrayType.getComponentType();
		
		P array = arrayType.cast(Array.newInstance(cType, list.size()));
		
		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		
		return array;
	}
	
	
	
//	public static <P> P toPrimitiveArray(List<?> list, Class<P> arrayType) {
//	    if (!arrayType.isArray()) {
//	        throw new IllegalArgumentException(arrayType.toString());
//	    }
//	    Class<?> primitiveType = arrayType.getComponentType();
//	    if (!primitiveType.isPrimitive()) {
//	        throw new IllegalArgumentException(primitiveType.toString());
//	    }
//
//	    P array = arrayType.cast(Array.newInstance(primitiveType, list.size()));
//
//	    for (int i = 0; i < list.size(); i++) {
//	        Array.set(array, i, list.get(i));
//	    }
//
//	    return array;
//	}
	

}
