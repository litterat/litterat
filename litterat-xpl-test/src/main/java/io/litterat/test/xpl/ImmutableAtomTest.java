/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.litterat.test.xpl;

import io.litterat.schema.TypeException;
import io.litterat.test.data.ImmutableAtom;
import io.litterat.test.data.SimpleEnum;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class ImmutableAtomTest {

	final static SimpleEnum ENUM_TEST = SimpleEnum.THREE;
	final static String STR_TEST = "test";
	final static boolean BOOL_TEST = true;
	final static Optional<String> OPTION_TEST = Optional.of("foo");

	ImmutableAtom test = new ImmutableAtom(ENUM_TEST, STR_TEST, BOOL_TEST, OPTION_TEST);

	// Disabled until after refactor.
	@Test
	public void testWriteAndReadImmutableAtom() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		ImmutableAtom p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(test.enumCount(), p2.enumCount());
		Assertions.assertEquals(test.str(), p2.str());
		Assertions.assertEquals(test.bool(), p2.bool());
		Assertions.assertEquals(test.optional(), p2.optional());

		System.out.println("p1: " + test);
		System.out.println("p2: " + p2);

	}
}
