/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
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

import io.litterat.core.TypeException;
import io.litterat.test.core.data.ComplexImmutable;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class ComplexImmutableTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	ComplexImmutable test = new ComplexImmutable(TEST_X, TEST_Y);

	@Test
	public void testWriteAndReadSimpleImmutable() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		ComplexImmutable p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(test.x(), p2.x());
		Assertions.assertEquals(test.y(), p2.y());

		System.out.println("p1: " + test);
		System.out.println("p2: " + p2);

	}
}
