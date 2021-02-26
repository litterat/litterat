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
import io.litterat.test.bind.data.SimplePojo;

public class SimplePojoTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	SimplePojo test;

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();

		test = new SimplePojo();
		test.setX(TEST_X);
		test.setY(TEST_Y);
	}

	@Test
	public void checkDescriptor() throws Throwable {
		PepContext context = new PepContext.Builder().build();
		PepDataClass descriptor = context.getDescriptor(SimplePojo.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(SimplePojo.class, descriptor.typeClass());
		Assertions.assertEquals(SimplePojo.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(2, fields.length);

		PepDataComponent fieldX = fields[0];
		Assertions.assertEquals("x", fieldX.name());
		Assertions.assertEquals(int.class, fieldX.type());

		PepDataComponent fieldY = fields[1];
		Assertions.assertEquals("y", fieldY.name());
		Assertions.assertEquals(int.class, fieldY.type());
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		// rebuild as an object.
		SimplePojo object = arrayMap.toObject(SimplePojo.class, values);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof SimplePojo);
		Assertions.assertEquals(TEST_X, object.getX());
		Assertions.assertEquals(TEST_Y, object.getY());

	}

	@Test
	public void testToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		SimplePojo object = (SimplePojo) mapMapper.toObject(SimplePojo.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof SimplePojo);
		Assertions.assertEquals(TEST_X, object.getX());
		Assertions.assertEquals(TEST_Y, object.getY());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("x", "error");

		Assertions.assertThrows(PepException.class, () -> {
			mapMapper.toObject(SimplePojo.class, map);
		});

	}

}
