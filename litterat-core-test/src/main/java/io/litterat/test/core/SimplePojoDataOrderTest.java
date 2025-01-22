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

import java.util.Map;

import io.litterat.bind.DataBindException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.test.core.data.SimplePojoDataOrder;

public class SimplePojoDataOrderTest {

	final static int TEST_A = -1;
	final static int TEST_X = 1;
	final static int TEST_Y = 2;
	final static int TEST_Z = 3;

	SimplePojoDataOrder test;

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();

		test = new SimplePojoDataOrder();
		test.setA(TEST_A);
		test.setX(TEST_X);
		test.setY(TEST_Y);
		test.setZ(TEST_Z);
	}

	@Test
	public void checkDescriptor() throws Throwable {
		TypeContext context = new TypeContext.Builder().build();
		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(SimplePojoDataOrder.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(SimplePojoDataOrder.class, descriptor.typeClass());
		// Assertions.assertEquals(SimplePojoDataOrder.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(4, fields.length);

		DataClassField fieldX = fields[0];
		Assertions.assertEquals("x", fieldX.name());
		Assertions.assertEquals(int.class, fieldX.type());
        Assertions.assertTrue(fieldX.isRequired());

		DataClassField fieldZ = fields[1];
		Assertions.assertEquals("z", fieldZ.name());
		Assertions.assertEquals(int.class, fieldZ.type());
        Assertions.assertTrue(fieldZ.isRequired());

		DataClassField fieldY = fields[2];
		Assertions.assertEquals("y", fieldY.name());
		Assertions.assertEquals(int.class, fieldY.type());
        Assertions.assertTrue(fieldY.isRequired());

		DataClassField fieldA = fields[3];
		Assertions.assertEquals("a", fieldA.name());
		Assertions.assertEquals(int.class, fieldA.type());
        Assertions.assertTrue(fieldA.isRequired());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		// rebuild as an object.
		SimplePojoDataOrder object = arrayMap.toObject(SimplePojoDataOrder.class, values);

		// Validate
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimplePojoDataOrder.class, object);
		Assertions.assertEquals(TEST_A, object.getA());
		Assertions.assertEquals(TEST_X, object.getX());
		Assertions.assertEquals(TEST_Y, object.getY());
		Assertions.assertEquals(TEST_Z, object.getZ());
	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		SimplePojoDataOrder object = (SimplePojoDataOrder) mapMapper.toObject(SimplePojoDataOrder.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimplePojoDataOrder.class, object);
		Assertions.assertEquals(TEST_A, object.getA());
		Assertions.assertEquals(TEST_X, object.getX());
		Assertions.assertEquals(TEST_Y, object.getY());
		Assertions.assertEquals(TEST_Z, object.getZ());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("x", "error");

		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(SimplePojoDataOrder.class, map);
		});

	}

}
