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
import io.litterat.test.data.ListOfString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ListOfStringTest {

	final static String TEST_ONE = "one";
	final static String TEST_TWO = "two";
	final static String TEST_THREE = "three";

	List<String> testList = List.of(TEST_ONE, TEST_TWO, TEST_THREE);

	ListOfString test = new ListOfString(testList);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void testToJson() throws Throwable {

		String json = JsonMapper.toJson(test);

		System.out.println("json: " + json);

		ListOfString object = JsonMapper.fromJson(json, ListOfString.class);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ListOfString);
		Assertions.assertEquals(3, object.list().size());

		Assertions.assertEquals(TEST_ONE, object.list().get(0));
		Assertions.assertEquals(TEST_TWO, object.list().get(1));
		Assertions.assertEquals(TEST_THREE, object.list().get(2));
	}
}
