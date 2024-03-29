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
package io.litterat.test.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataClass;
import io.litterat.model.Definition;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.TypeName;
import io.litterat.model.library.TypeLibrary;
import io.litterat.test.bind.data.SimpleImmutable;

public class SimpleImmutableTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	SimpleImmutable test = new SimpleImmutable(TEST_X, TEST_Y);

	DataBindContext context;

	@BeforeEach
	public void setup() {
		context = DataBindContext.builder().build();
	}

	@Test
	public void testToBindSimpleImmutable() throws Throwable {

		// @formatter:off
		Record simpleImmutableType = new Record(
				new Field[] { 
						new Field("x", TypeLibrary.INT32), 
						new Field("y", TypeLibrary.INT32) });
		// @formatter:on

		TypeLibrary typeLibrary = new TypeLibrary();

		DataClass simpleImmutableDataClass = context.getDescriptor(SimpleImmutable.class);

		TypeName typePoint = new TypeName("point");

		typeLibrary.register(typePoint, simpleImmutableType, simpleImmutableDataClass);

		Definition pointDef = typeLibrary.getDefinition(typePoint);

		Assertions.assertNotNull(pointDef);
	}

}
