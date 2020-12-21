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

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.json.JsonMapper;
import io.litterat.pep.PepContext;
import io.litterat.test.pep.data.ImmutableAtom;
import io.litterat.test.pep.data.SimpleEnum;
import io.litterat.test.pep.data.UUIDBridge;

public class ImmutableAtomTest {

	final static SimpleEnum ENUM_TEST = SimpleEnum.THREE;
	final static String STR_TEST = "test";
	final static boolean BOOL_TEST = true;
	final static Optional<String> OPTION_TEST = Optional.of("foo");

	ImmutableAtom test = new ImmutableAtom(ENUM_TEST, STR_TEST, BOOL_TEST, OPTION_TEST);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {
		context.registerAtom(UUID.class, new UUIDBridge());

	}

	@Test
	public void testToJson() throws Throwable {

		context.registerAtom(UUID.class, new UUIDBridge());

		// project to an array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		ImmutableAtom object = JsonMapper.fromJson(json, ImmutableAtom.class);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ImmutableAtom);
		Assertions.assertEquals(ENUM_TEST, test.enumCount());
		Assertions.assertEquals(STR_TEST, test.str());
		Assertions.assertEquals(BOOL_TEST, test.bool());
		Assertions.assertEquals(OPTION_TEST, test.optional());
	}

}
