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
import io.litterat.test.data.SimplePublicFields;

public class SimplePublicFieldsTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	final static SimplePublicFields test = new SimplePublicFields();
	static {
		test.x = TEST_X;
		test.y = TEST_Y;
	}

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(SimplePublicFields.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(SimplePublicFields.class, descriptor.typeClass());
		// Assertions.assertEquals(SimplePublicFields.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(2, fields.length);

		DataClassField fieldX = fields[0];
		Assertions.assertEquals("x", fieldX.name());
		Assertions.assertEquals(int.class, fieldX.type());
        Assertions.assertTrue(fieldX.isRequired());

		DataClassField fieldY = fields[1];
		Assertions.assertEquals("y", fieldY.name());
		Assertions.assertEquals(int.class, fieldY.type());
        Assertions.assertTrue(fieldY.isRequired());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		SimplePublicFields object = arrayMap.toObject(SimplePublicFields.class, values);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimplePublicFields.class, object);

		Assertions.assertEquals(TEST_X, object.x);
		Assertions.assertEquals(TEST_Y, object.y);
	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		SimplePublicFields object = (SimplePublicFields) mapMapper.toObject(SimplePublicFields.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimplePublicFields.class, object);

		Assertions.assertEquals(TEST_X, object.x);
		Assertions.assertEquals(TEST_Y, object.y);
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("x", "error");

		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(SimplePublicFields.class, map);
		});

	}
}
