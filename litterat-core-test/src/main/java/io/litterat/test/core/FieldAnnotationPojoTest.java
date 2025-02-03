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
package io.litterat.test.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.test.data.FieldAnnotationPojo;

public class FieldAnnotationPojoTest {

	FieldAnnotationPojo test = new FieldAnnotationPojo();

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassRecord descriptor = (DataClassRecord) context.getDescriptor(FieldAnnotationPojo.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(FieldAnnotationPojo.class, descriptor.typeClass());
		// Assertions.assertEquals(FieldAnnotationPojo.class, descriptor.dataClass());

		DataClassField[] fields = descriptor.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(9, fields.length);

		DataClassField fieldA = fields[0];
		Assertions.assertEquals("a", fieldA.name());
		Assertions.assertEquals(String.class, fieldA.type());
        Assertions.assertTrue(fieldA.isRequired());

		DataClassField fieldB = fields[1];
		Assertions.assertEquals("b", fieldB.name());
		Assertions.assertEquals(String.class, fieldB.type());
        Assertions.assertFalse(fieldB.isRequired());

		DataClassField fieldC = fields[2];
		Assertions.assertEquals("c", fieldC.name());
		Assertions.assertEquals(String.class, fieldC.type());
        Assertions.assertTrue(fieldC.isRequired());

		DataClassField fieldD = fields[3];
		Assertions.assertEquals("d", fieldD.name());
		Assertions.assertEquals(String.class, fieldD.type());
        Assertions.assertTrue(fieldD.isRequired());

		DataClassField fieldE = fields[4];
		Assertions.assertEquals("e", fieldE.name());
		Assertions.assertEquals(String.class, fieldE.type());
        Assertions.assertFalse(fieldE.isRequired());

		DataClassField fieldF = fields[5];
		Assertions.assertEquals("f", fieldF.name());
		Assertions.assertEquals(String.class, fieldF.type());
        Assertions.assertTrue(fieldF.isRequired());

		DataClassField fieldG = fields[6];
		Assertions.assertEquals("g", fieldG.name());
		Assertions.assertEquals(String.class, fieldG.type());
        Assertions.assertTrue(fieldG.isRequired());

		DataClassField fieldH = fields[7];
		Assertions.assertEquals("h", fieldH.name());
		Assertions.assertEquals(String.class, fieldH.type());
        Assertions.assertFalse(fieldH.isRequired());

		DataClassField fieldI = fields[8];
		Assertions.assertEquals("i", fieldI.name());
		Assertions.assertEquals(String.class, fieldI.type());
        Assertions.assertTrue(fieldI.isRequired());
	}

}
