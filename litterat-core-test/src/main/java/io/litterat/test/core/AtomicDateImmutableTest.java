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

import java.util.Date;
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
import io.litterat.test.data.AtomicDateImmutable;

public class AtomicDateImmutableTest {

	final static long TEST_TIMESTAMP = System.currentTimeMillis();

	AtomicDateImmutable test = new AtomicDateImmutable(new Date(TEST_TIMESTAMP));

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(AtomicDateImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(AtomicDateImmutable.class, descriptor.typeClass());
		// Assertions.assertEquals(AtomicDateImmutable.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		DataClassField fieldDate = fields[0];
		Assertions.assertEquals("date", fieldDate.name());
		Assertions.assertEquals(Date.class, fieldDate.type());
        Assertions.assertFalse(fieldDate.isRequired());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		AtomicDateImmutable object = arrayMap.toObject(AtomicDateImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(AtomicDateImmutable.class, object);

		Assertions.assertEquals(TEST_TIMESTAMP, object.date().getTime());
	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		AtomicDateImmutable object = (AtomicDateImmutable) mapMapper.toObject(AtomicDateImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(AtomicDateImmutable.class, object);

		Assertions.assertEquals(TEST_TIMESTAMP, object.date().getTime());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("date", "error");

		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(AtomicDateImmutable.class, map);
		});

	}
}
