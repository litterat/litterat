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
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.model.TypeException;
import io.litterat.test.bind.data.OptionalImmutable;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;

public class OptionalImmutableTest {

	OptionalImmutable test1 = new OptionalImmutable(Optional.empty());
	OptionalImmutable test2 = new OptionalImmutable(Optional.of("test"));

	DataBindContext context;

	@Test
	public void testWriteAndReadMixedImmutable() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(buffer);
		out.writeObject(test1);
		out.writeObject(test2);
		out.close();

		TypeInputStream in = new TypeInputStream(buffer);
		OptionalImmutable t1 = in.readObject();
		OptionalImmutable t2 = in.readObject();

		Assertions.assertNotNull(t1);
		Assertions.assertNotNull(t2);
		Assertions.assertEquals(test1.optionalString(), t1.optionalString());
		Assertions.assertEquals(test2.optionalString(), t2.optionalString());

		System.out.println("test2: " + test2);
		System.out.println("t2: " + t2);

	}

}
