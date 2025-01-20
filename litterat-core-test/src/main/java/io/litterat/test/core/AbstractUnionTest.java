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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.litterat.bind.DataBindException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.test.core.union.AbstractUnion;
import io.litterat.test.core.union.AbstractUnionCircle;
import io.litterat.test.core.union.AbstractUnionList;
import io.litterat.test.core.union.AbstractUnionRectangle;

public class AbstractUnionTest {

	final static AbstractUnion TEST_ONE = new AbstractUnionCircle(1, 2, 3);
	final static AbstractUnion TEST_TWO = new AbstractUnionRectangle(3, 4, 5, 6);
	final static AbstractUnion TEST_THREE = new AbstractUnionCircle(7, 8, 9);

	List<AbstractUnion> testList = List.of(TEST_ONE, TEST_TWO, TEST_THREE);
	AbstractUnion[] testArray = { TEST_ONE, TEST_TWO, TEST_THREE };

	AbstractUnionList test = new AbstractUnionList(testList, testArray);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassUnion descriptor = (DataClassUnion) context.getDescriptor(AbstractUnion.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(AbstractUnion.class, descriptor.typeClass());

		// A union initially has no implementations.
		Class<?>[] components = descriptor.memberTypes();
		Assertions.assertNotNull(components);
		Assertions.assertEquals(0, components.length);

		DataClass pointDescriptor = context.getDescriptor(AbstractUnionCircle.class);
		Assertions.assertNotNull(pointDescriptor);

		// Need to get hold of latest version of array.
		components = descriptor.memberTypes();
		Assertions.assertEquals(1, components.length);

		Class<?> memberType = components[0];
		DataClass dataClass = context.getDescriptor(memberType);
        Assertions.assertInstanceOf(DataClassRecord.class, dataClass);
		Assertions.assertEquals(AbstractUnionCircle.class, dataClass.typeClass());

	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		AbstractUnionList object = arrayMap.toObject(AbstractUnionList.class, values);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(AbstractUnionList.class, object);

		Assertions.assertEquals(TEST_ONE, object.list().get(0));
		Assertions.assertEquals(TEST_TWO, object.list().get(1));
		Assertions.assertEquals(TEST_THREE, object.list().get(2));

		Assertions.assertEquals(TEST_ONE, object.array()[0]);
		Assertions.assertEquals(TEST_TWO, object.array()[1]);
		Assertions.assertEquals(TEST_THREE, object.array()[2]);

	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

        Assertions.assertTrue(map.containsKey("list"));
		Object[] listArray = (Object[]) map.get("list");
		System.out.println(Arrays.toString(listArray));

		AbstractUnionList object = (AbstractUnionList) mapMapper.toObject(AbstractUnionList.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(AbstractUnionList.class, object);

		Assertions.assertEquals(TEST_ONE, object.list().get(0));
		Assertions.assertEquals(TEST_TWO, object.list().get(1));
		Assertions.assertEquals(TEST_THREE, object.list().get(2));

		Assertions.assertEquals(TEST_ONE, object.array()[0]);
		Assertions.assertEquals(TEST_TWO, object.array()[1]);
		Assertions.assertEquals(TEST_THREE, object.array()[2]);

	}

	@Test
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for x.
		map.put("list", "error");

		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(AbstractUnionList.class, map);
		});

	}
}
