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
package io.litterat.json.test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.json.JsonMapper;
import io.litterat.pep.PepContext;
import io.litterat.pep.PepException;
import io.litterat.pep.test.data.SimpleUUIDImmutable;
import io.litterat.pep.test.data.UUIDBridge;

public class UUIDBridgeTest {

	final static UUID FIRST_UUID = UUID.randomUUID();
	final static UUID SECOND_UUID = UUID.randomUUID();

	SimpleUUIDImmutable test = new SimpleUUIDImmutable(FIRST_UUID, SECOND_UUID);

	PepContext context;

	@BeforeEach
	public void setup() throws PepException {
		context = PepContext.builder().build();
		context.registerAtom(UUID.class, new UUIDBridge());
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
		Assertions.assertEquals(FIRST_UUID, test.first());
		Assertions.assertEquals(SECOND_UUID, test.second());

	}


}
