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

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.json.JsonMapper;
import io.litterat.test.bind.data.PrimitiveArray;

public class PrimitiveArrayTest {

	final static int[] TEST = new int[] { 1, 2, 3 };

	PrimitiveArray test = new PrimitiveArray(TEST);

	DataBindContext context;

	@BeforeEach
	public void setup() {
		context = DataBindContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		PrimitiveArray object = JsonMapper.fromJson(json, PrimitiveArray.class);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof PrimitiveArray);
		Assertions.assertEquals(3, object.intArray().length);

		Assertions.assertTrue(Arrays.equals(TEST, object.intArray()));
	}
}
