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
import org.junit.jupiter.api.Test;

import io.litterat.model.TypeLibrary;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.SlotSet;
import io.litterat.xpl.lang.Value;

public class SlotSetGeneratorTest {

	@Test
	public void testBind() throws Throwable {

		Integer value = Integer.valueOf(123);
		int varSlot = 0;

		Value valueRef = new Value(Integer.class, value);
		SlotSet setNode = new SlotSet(varSlot, valueRef);
		ValueGenerator valueGen = new ValueGenerator(valueRef);
		SlotSetGenerator slotSetGen = new SlotSetGenerator(setNode, valueGen);

		Class<?>[] slots = new Class[] { Integer.class };

		TypeLibrary library = new TypeLibrary();
		TypeMap typeMap = new TypeMap(library);
		LitteratMachine am = new LitteratMachine(typeMap, slots);
		MethodHandle handle = slotSetGen.bind(am);

		handle.invoke(am);

		Assertions.assertEquals(value, am.getVariable(varSlot));
	}
}
