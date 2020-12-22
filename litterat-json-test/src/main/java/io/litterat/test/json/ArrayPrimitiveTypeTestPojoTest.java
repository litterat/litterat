/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.litterat.test.json;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.json.JsonMapper;
import io.litterat.pep.PepContext;
import io.litterat.test.pep.data.ArrayPrimitiveTypeTestPojo;

public class ArrayPrimitiveTypeTestPojoTest {

	private final static boolean[] pBoolean = new boolean[] { false };
	private final static Boolean[] oBoolean = new Boolean[] { Boolean.TRUE };
	private final static byte[] pByte = new byte[] { 1 };
	private final static Byte[] oByte = new Byte[] { Byte.valueOf((byte) 2) };
	private final static char[] pChar = new char[] { 3 };
	private final static Character[] oChar = new Character[] { Character.valueOf((char) 103) };
	private final static short[] pShort = new short[] { 4 };
	private final static Short[] oShort = new Short[] { Short.valueOf((short) 5) };
	private final static int[] pInteger = new int[] { 42 };
	private final static Integer[] oInteger = new Integer[] { Integer.valueOf(6) };
	private final static long[] pLong = new long[] { 7 };
	private final static Long[] oLong = new Long[] { Long.valueOf(10l) };
	private final static float[] pFloat = new float[] { 12.42f };
	private final static Float[] oFloat = new Float[] { Float.valueOf(13.54f) };
	private final static double[] pDouble = new double[] { 56.34d };
	private final static Double[] oDouble = new Double[] { Double.valueOf(123.456d) };
	private final static String[] string = new String[] { "test" };

	final static ArrayPrimitiveTypeTestPojo test = new ArrayPrimitiveTypeTestPojo();

	static {
		test.setpBoolean(pBoolean);
		test.setoBoolean(oBoolean);
		test.setpByte(pByte);
		test.setoByte(oByte);
		test.setpChar(pChar);
		test.setoChar(oChar);
		test.setpShort(pShort);
		test.setoShort(oShort);
		test.setpInteger(pInteger);
		test.setoInteger(oInteger);
		test.setpLong(pLong);
		test.setoLong(oLong);
		test.setpFloat(pFloat);
		test.setoFloat(oFloat);
		test.setpDouble(pDouble);
		test.setoDouble(oDouble);
		test.setString(string);
	}

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		// project to an array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		ArrayPrimitiveTypeTestPojo object = JsonMapper.fromJson(json, ArrayPrimitiveTypeTestPojo.class);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ArrayPrimitiveTypeTestPojo);

		Assertions.assertTrue(Arrays.equals(pBoolean, object.getpBoolean()));
		Assertions.assertTrue(Arrays.equals(oBoolean, object.getoBoolean()));
		Assertions.assertTrue(Arrays.equals(pByte, object.getpByte()));
		Assertions.assertTrue(Arrays.equals(oByte, object.getoByte()));
		Assertions.assertTrue(Arrays.equals(pChar, object.getpChar()));
		Assertions.assertTrue(Arrays.equals(oChar, object.getoChar()));
		Assertions.assertTrue(Arrays.equals(pShort, object.getpShort()));
		Assertions.assertTrue(Arrays.equals(oShort, object.getoShort()));
		Assertions.assertTrue(Arrays.equals(pInteger, object.getpInteger()));
		Assertions.assertTrue(Arrays.equals(oInteger, object.getoInteger()));
		Assertions.assertTrue(Arrays.equals(pLong, object.getpLong()));
		Assertions.assertTrue(Arrays.equals(oLong, object.getoLong()));
		Assertions.assertTrue(Arrays.equals(pFloat, object.getpFloat()));
		Assertions.assertTrue(Arrays.equals(oFloat, object.getoFloat()));
		Assertions.assertTrue(Arrays.equals(pDouble, object.getpDouble()));
		Assertions.assertTrue(Arrays.equals(oDouble, object.getoDouble()));
		Assertions.assertTrue(Arrays.equals(string, object.getString()));

	}
}
