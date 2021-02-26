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
package io.litterat.test.bind;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.PepContext;
import io.litterat.bind.PepDataClass;
import io.litterat.bind.PepDataComponent;
import io.litterat.bind.PepException;
import io.litterat.bind.mapper.PepArrayMapper;
import io.litterat.bind.mapper.PepMapMapper;
import io.litterat.test.bind.data.AtomicTypeTestImmutable;

public class AtomicTypeTestImmutableTest {

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

	AtomicTypeTestImmutable test = new AtomicTypeTestImmutable(pBoolean, oBoolean, pByte, oByte, pChar, oChar, pShort,
			oShort, pInteger, oInteger, pLong, oLong, pFloat, oFloat, pDouble, oDouble, string);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		PepDataClass descriptor = context.getDescriptor(AtomicTypeTestImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(AtomicTypeTestImmutable.class, descriptor.typeClass());
		Assertions.assertEquals(AtomicTypeTestImmutable.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(17, fields.length);

		PepDataComponent fieldPBoolean = fields[0];
		Assertions.assertEquals("pBoolean", fieldPBoolean.name());
		Assertions.assertEquals(boolean.class, fieldPBoolean.type());

		PepDataComponent fieldOBoolean = fields[1];
		Assertions.assertEquals("oBoolean", fieldOBoolean.name());
		Assertions.assertEquals(Boolean.class, fieldOBoolean.type());

		PepDataComponent fieldPByte = fields[2];
		Assertions.assertEquals("pByte", fieldPByte.name());
		Assertions.assertEquals(byte.class, fieldPByte.type());

		PepDataComponent fieldOByte = fields[3];
		Assertions.assertEquals("oByte", fieldOByte.name());
		Assertions.assertEquals(Byte.class, fieldOByte.type());

		PepDataComponent fieldPChar = fields[4];
		Assertions.assertEquals("pChar", fieldPChar.name());
		Assertions.assertEquals(char.class, fieldPChar.type());

		PepDataComponent fieldOChar = fields[5];
		Assertions.assertEquals("oChar", fieldOChar.name());
		Assertions.assertEquals(Character.class, fieldOChar.type());

		PepDataComponent fieldPShort = fields[6];
		Assertions.assertEquals("pShort", fieldPShort.name());
		Assertions.assertEquals(short.class, fieldPShort.type());

		PepDataComponent fieldOShort = fields[7];
		Assertions.assertEquals("oShort", fieldOShort.name());
		Assertions.assertEquals(Short.class, fieldOShort.type());

		PepDataComponent fieldPInteger = fields[8];
		Assertions.assertEquals("pInteger", fieldPInteger.name());
		Assertions.assertEquals(int.class, fieldPInteger.type());

		PepDataComponent fieldOInteger = fields[9];
		Assertions.assertEquals("oInteger", fieldOInteger.name());
		Assertions.assertEquals(Integer.class, fieldOInteger.type());

		PepDataComponent fieldPLong = fields[10];
		Assertions.assertEquals("pLong", fieldPLong.name());
		Assertions.assertEquals(long.class, fieldPLong.type());

		PepDataComponent fieldOLong = fields[11];
		Assertions.assertEquals("oLong", fieldOLong.name());
		Assertions.assertEquals(Long.class, fieldOLong.type());

		PepDataComponent fieldPFloat = fields[12];
		Assertions.assertEquals("pFloat", fieldPFloat.name());
		Assertions.assertEquals(float.class, fieldPFloat.type());

		PepDataComponent fieldOFloat = fields[13];
		Assertions.assertEquals("oFloat", fieldOFloat.name());
		Assertions.assertEquals(Float.class, fieldOFloat.type());

		PepDataComponent fieldPDouble = fields[14];
		Assertions.assertEquals("pDouble", fieldPDouble.name());
		Assertions.assertEquals(double.class, fieldPDouble.type());

		PepDataComponent fieldODouble = fields[15];
		Assertions.assertEquals("oDouble", fieldODouble.name());
		Assertions.assertEquals(Double.class, fieldODouble.type());

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
		AtomicTypeTestImmutable object = arrayMap.toObject(AtomicTypeTestImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicTypeTestImmutable);

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

		AtomicTypeTestImmutable object = (AtomicTypeTestImmutable) mapMapper.toObject(AtomicTypeTestImmutable.class,
				map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicTypeTestImmutable);

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
			mapMapper.toObject(AtomicTypeTestImmutable.class, map);
		});

	}
}
