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

import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Meta;
import io.litterat.test.data.SimpleUUIDImmutable;
import io.litterat.test.data.UUIDBridge;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.TypeOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

public class UUIDBridgeTest {

	final static UUID FIRST_UUID = UUID.randomUUID();
	final static UUID SECOND_UUID = UUID.randomUUID();

	SimpleUUIDImmutable test = new SimpleUUIDImmutable(FIRST_UUID, SECOND_UUID);

	// This uses a UUID bridge to convert UUIDs to Strings.
	// The bridge could also convert to a byte[] as the output is binary for XPL.
	// The TypeMap/library/context combination must be passed in to allow access to
	// the bridge.

	TypeContext context;
	//TypeLibrary library;

	@BeforeEach
	public void setup() throws TypeException {
		context = TypeContext.builder().build();
		context.registerAtom(Meta.UUID, UUID.class, new UUIDBridge());
		//library = context.library();
	}

	@Test
	public void testWriteAndReadSimplePojo() throws IOException, TypeException {

		// Test writing out a Point.
		byte[] buffer = new byte[500];
		TypeOutputStream out = new TypeOutputStream(new TypeMap(context), buffer);
		out.writeObject(test);
		out.close();

		TypeInputStream in = new TypeInputStream(new TypeMap(context), buffer);
		SimpleUUIDImmutable p2 = in.readObject();

		Assertions.assertNotNull(p2);
		Assertions.assertEquals(test.first(), p2.first());
		Assertions.assertEquals(test.second(), p2.second());

		System.out.println("p1: " + test);
		System.out.println("p2: " + p2);

	}

}
