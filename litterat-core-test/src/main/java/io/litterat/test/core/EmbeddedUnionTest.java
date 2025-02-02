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

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.core.TypeContext;
import io.litterat.test.core.union.EmbeddedUnion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class EmbeddedUnionTest {

	EmbeddedUnion testString = new EmbeddedUnion("test");
	EmbeddedUnion testInteger = new EmbeddedUnion(10);
	EmbeddedUnion testError = new EmbeddedUnion(11L);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClass descriptor = context.getDescriptor(EmbeddedUnion.class);

		Assertions.assertNotNull(descriptor);
        Assertions.assertInstanceOf(DataClassRecord.class, descriptor);

		DataClassRecord descriptorRecord = (DataClassRecord) descriptor;

		Assertions.assertEquals(EmbeddedUnion.class, descriptorRecord.typeClass());
		// Assertions.assertEquals(EmbeddedUnion.class, descriptorRecord.dataClass());

		DataClassField[] fields = descriptorRecord.fields();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(1, fields.length);

		DataClassField fieldIdentifier = fields[0];
		Assertions.assertEquals("identifier", fieldIdentifier.name());
		Assertions.assertEquals(Object.class, fieldIdentifier.type());
        Assertions.assertFalse(fieldIdentifier.isRequired());

		DataClassUnion union = (DataClassUnion) fieldIdentifier.dataClass();
		Assertions.assertNotNull(union);
		Assertions.assertEquals(2, union.memberTypes().length);
		Assertions.assertTrue(union.isSealed());

        Assertions.assertInstanceOf(DataClassAtom.class, context.getDescriptor(union.memberTypes()[0]));
        Assertions.assertInstanceOf(DataClassAtom.class, context.getDescriptor(union.memberTypes()[0]));
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] valuesString = arrayMap.toArray(testString);
		Object[] valuesInteger = arrayMap.toArray(testInteger);

		// convert to object.
		EmbeddedUnion objectString = arrayMap.toObject(EmbeddedUnion.class, valuesString);
		EmbeddedUnion objectInteger = arrayMap.toObject(EmbeddedUnion.class, valuesInteger);

		// validate result.
		Assertions.assertNotNull(objectString);
		Assertions.assertNotNull(objectInteger);

		Assertions.assertEquals(testString.identifier(), objectString.identifier());
		Assertions.assertEquals(testInteger.identifier(), objectInteger.identifier());
	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> mapString = mapMapper.toMap(testString);
		Map<String, Object> mapInteger = mapMapper.toMap(testInteger);

		EmbeddedUnion objectString = (EmbeddedUnion) mapMapper.toObject(EmbeddedUnion.class, mapString);
		EmbeddedUnion objectInteger = (EmbeddedUnion) mapMapper.toObject(EmbeddedUnion.class, mapInteger);

		// validate result.
		Assertions.assertNotNull(objectString);
		Assertions.assertNotNull(objectInteger);

		Assertions.assertEquals(testString.identifier(), objectString.identifier());
		Assertions.assertEquals(testInteger.identifier(), objectInteger.identifier());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());

		Map<String, Object> map;
		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toMap(testError);
		});

		// corrupting the map by putting an invalid value for x.
		map = new HashMap<>();
		map.put("identifier", 1L);

		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(EmbeddedUnion.class, map);
		});

	}

}
