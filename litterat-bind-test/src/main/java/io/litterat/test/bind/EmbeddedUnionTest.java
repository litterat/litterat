/*
 * Copyright (c) 2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.test.bind;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.test.bind.union.EmbeddedUnion;

public class EmbeddedUnionTest {

	EmbeddedUnion testString = new EmbeddedUnion("test");
	EmbeddedUnion testInteger = new EmbeddedUnion(Integer.valueOf(10));
	EmbeddedUnion testError = new EmbeddedUnion(Long.valueOf(11l));

	DataBindContext context;

	@BeforeEach
	public void setup() {
		context = DataBindContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClass descriptor = context.getDescriptor(EmbeddedUnion.class);

		Assertions.assertNotNull(descriptor);
		Assertions.assertTrue(descriptor instanceof DataClassRecord);

		DataClassRecord descriptorRecord = (DataClassRecord) descriptor;

		Assertions.assertEquals(EmbeddedUnion.class, descriptorRecord.typeClass());
		Assertions.assertEquals(EmbeddedUnion.class, descriptorRecord.dataClass());

		DataClassField[] fields = descriptorRecord.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		DataClassField fieldIdentifier = fields[0];
		Assertions.assertEquals("identifier", fieldIdentifier.name());
		Assertions.assertEquals(Object.class, fieldIdentifier.type());
		Assertions.assertEquals(false, fieldIdentifier.isRequired());

		DataClassUnion union = (DataClassUnion) fieldIdentifier.dataClass();
		Assertions.assertNotNull(union);
		Assertions.assertEquals(2, union.memberTypes().length);

		Assertions.assertTrue(union.memberTypes()[0] instanceof DataClassAtom);
		Assertions.assertTrue(union.memberTypes()[1] instanceof DataClassAtom);
	}

}
