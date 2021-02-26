/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.xpl.lang.mh;

import java.lang.invoke.MethodHandle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.litterat.model.TypeLibrary;
import io.litterat.model.types.TypeName;
import io.litterat.xpl.Point;
import io.litterat.xpl.TypeBaseInput;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.Value;

public class FieldSetGeneratorTest {

	@Test
	@Disabled
	public void testBind() throws Throwable {

		Point p = new Point(1.23f, 2.34f);

		float newValue = 4.23f;

		Class<?>[] slots = new Class[] { TypeBaseInput.class };

		// Build a AST for setting the latitude value. The FieldSet goes directly to the
		// class field so bypasses private/final.
		Value pointRef = new Value(Point.class, p);
		Value valueRef = new Value(float.class, newValue);
		FieldSet setNode = new FieldSet(pointRef, valueRef, new TypeName("io.litterat.xpl", "point"), "latitude");
		ValueGenerator pointGen = new ValueGenerator(pointRef);
		ValueGenerator valueGen = new ValueGenerator(valueRef);

		// The generator will prepare the MethodHandle.
		FieldSetGenerator gen = new FieldSetGenerator(setNode, pointGen, valueGen);

		// Setup a context and machine to execute.
		TypeLibrary library = new TypeLibrary();
		TypeMap typeMap = new TypeMap(library);
		LitteratMachine am = new LitteratMachine(typeMap, slots);

		// bind and execture.
		MethodHandle handle = gen.bind(am);
		handle.invoke(am);

		// Check the value has been set.
		Assertions.assertEquals(p.latitude(), newValue);
	}
}
