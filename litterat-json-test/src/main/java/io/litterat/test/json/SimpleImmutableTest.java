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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.json.JsonMapper;
import io.litterat.pep.PepContext;
import io.litterat.pep.test.data.SimpleImmutable;
 

public class SimpleImmutableTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	SimpleImmutable test = new SimpleImmutable(TEST_X, TEST_Y);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		// write to array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		SimpleImmutable t2 = JsonMapper.fromJson(json, SimpleImmutable.class);
		Assertions.assertNotNull(t2);
	}

}
