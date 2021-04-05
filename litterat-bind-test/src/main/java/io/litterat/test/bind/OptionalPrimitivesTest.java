/*
 * Copyright (c) 2021, Live Media Pty. Ltd. All Rights Reserved.
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
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.mapper.PepArrayMapper;
import io.litterat.bind.mapper.PepMapMapper;
import io.litterat.test.bind.data.OptionalPrimitives;

public class OptionalPrimitivesTest {

	OptionalPrimitives test1 = new OptionalPrimitives(OptionalInt.empty(), OptionalLong.empty(),
			OptionalDouble.empty());
	OptionalPrimitives test2 = new OptionalPrimitives(OptionalInt.of(1), OptionalLong.of(2l), OptionalDouble.of(3.0d));

	DataBindContext context;

	@BeforeEach
	public void setup() {
		context = DataBindContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(OptionalPrimitives.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(OptionalPrimitives.class, descriptor.typeClass());
		Assertions.assertEquals(OptionalPrimitives.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(3, fields.length);

		DataClassField intField = fields[0];
		Assertions.assertEquals("optionalInt", intField.name());
		Assertions.assertEquals(Integer.class, intField.type());

		DataClassField longField = fields[1];
		Assertions.assertEquals("optionalLong", longField.name());
		Assertions.assertEquals(Long.class, longField.type());

		DataClassField doubleField = fields[2];
		Assertions.assertEquals("optionalDouble", doubleField.name());
		Assertions.assertEquals(Double.class, doubleField.type());
	}

	@Test
	public void test1ToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test1);

		// convert to object.
		OptionalPrimitives object = arrayMap.toObject(OptionalPrimitives.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalPrimitives);

		Assertions.assertEquals(test1.optionalInt(), object.optionalInt());
		Assertions.assertEquals(test1.optionalLong(), object.optionalLong());
		Assertions.assertEquals(test1.optionalDouble(), object.optionalDouble());
	}

	@Test
	public void test1ToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test1);

		OptionalPrimitives object = (OptionalPrimitives) mapMapper.toObject(OptionalPrimitives.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalPrimitives);

		Assertions.assertEquals(test1.optionalInt(), object.optionalInt());
		Assertions.assertEquals(test1.optionalLong(), object.optionalLong());
		Assertions.assertEquals(test1.optionalDouble(), object.optionalDouble());
	}

	@Test
	public void test2ToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);

		// write to array.
		Object[] values = arrayMap.toArray(test2);

		// convert to object.
		OptionalPrimitives object = arrayMap.toObject(OptionalPrimitives.class, values);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalPrimitives);

		Assertions.assertEquals(test2.optionalInt(), object.optionalInt());
		Assertions.assertEquals(test2.optionalLong(), object.optionalLong());
		Assertions.assertEquals(test2.optionalDouble(), object.optionalDouble());
	}

	@Test
	public void test2ToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test2);

		OptionalPrimitives object = (OptionalPrimitives) mapMapper.toObject(OptionalPrimitives.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof OptionalPrimitives);

		Assertions.assertEquals(test2.optionalInt(), object.optionalInt());
		Assertions.assertEquals(test2.optionalLong(), object.optionalLong());
		Assertions.assertEquals(test2.optionalDouble(), object.optionalDouble());
	}

}
