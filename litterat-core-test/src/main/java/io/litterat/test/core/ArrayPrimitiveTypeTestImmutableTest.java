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
package io.litterat.test.core;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.test.core.data.ArrayPrimitiveTypeTestImmutable;

public class ArrayPrimitiveTypeTestImmutableTest {

	private final static boolean[] pBoolean = new boolean[] { false };
	private final static Boolean[] oBoolean = new Boolean[] { Boolean.TRUE };
	private final static byte[] pByte = new byte[] { 1 };
	private final static Byte[] oByte = new Byte[] {(byte) 2};
	private final static char[] pChar = new char[] { 3 };
	private final static Character[] oChar = new Character[] {(char) 103};
	private final static short[] pShort = new short[] { 4 };
	private final static Short[] oShort = new Short[] {(short) 5};
	private final static int[] pInteger = new int[] { 42 };
	private final static Integer[] oInteger = new Integer[] {6};
	private final static long[] pLong = new long[] { 7 };
	private final static Long[] oLong = new Long[] {10L};
	private final static float[] pFloat = new float[] { 12.42f };
	private final static Float[] oFloat = new Float[] {13.54f};
	private final static double[] pDouble = new double[] { 56.34d };
	private final static Double[] oDouble = new Double[] {123.456d};
	private final static String[] string = new String[] { "test" };

	ArrayPrimitiveTypeTestImmutable test = new ArrayPrimitiveTypeTestImmutable(pBoolean, oBoolean, pByte, oByte, pChar,
			oChar, pShort, oShort, pInteger, oInteger, pLong, oLong, pFloat, oFloat, pDouble, oDouble, string);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(ArrayPrimitiveTypeTestImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(ArrayPrimitiveTypeTestImmutable.class, descriptor.typeClass());
		// Assertions.assertEquals(ArrayPrimitiveTypeTestImmutable.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(17, fields.length);

		DataClassField fieldPBoolean = fields[0];
		Assertions.assertEquals("pBoolean", fieldPBoolean.name());
		Assertions.assertEquals(boolean[].class, fieldPBoolean.type());
        Assertions.assertFalse(fieldPBoolean.isRequired());

		DataClassField fieldOBoolean = fields[1];
		Assertions.assertEquals("oBoolean", fieldOBoolean.name());
		Assertions.assertEquals(Boolean[].class, fieldOBoolean.type());
        Assertions.assertFalse(fieldOBoolean.isRequired());

		DataClassField fieldPByte = fields[2];
		Assertions.assertEquals("pByte", fieldPByte.name());
		Assertions.assertEquals(byte[].class, fieldPByte.type());
        Assertions.assertFalse(fieldPByte.isRequired());

		DataClassField fieldOByte = fields[3];
		Assertions.assertEquals("oByte", fieldOByte.name());
		Assertions.assertEquals(Byte[].class, fieldOByte.type());
        Assertions.assertFalse(fieldOByte.isRequired());

		DataClassField fieldPChar = fields[4];
		Assertions.assertEquals("pChar", fieldPChar.name());
		Assertions.assertEquals(char[].class, fieldPChar.type());
        Assertions.assertFalse(fieldPChar.isRequired());

		DataClassField fieldOChar = fields[5];
		Assertions.assertEquals("oChar", fieldOChar.name());
		Assertions.assertEquals(Character[].class, fieldOChar.type());
        Assertions.assertFalse(fieldOChar.isRequired());

		DataClassField fieldPShort = fields[6];
		Assertions.assertEquals("pShort", fieldPShort.name());
		Assertions.assertEquals(short[].class, fieldPShort.type());
        Assertions.assertFalse(fieldPShort.isRequired());

		DataClassField fieldOShort = fields[7];
		Assertions.assertEquals("oShort", fieldOShort.name());
		Assertions.assertEquals(Short[].class, fieldOShort.type());
        Assertions.assertFalse(fieldOShort.isRequired());

		DataClassField fieldPInteger = fields[8];
		Assertions.assertEquals("pInteger", fieldPInteger.name());
		Assertions.assertEquals(int[].class, fieldPInteger.type());
        Assertions.assertFalse(fieldPInteger.isRequired());

		DataClassField fieldOInteger = fields[9];
		Assertions.assertEquals("oInteger", fieldOInteger.name());
		Assertions.assertEquals(Integer[].class, fieldOInteger.type());
        Assertions.assertFalse(fieldOInteger.isRequired());

		DataClassField fieldPLong = fields[10];
		Assertions.assertEquals("pLong", fieldPLong.name());
		Assertions.assertEquals(long[].class, fieldPLong.type());
        Assertions.assertFalse(fieldPLong.isRequired());

		DataClassField fieldOLong = fields[11];
		Assertions.assertEquals("oLong", fieldOLong.name());
		Assertions.assertEquals(Long[].class, fieldOLong.type());
        Assertions.assertFalse(fieldOLong.isRequired());

		DataClassField fieldPFloat = fields[12];
		Assertions.assertEquals("pFloat", fieldPFloat.name());
		Assertions.assertEquals(float[].class, fieldPFloat.type());
        Assertions.assertFalse(fieldPFloat.isRequired());

		DataClassField fieldOFloat = fields[13];
		Assertions.assertEquals("oFloat", fieldOFloat.name());
		Assertions.assertEquals(Float[].class, fieldOFloat.type());
        Assertions.assertFalse(fieldOFloat.isRequired());

		DataClassField fieldPDouble = fields[14];
		Assertions.assertEquals("pDouble", fieldPDouble.name());
		Assertions.assertEquals(double[].class, fieldPDouble.type());
        Assertions.assertFalse(fieldPDouble.isRequired());

		DataClassField fieldODouble = fields[15];
		Assertions.assertEquals("oDouble", fieldODouble.name());
		Assertions.assertEquals(Double[].class, fieldODouble.type());
        Assertions.assertFalse(fieldODouble.isRequired());

		DataClassField fieldString = fields[16];
		Assertions.assertEquals("string", fieldString.name());
		Assertions.assertEquals(String[].class, fieldString.type());
        Assertions.assertFalse(fieldString.isRequired());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		ArrayPrimitiveTypeTestImmutable object = arrayMap.toObject(ArrayPrimitiveTypeTestImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(ArrayPrimitiveTypeTestImmutable.class, object);

        Assertions.assertArrayEquals(pBoolean, object.getpBoolean());
        Assertions.assertArrayEquals(oBoolean, object.getoBoolean());
        Assertions.assertArrayEquals(pByte, object.getpByte());
        Assertions.assertArrayEquals(oByte, object.getoByte());
        Assertions.assertArrayEquals(pChar, object.getpChar());
        Assertions.assertArrayEquals(oChar, object.getoChar());
        Assertions.assertArrayEquals(pShort, object.getpShort());
        Assertions.assertArrayEquals(oShort, object.getoShort());
        Assertions.assertArrayEquals(pInteger, object.getpInteger());
        Assertions.assertArrayEquals(oInteger, object.getoInteger());
        Assertions.assertArrayEquals(pLong, object.getpLong());
        Assertions.assertArrayEquals(oLong, object.getoLong());
        Assertions.assertArrayEquals(pFloat, object.getpFloat());
        Assertions.assertArrayEquals(oFloat, object.getoFloat());
        Assertions.assertArrayEquals(pDouble, object.getpDouble());
        Assertions.assertArrayEquals(oDouble, object.getoDouble());
        Assertions.assertArrayEquals(string, object.getString());

	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		ArrayPrimitiveTypeTestImmutable object = (ArrayPrimitiveTypeTestImmutable) mapMapper
				.toObject(ArrayPrimitiveTypeTestImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(ArrayPrimitiveTypeTestImmutable.class, object);

        Assertions.assertArrayEquals(pBoolean, object.getpBoolean());
        Assertions.assertArrayEquals(oBoolean, object.getoBoolean());
        Assertions.assertArrayEquals(pByte, object.getpByte());
        Assertions.assertArrayEquals(oByte, object.getoByte());
        Assertions.assertArrayEquals(pChar, object.getpChar());
        Assertions.assertArrayEquals(oChar, object.getoChar());
        Assertions.assertArrayEquals(pShort, object.getpShort());
        Assertions.assertArrayEquals(oShort, object.getoShort());
        Assertions.assertArrayEquals(pInteger, object.getpInteger());
        Assertions.assertArrayEquals(oInteger, object.getoInteger());
        Assertions.assertArrayEquals(pLong, object.getpLong());
        Assertions.assertArrayEquals(oLong, object.getoLong());
        Assertions.assertArrayEquals(pFloat, object.getpFloat());
        Assertions.assertArrayEquals(oFloat, object.getoFloat());
        Assertions.assertArrayEquals(pDouble, object.getpDouble());
        Assertions.assertArrayEquals(oDouble, object.getoDouble());
        Assertions.assertArrayEquals(string, object.getString());

	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("pBoolean", "error");

		Assertions.assertThrows(TypeException.class, () -> {
			mapMapper.toObject(ArrayPrimitiveTypeTestImmutable.class, map);
		});

	}
}
