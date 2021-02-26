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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.PepContext;
import io.litterat.json.JsonMapper;
import io.litterat.test.bind.data.SimpleArray;
import io.litterat.test.bind.data.SimpleImmutable;

public class SimpleArrayTest {

	SimpleImmutable a1 = new SimpleImmutable(1, 2);
	SimpleImmutable a2 = new SimpleImmutable(22, 212);
	SimpleImmutable a3 = new SimpleImmutable(13, 3);

	SimpleImmutable[] array = { a1, a2, a3 };

	SimpleArray test = new SimpleArray(array);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		SimpleArray object = JsonMapper.fromJson(json, SimpleArray.class);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof SimpleArray);
		Assertions.assertEquals(3, object.arrayImmutable().length);
	}
}
