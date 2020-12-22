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
package io.litterat.test.pep;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.litterat.pep.PepContext;
import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.PepException;
import io.litterat.pep.mapper.PepArrayMapper;
import io.litterat.pep.mapper.PepMapMapper;
import io.litterat.test.pep.data.ProjectImmutable;

public class ProjectImmutableTest {

	final static int TEST_X = 1;
	final static int TEST_Y = 2;

	ProjectImmutable test = new ProjectImmutable(TEST_X, TEST_Y);

	PepContext context;

	@BeforeEach
	public void setup() {
		context = PepContext.builder().build();
	}

	@Test
	public void checkDescriptor() throws Throwable {
		PepContext context = new PepContext.Builder().build();
		PepDataClass descriptor = context.getDescriptor(ProjectImmutable.class);
		Assertions.assertNotNull(descriptor);

		Assertions.assertEquals(ProjectImmutable.class, descriptor.typeClass());
		Assertions.assertEquals(ProjectImmutable.ProjectImmutableData.class, descriptor.dataClass());

		PepDataComponent[] fields = descriptor.dataComponents();
		Assertions.assertNotNull(fields);
		Assertions.assertEquals(2, fields.length);

		PepDataComponent fieldX = fields[0];
		Assertions.assertEquals("a", fieldX.name());
		Assertions.assertEquals(int.class, fieldX.type());

		PepDataComponent fieldY = fields[1];
		Assertions.assertEquals("b", fieldY.name());
		Assertions.assertEquals(int.class, fieldY.type());
	}

	@Test
	public void testToArray() throws Throwable {

		// project to an array.
		PepArrayMapper arrayMap = new PepArrayMapper(context);
		Object[] values = arrayMap.toArray(test);
		Assertions.assertNotNull(values);

		System.out.println(Arrays.toString(values));

		// rebuild as an object.
		ProjectImmutable object = arrayMap.toObject(ProjectImmutable.class, values);

		// Validate
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ProjectImmutable);
		Assertions.assertEquals(TEST_X, object.x());
		Assertions.assertEquals(TEST_Y, object.y());

	}

	@Test
	public void testToMap() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		System.out.println(map.toString());

		ProjectImmutable object = (ProjectImmutable) mapMapper.toObject(ProjectImmutable.class, map);

		// validate result.
		Assertions.assertNotNull(object);
		Assertions.assertTrue(object instanceof ProjectImmutable);
		Assertions.assertEquals(TEST_X, object.x());
		Assertions.assertEquals(TEST_Y, object.y());
	}

	@Test
	public void testMapToObjectException() throws Throwable {
		PepMapMapper mapMapper = new PepMapMapper(context);
		Map<String, Object> map = mapMapper.toMap(test);

		// corrupting the map by putting an invalid value for a.
		map.put("a", "error");

		Assertions.assertThrows(PepException.class, () -> {
			mapMapper.toObject(ProjectImmutable.class, map);
		});

	}
}
