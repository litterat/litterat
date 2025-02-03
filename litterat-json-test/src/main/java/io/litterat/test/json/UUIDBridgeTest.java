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
package io.litterat.test.json;

import io.litterat.core.TypeContext;
import io.litterat.json.JsonMapper;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Meta;
import io.litterat.test.data.SimpleUUIDImmutable;
import io.litterat.test.data.UUIDBridge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

public class UUIDBridgeTest {

	final static UUID FIRST_UUID = UUID.randomUUID();
	final static UUID SECOND_UUID = UUID.randomUUID();

	SimpleUUIDImmutable test = new SimpleUUIDImmutable(FIRST_UUID, SECOND_UUID);

	TypeContext context;

	@BeforeEach
	public void setup() throws TypeException {
		context = TypeContext.builder().build();
		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());
	}

	@Test
	public void testToJson() throws Throwable {

		// Requires a bit more boiler plate to pass in context.
		JsonMapper mapper = new JsonMapper(context);
		StringWriter writer = new StringWriter();
		mapper.toJson(test, writer);
		String json = writer.toString();

		System.out.println("json: " + json);

		StringReader reader = new StringReader(json);
		SimpleUUIDImmutable object = mapper.fromJson(json, SimpleUUIDImmutable.class, reader);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof SimpleUUIDImmutable);
		Assertions.assertEquals(FIRST_UUID, object.first());
		Assertions.assertEquals(SECOND_UUID, object.second());

	}

}
