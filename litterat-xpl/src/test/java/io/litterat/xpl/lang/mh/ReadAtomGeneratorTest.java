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

import io.litterat.core.TypeContext;
import io.litterat.core.meta.Meta;
import io.litterat.xpl.TypeBaseInput;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.io.ByteArrayBaseInput;
import io.litterat.xpl.io.ByteArrayBaseOutput;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.ReadValue;

public class ReadAtomGeneratorTest {

	@Test
	public void testBind() throws Throwable {

		float value = 1.23f;
		// int varSlot = 1;

		ReadValue readNode = new ReadValue(Meta.FLOAT);
		ReadAtomGenerator readGen = new ReadAtomGenerator(readNode);

		Class<?>[] slots = new Class[] { TypeMap.class, TypeBaseInput.class };

		TypeContext context = TypeContext.builder().build();
		TypeMap typeMap = new TypeMap(context);
		LitteratMachine am = new LitteratMachine(typeMap, slots);
		MethodHandle handle = readGen.bind(am);

		byte[] buffer = new byte[10];
		ByteArrayBaseOutput out = new ByteArrayBaseOutput(buffer);
		out.writeFloat(value);

		ByteArrayBaseInput in = new ByteArrayBaseInput(buffer);
		am.setVariable(LitteratMachine.VAR_TRANSPORT, in);

		float varValue = (float) handle.invoke(am);
		Assertions.assertEquals(value, varValue);
	}
}
