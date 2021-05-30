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
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.litterat.model.library.TypeException;
import io.litterat.test.bind.data.PrimitiveArray;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class PrimitiveArrayTest {

	final static int[] TEST = new int[] { 1, 2, 3 };

	PrimitiveArray test = new PrimitiveArray(TEST);

	@Test
	@Disabled
	public void testWriteAndReadSimpleArray() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		PrimitiveArray object = in.readObject();

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof PrimitiveArray);

		Assertions.assertTrue(Arrays.equals(TEST, object.intArray()));

	}
}
