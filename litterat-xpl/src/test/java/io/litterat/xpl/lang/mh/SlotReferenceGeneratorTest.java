/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
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

import io.litterat.model.library.TypeLibrary;
import io.litterat.xpl.TypeBaseInput;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.SlotReference;

public class SlotReferenceGeneratorTest {

	@Test
	public void testBind() throws Throwable {

		Integer value = Integer.valueOf(123);
		int varSlot = 2;

		SlotReference refNode = new SlotReference(varSlot);
		SlotReferenceGenerator slotRefGen = new SlotReferenceGenerator(refNode);

		Class<?>[] slots = new Class[] { TypeMap.class, TypeBaseInput.class, Integer.class };

		TypeLibrary library = new TypeLibrary();
		TypeMap typeMap = new TypeMap(library);
		LitteratMachine am = new LitteratMachine(typeMap, slots);

		MethodHandle handle = slotRefGen.bind(am);

		am.setVariable(varSlot, value);

		Integer varValue = (Integer) handle.invoke(am);
		Assertions.assertEquals(value, varValue);
	}
}
