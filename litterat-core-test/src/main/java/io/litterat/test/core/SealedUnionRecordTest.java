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
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.bind.mapper.ArrayMapper;
import io.litterat.bind.mapper.MapMapper;
import io.litterat.core.TypeContext;
import io.litterat.test.data.SealedUnionRecord.RecordCircle;
import io.litterat.test.data.SealedUnionRecord.RecordPoint;
import io.litterat.test.data.SealedUnionRecord.SealedShape;
import io.litterat.test.data.SealedUnionRecord.SealedShapeList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SealedUnionRecordTest {

	final static SealedShape TEST_ONE = new RecordPoint(1, 2);
	final static SealedShape TEST_TWO = new RecordCircle(3, 4, 5, "test");

	List<SealedShape> testList = List.of(TEST_ONE, TEST_TWO);
	SealedShape[] testArray = { TEST_ONE, TEST_TWO };

	SealedShapeList test = new SealedShapeList(testList, testArray);

	TypeContext context;

	@BeforeEach
	public void setup() {
		context = TypeContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {

		DataClassUnion descriptor = (DataClassUnion) context.getDescriptor(SealedShape.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(SealedShape.class, descriptor.typeClass());

		// A union initially has no implementations.
		Class<?>[] components = descriptor.memberTypes();
		Assertions.assertNotNull(components);
		Assertions.assertEquals(2, components.length);

		Class<?> memberClass = components[0];
		DataClass memberType = context.getDescriptor(memberClass);
        Assertions.assertInstanceOf(DataClassRecord.class, memberType);
		Assertions.assertEquals(RecordPoint.class, memberType.typeClass());

		Class<?> memberClassCircle = components[1];
		DataClass memberTypeCircle = context.getDescriptor(memberClassCircle);
        Assertions.assertInstanceOf(DataClassRecord.class, memberTypeCircle);
		Assertions.assertEquals(RecordCircle.class, memberTypeCircle.typeClass());
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		ArrayMapper arrayMap = new ArrayMapper(context.dataBindContext());

		// write to array.
		Object[] values = arrayMap.toArray(test);

		// convert to object.
		SealedShapeList object = arrayMap.toObject(SealedShapeList.class, values);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SealedShapeList.class, object);

		Assertions.assertEquals(TEST_ONE, object.list().get(0));
		Assertions.assertEquals(TEST_TWO, object.list().get(1));

		Assertions.assertEquals(TEST_ONE, object.array()[0]);
		Assertions.assertEquals(TEST_TWO, object.array()[1]);
	}

	@Test
	public void testToMap() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());
		Map<String, Object> map = mapMapper.toMap(test);

        Assertions.assertTrue(map.containsKey("list"));
		Object[] listArray = (Object[]) map.get("list");
		System.out.println(Arrays.toString(listArray));

		SealedShapeList object = (SealedShapeList) mapMapper.toObject(SealedShapeList.class, map);

		// validate result.
		Assertions.assertNotNull(object);
        Assertions.assertInstanceOf(SealedShapeList.class, object);

		Assertions.assertEquals(TEST_ONE, object.list().get(0));
		Assertions.assertEquals(TEST_TWO, object.list().get(1));

		Assertions.assertEquals(TEST_ONE, object.array()[0]);
		Assertions.assertEquals(TEST_TWO, object.array()[1]);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMapToObjectException() throws Throwable {
		MapMapper mapMapper = new MapMapper(context.dataBindContext());

		Map<String, Object> map = mapMapper.toMap(test);

		// Change the type of a value to a Rectangle which is not valid.
		Object[] listOfUnion = (Object[]) map.get("list");
		Map<String, Object> point = (Map<String, Object>) listOfUnion[0];
		point.put("type", "io.litterat.test.bind.union.SealedInterfaceUnionRectangle");

		// Fails because Rectangle is not part of the SealedInterfaceUnion union list.
		Assertions.assertThrows(DataBindException.class, () -> {
			mapMapper.toObject(SealedShapeList.class, map);
		});

	}
}
