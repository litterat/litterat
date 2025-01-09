/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.test.core;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.test.core.data.IntAtom;
import io.litterat.test.core.data.IntAtomData;

public class IntAtomTest {

	final static IntAtom INT_ATOM_TEST = IntAtom.getAtom(23);

	IntAtomData test = new IntAtomData(INT_ATOM_TEST);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void testDescriptor() throws Throwable {
		DataClass descriptor = context.getDescriptor(IntAtom.class);

		Assertions.assertNotNull(descriptor);
        Assertions.assertInstanceOf(DataClassAtom.class, descriptor);

		DataClassAtom descriptorAtom = (DataClassAtom) descriptor;

		Assertions.assertEquals(IntAtom.class, descriptorAtom.typeClass());
		Assertions.assertEquals(int.class, descriptorAtom.dataClass());
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		// rebuild as an object.
		IntAtomData object = arrayMap.toObject(IntAtomData.class, values);

		// Validate
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(IntAtomData.class, object);
		Assertions.assertEquals(INT_ATOM_TEST, test.intAtom());
	}

	@Test
	public void testToMap() throws Throwable {

		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		IntAtomData object = (IntAtomData) mapMapper.toObject(IntAtomData.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(IntAtomData.class, object);
		Assertions.assertEquals(INT_ATOM_TEST, test.intAtom());
	}
}
