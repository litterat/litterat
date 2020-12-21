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

import java.util.Arrays;
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
import io.litterat.test.pep.data.PrimitiveArray;

public class PrimitiveArrayTest {

	final static int[] TEST = new int[] { 1, 2, 3 };

	PrimitiveArray test = new PrimitiveArray(TEST);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		PepDataClass descriptor = context.getDescriptor(PrimitiveArray.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(PrimitiveArray.class, descriptor.typeClass());
		Assertions.assertEquals(PrimitiveArray.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		PepDataComponent field = fields[0];
		Assertions.assertEquals("intArray", field.name());
		Assertions.assertEquals(int[].class, field.type());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		PrimitiveArray object = arrayMap.toObject(PrimitiveArray.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof PrimitiveArray);

		Assertions.assertTrue(Arrays.equals(TEST, object.intArray()));

	}

	@Test
	public void testToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		PrimitiveArray object = (PrimitiveArray) mapMapper.toObject(PrimitiveArray.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof PrimitiveArray);

		Assertions.assertTrue(Arrays.equals(TEST, object.intArray()));
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("intArray", "error");

		Assertions.assertThrows(PepException.class, () -> {
			mapMapper.toObject(PrimitiveArray.class, map);
		});

	}
}
