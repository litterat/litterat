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

import java.util.Date;
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
import io.litterat.test.bind.data.AtomicDateImmutable;

public class AtomicDateImmutableTest {

	final static long TEST_TIMESTAMP = System.currentTimeMillis();

	AtomicDateImmutable test = new AtomicDateImmutable(new Date(TEST_TIMESTAMP));

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		PepDataClass descriptor = context.getDescriptor(AtomicDateImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(AtomicDateImmutable.class, descriptor.typeClass());
		Assertions.assertEquals(AtomicDateImmutable.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		PepDataComponent fieldDate = fields[0];
		Assertions.assertEquals("date", fieldDate.name());
		Assertions.assertEquals(Date.class, fieldDate.type());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		AtomicDateImmutable object = arrayMap.toObject(AtomicDateImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicDateImmutable);

		Assertions.assertEquals(TEST_TIMESTAMP, object.date().getTime());
	}

	@Test
	public void testToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		AtomicDateImmutable object = (AtomicDateImmutable) mapMapper.toObject(AtomicDateImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof AtomicDateImmutable);

		Assertions.assertEquals(TEST_TIMESTAMP, object.date().getTime());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("date", "error");

		Assertions.assertThrows(PepException.class, () -> {
			mapMapper.toObject(AtomicDateImmutable.class, map);
		});

	}
}