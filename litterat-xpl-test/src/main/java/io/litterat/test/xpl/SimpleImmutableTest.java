/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.litterat.model.TypeException;
import io.litterat.test.bind.data.SimpleImmutable;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class SimpleImmutableTest {

	@Test
	public void testWriteAndReadSimpleImmutable() throws IOException, TypeException {

		SimpleImmutable p1 = new SimpleImmutable(42, 2);

		// Test writing out a Point.
		byte[] buffer = new byte[150];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(p1);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		SimpleImmutable p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(p1, p2);

		System.out.println("p1: " + p1);
		System.out.println("p2: " + p2);

	}

}
