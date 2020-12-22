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
package io.litterat.test.pep;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.pep.PepContext;
import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.PepException;
import io.litterat.pep.mapper.PepArrayMapper;
import io.litterat.pep.mapper.PepMapMapper;
import io.litterat.test.pep.data.AtomicTypeTestPojo;

public class AtomicTypeTestPojoTest {

	private final static boolean pBoolean = false;
	private final static Boolean oBoolean = Boolean.TRUE;
	private final static byte pByte = 1;
	private final static Byte oByte = Byte.valueOf((byte) 2);
	private final static char pChar = 3;
	private final static Character oChar = Character.valueOf((char) 103);
	private final static short pShort = 4;
	private final static Short oShort = Short.valueOf((short) 5);
	private final static int pInteger = 42;
	private final static Integer oInteger = Integer.valueOf(6);
	private final static long pLong = 7;
	private final static Long oLong = Long.valueOf(10l);
	private final static float pFloat = 12.42f;
	private final static Float oFloat = Float.valueOf(13.54f);
	private final static double pDouble = 56.34d;
	private final static Double oDouble = Double.valueOf(123.456d);
	private final static String string = "test";
	

	final static AtomicTypeTestPojo test = new AtomicTypeTestPojo();
	
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
	public void checkDescriptor() throws Throwable {

		PepDataClass descriptor = context.getDescriptor(AtomicTypeTestPojo.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(AtomicTypeTestPojo.class, descriptor.typeClass());
		Assertions.assertEquals(AtomicTypeTestPojo.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(17, fields.length);

		// For POJO these fields are in alpha order.
		
		PepDataComponent fieldOBoolean = fields[0];
		Assertions.assertEquals("oBoolean", fieldOBoolean.name());
		Assertions.assertEquals(Boolean.class, fieldOBoolean.type());

		PepDataComponent fieldOByte = fields[1];
		Assertions.assertEquals("oByte", fieldOByte.name());
		Assertions.assertEquals(Byte.class, fieldOByte.type());
		
		PepDataComponent fieldOChar = fields[2];
		Assertions.assertEquals("oChar", fieldOChar.name());
		Assertions.assertEquals(Character.class, fieldOChar.type());
		
		PepDataComponent fieldODouble = fields[3];
		Assertions.assertEquals("oDouble", fieldODouble.name());
		Assertions.assertEquals(Double.class, fieldODouble.type());
		
		PepDataComponent fieldOFloat = fields[4];
		Assertions.assertEquals("oFloat", fieldOFloat.name());
		Assertions.assertEquals(Float.class, fieldOFloat.type());
		
		PepDataComponent fieldOInteger = fields[5];
		Assertions.assertEquals("oInteger", fieldOInteger.name());
		Assertions.assertEquals(Integer.class, fieldOInteger.type());
		
		PepDataComponent fieldOLong = fields[6];
		Assertions.assertEquals("oLong", fieldOLong.name());
		Assertions.assertEquals(Long.class, fieldOLong.type());
		
		PepDataComponent fieldOShort = fields[7];
		Assertions.assertEquals("oShort", fieldOShort.name());
		Assertions.assertEquals(Short.class, fieldOShort.type());
		
		
		PepDataComponent fieldPBoolean = fields[8];
		Assertions.assertEquals("pBoolean", fieldPBoolean.name());
		Assertions.assertEquals(boolean.class, fieldPBoolean.type());


		PepDataComponent fieldPByte = fields[9];
		Assertions.assertEquals("pByte", fieldPByte.name());
		Assertions.assertEquals(byte.class, fieldPByte.type());


		PepDataComponent fieldPChar = fields[10];
		Assertions.assertEquals("pChar", fieldPChar.name());
		Assertions.assertEquals(char.class, fieldPChar.type());

		PepDataComponent fieldPDouble = fields[11];
		Assertions.assertEquals("pDouble", fieldPDouble.name());
		Assertions.assertEquals(double.class, fieldPDouble.type());

		PepDataComponent fieldPFloat = fields[12];
		Assertions.assertEquals("pFloat", fieldPFloat.name());
		Assertions.assertEquals(float.class, fieldPFloat.type());

		PepDataComponent fieldPInteger = fields[13];
		Assertions.assertEquals("pInteger", fieldPInteger.name());
		Assertions.assertEquals(int.class, fieldPInteger.type());


		PepDataComponent fieldPLong = fields[14];
		Assertions.assertEquals("pLong", fieldPLong.name());
		Assertions.assertEquals(long.class, fieldPLong.type());


		PepDataComponent fieldPShort = fields[15];
		Assertions.assertEquals("pShort", fieldPShort.name());
		Assertions.assertEquals(short.class, fieldPShort.type());

		PepDataComponent fieldString = fields[16];
		Assertions.assertEquals("string", fieldString.name());
		Assertions.assertEquals(String.class, fieldString.type());
		
		
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		AtomicTypeTestPojo object = arrayMap.toObject(AtomicTypeTestPojo.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicTypeTestPojo);

		Assertions.assertEquals(pBoolean, object.getpBoolean());
		Assertions.assertEquals(oBoolean, object.getoBoolean());
		Assertions.assertEquals(pByte, object.getpByte());
		Assertions.assertEquals(oByte, object.getoByte());
		Assertions.assertEquals(pChar, object.getpChar());
		Assertions.assertEquals(oChar, object.getoChar());
		Assertions.assertEquals(pShort, object.getpShort());
		Assertions.assertEquals(oShort, object.getoShort());
		Assertions.assertEquals(pInteger, object.getpInteger());
		Assertions.assertEquals(oInteger, object.getoInteger());
		Assertions.assertEquals(pLong, object.getpLong());
		Assertions.assertEquals(oLong, object.getoLong());
		Assertions.assertEquals(pFloat, object.getpFloat());
		Assertions.assertEquals(oFloat, object.getoFloat());
		Assertions.assertEquals(pDouble, object.getpDouble());
		Assertions.assertEquals(oDouble, object.getoDouble());
		Assertions.assertEquals(string, object.getString());

	}

	@Test
	public void testToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		AtomicTypeTestPojo object = (AtomicTypeTestPojo) mapMapper.toObject(AtomicTypeTestPojo.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicTypeTestPojo);

		Assertions.assertEquals(pBoolean, object.getpBoolean());
		Assertions.assertEquals(oBoolean, object.getoBoolean());
		Assertions.assertEquals(pByte, object.getpByte());
		Assertions.assertEquals(oByte, object.getoByte());
		Assertions.assertEquals(pChar, object.getpChar());
		Assertions.assertEquals(oChar, object.getoChar());
		Assertions.assertEquals(pShort, object.getpShort());
		Assertions.assertEquals(oShort, object.getoShort());
		Assertions.assertEquals(pInteger, object.getpInteger());
		Assertions.assertEquals(oInteger, object.getoInteger());
		Assertions.assertEquals(pLong, object.getpLong());
		Assertions.assertEquals(oLong, object.getoLong());
		Assertions.assertEquals(pFloat, object.getpFloat());
		Assertions.assertEquals(oFloat, object.getoFloat());
		Assertions.assertEquals(pDouble, object.getpDouble());
		Assertions.assertEquals(oDouble, object.getoDouble());
		Assertions.assertEquals(string, object.getString());

	}

	@Test
	public void testMapToObjectException() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("pBoolean", "error");

		Assertions.assertThrows(PepException.class, () -> {
			mapMapper.toObject(AtomicTypeTestPojo.class, map);
		});

	}
}