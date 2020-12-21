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
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.pep.PepContext;
import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.mapper.PepArrayMapper;
import io.litterat.pep.mapper.PepMapMapper;
import io.litterat.test.pep.data.OptionalImmutable;

public class OptionalImmutableTest {

	OptionalImmutable test1 = new OptionalImmutable(Optional.empty());
	OptionalImmutable test2 = new OptionalImmutable(Optional.of("test"));

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		PepDataClass descriptor = context.getDescriptor(OptionalImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(OptionalImmutable.class, descriptor.typeClass());
		Assertions.assertEquals(OptionalImmutable.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		PepDataComponent field = fields[0];
		Assertions.assertEquals("optionalString", field.name());
		Assertions.assertEquals(String.class, field.type());

	}

	@Test
	public void test1ToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test1);

		// convert to object.
		OptionalImmutable object = arrayMap.toObject(OptionalImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalImmutable);

		Assertions.assertEquals(test1.optionalString(), object.optionalString());

	}

	@Test
	public void test1ToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test1);

		OptionalImmutable object = (OptionalImmutable) mapMapper.toObject(OptionalImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalImmutable);

		Assertions.assertEquals(test1.optionalString(), object.optionalString());
	}

	@Test
	public void test2ToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test2);

		// convert to object.
		OptionalImmutable object = arrayMap.toObject(OptionalImmutable.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalImmutable);

		Assertions.assertEquals(test2.optionalString(), object.optionalString());

	}

	@Test
	public void test2ToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test2);

		OptionalImmutable object = (OptionalImmutable) mapMapper.toObject(OptionalImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalImmutable);

		Assertions.assertEquals(test2.optionalString(), object.optionalString());
	}

}
