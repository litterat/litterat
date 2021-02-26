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
import io.litterat.xpl.TypeBaseOutput;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.io.ByteArrayBaseInput;
import io.litterat.xpl.io.ByteArrayBaseOutput;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.Value;
import io.litterat.xpl.lang.WriteValue;

public class WriteAtomGeneratorTest {

	@Test
	public void testBind() throws Throwable {

		float value = 1.23f;

		Value valueRef = new Value(float.class, value);
		WriteValue writeNode = new WriteValue(TypeLibrary.FLOAT, valueRef);
		ValueGenerator valueGen = new ValueGenerator(valueRef);
		WriteAtomGenerator writeTypeGen = new WriteAtomGenerator(writeNode, valueGen);

		Class<?>[] slots = new Class[] { TypeMap.class, TypeBaseOutput.class };

		TypeLibrary library = new TypeLibrary();
		TypeMap typeMap = new TypeMap(library);
		LitteratMachine am = new LitteratMachine(typeMap, slots);

		MethodHandle handle = writeTypeGen.bind(am);

		byte[] buffer = new byte[10];
		ByteArrayBaseOutput out = new ByteArrayBaseOutput(buffer);
		am.setVariable(LitteratMachine.VAR_TRANSPORT, out);

		handle.invoke(am);

		ByteArrayBaseInput in = new ByteArrayBaseInput(buffer);
		float varValue = in.readFloat();
		Assertions.assertEquals(value, varValue);
	}
}
