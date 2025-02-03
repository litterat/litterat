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
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.schema.meta.Meta;
import io.litterat.test.data.SimpleUUIDImmutable;
import io.litterat.test.data.UUIDBridge;

public class UUIDBridgeTest {

	final static UUID FIRST_UUID = UUID.randomUUID();
	final static UUID SECOND_UUID = UUID.randomUUID();

	SimpleUUIDImmutable test = new SimpleUUIDImmutable(FIRST_UUID, SECOND_UUID);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {
		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());

	}

	@Test
	public void testToArray() throws Throwable {

		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		// rebuild as an object.
		SimpleUUIDImmutable object = arrayMap.toObject(SimpleUUIDImmutable.class, values);

		// Validate
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimpleUUIDImmutable.class, object);
		Assertions.assertEquals(FIRST_UUID, object.first());
		Assertions.assertEquals(SECOND_UUID, object.second());

	}

	@Test
	public void testToMap() throws Throwable {

		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());

		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		SimpleUUIDImmutable object = (SimpleUUIDImmutable) mapMapper.toObject(SimpleUUIDImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SimpleUUIDImmutable.class, object);
		Assertions.assertEquals(FIRST_UUID, object.first());
		Assertions.assertEquals(SECOND_UUID, object.second());
	}
}
