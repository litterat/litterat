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
import io.litterat.test.data.ComplexImmutable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComplexImmutableTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	ComplexImmutable test = new ComplexImmutable(TEST_X, TEST_Y);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		// project to an array.
		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		ComplexImmutable object = JsonMapper.fromJson(json, ComplexImmutable.class);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ComplexImmutable);

		Assertions.assertEquals(TEST_X, object.x());
		Assertions.assertEquals(TEST_Y, object.y());
	}

}
