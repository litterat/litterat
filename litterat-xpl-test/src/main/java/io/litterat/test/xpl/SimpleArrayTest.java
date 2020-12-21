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
package io.litterat.test.xpl;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.litterat.test.pep.data.SimpleArray;
import io.litterat.test.pep.data.SimpleImmutable;
import io.litterat.schema.TypeException;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class SimpleArrayTest {

	SimpleImmutable a1 = new SimpleImmutable(1, 2);
	SimpleImmutable a2 = new SimpleImmutable(22, 212);
	SimpleImmutable a3 = new SimpleImmutable(13, 3);

	SimpleImmutable[] array = { a1, a2, a3 };

	SimpleArray test = new SimpleArray(array);

	@Test
	public void testWriteAndReadSimpleArray() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		SimpleArray p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(test.arrayImmutable().length, p2.arrayImmutable().length);

		System.out.println("p1: " + Arrays.toString(test.arrayImmutable()));
		System.out.println("p2: " + Arrays.toString(p2.arrayImmutable()));

	}
}
