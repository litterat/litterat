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
import io.litterat.schema.meta.Meta;
import io.litterat.test.data.ImmutableAtom;
import io.litterat.test.data.SimpleEnum;
import io.litterat.test.data.UUIDBridge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

public class ImmutableAtomTest {

	final static SimpleEnum ENUM_TEST = SimpleEnum.THREE;
	final static String STR_TEST = "test";
	final static boolean BOOL_TEST = true;
	final static Optional<String> OPTION_TEST = Optional.of("foo");

	ImmutableAtom test = new ImmutableAtom(ENUM_TEST, STR_TEST, BOOL_TEST, OPTION_TEST);

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
	public void testToJson() throws Throwable {

		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());

		// project to an array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		ImmutableAtom object = JsonMapper.fromJson(json, ImmutableAtom.class);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ImmutableAtom);
		Assertions.assertEquals(ENUM_TEST, object.enumCount());
		Assertions.assertEquals(STR_TEST, object.str());
		Assertions.assertEquals(BOOL_TEST, object.bool());
		Assertions.assertEquals(OPTION_TEST, object.optional());
	}

}
