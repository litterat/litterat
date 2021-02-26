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
import io.litterat.test.bind.data.SimplePojo;

public class SimplePojoTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	SimplePojo test;

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();

		test = new SimplePojo();
		test.setX(TEST_X);
		test.setY(TEST_Y);
	}

	@Test
	public void testToJson() throws Throwable {

		// project to an array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		SimplePojo object = JsonMapper.fromJson(json, SimplePojo.class);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof SimplePojo);
		Assertions.assertEquals(TEST_X, object.getX());
		Assertions.assertEquals(TEST_Y, object.getY());

	}

}
