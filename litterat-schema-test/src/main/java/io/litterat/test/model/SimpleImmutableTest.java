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

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataClass;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Field;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Record;
import io.litterat.schema.meta.Typename;
import io.litterat.test.data.SimpleImmutable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
						new Field("x", Meta.INT32),
						new Field("y", Meta.INT32) });
		// @formatter:on

		TypeLibrary typeLibrary = new TypeLibrary();

		DataClass simpleImmutableDataClass = context.getDescriptor(SimpleImmutable.class);

		Typename typePoint = new Typename("point");

		typeLibrary.register(typePoint, simpleImmutableType);

		Definition pointDef = typeLibrary.getDefinition(typePoint);

		Assertions.assertNotNull(pointDef);
	}

}
