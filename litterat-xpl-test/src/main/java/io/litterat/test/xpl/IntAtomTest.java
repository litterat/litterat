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

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.test.core.data.IntAtom;
import io.litterat.test.core.data.IntAtomData;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class IntAtomTest {

	final static IntAtom INT_ATOM_TEST = IntAtom.getAtom(23);

	IntAtomData test = new IntAtomData(INT_ATOM_TEST);

	TypeContext context;

	// Disabled until after refactor
	@Disabled
	@Test
	public void testWriteAndReadMixedImmutable() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		IntAtomData p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(test.intAtom().id(), p2.intAtom().id());

		System.out.println("p1: " + test);
		System.out.println("p2: " + p2);

	}
}
