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

import io.litterat.core.TypeContext;
import io.litterat.json.JsonMapper;
import io.litterat.test.core.data.IntAtom;
import io.litterat.test.core.data.IntAtomData;

public class IntAtomTest {

	final static IntAtom INT_ATOM_TEST = IntAtom.getAtom(23);

	IntAtomData test = new IntAtomData(INT_ATOM_TEST);

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

		IntAtomData object = JsonMapper.fromJson(json, IntAtomData.class);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof IntAtomData);
		Assertions.assertEquals(INT_ATOM_TEST, object.intAtom());
	}

}
