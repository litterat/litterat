/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.litterat.test.bind;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.test.bind.data.ImmutableAtom;
import io.litterat.test.bind.data.SimpleEnum;
import io.litterat.test.bind.data.UUIDBridge;

public class ImmutableAtomTest {

	final static SimpleEnum ENUM_TEST = SimpleEnum.THREE;
	final static String STR_TEST = "test";
	final static boolean BOOL_TEST = true;
	final static Optional<String> OPTION_TEST = Optional.of("foo");

	ImmutableAtom test = new ImmutableAtom(ENUM_TEST, STR_TEST, BOOL_TEST, OPTION_TEST);

	DataBindContext context;

	@BeforeEach
	public void setup() {
		context = DataBindContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {
		context.registerAtom(UUID.class, new UUIDBridge());

	}

	@Test
	public void testToArray() throws Throwable {

		context.registerAtom(UUID.class, new UUIDBridge());

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context);
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		System.out.println(Arrays.toString(values));

		// rebuild as an object.
		ImmutableAtom object = arrayMap.toObject(ImmutableAtom.class, values);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ImmutableAtom);
		Assertions.assertEquals(ENUM_TEST, object.enumCount());
		Assertions.assertEquals(STR_TEST, object.str());
		Assertions.assertEquals(BOOL_TEST, object.bool());
		Assertions.assertEquals(OPTION_TEST, object.optional());
	}

	@Test
	public void testToMap() throws Throwable {

		context.registerAtom(UUID.class, new UUIDBridge());

		MapMapper mapMapper = new MapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		ImmutableAtom object = (ImmutableAtom) mapMapper.toObject(ImmutableAtom.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ImmutableAtom);
		Assertions.assertEquals(ENUM_TEST, object.enumCount());
		Assertions.assertEquals(STR_TEST, object.str());
		Assertions.assertEquals(BOOL_TEST, object.bool());
		Assertions.assertEquals(OPTION_TEST, object.optional());
	}
}
