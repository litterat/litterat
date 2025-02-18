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
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import io.litterat.schema.TypeException;
import io.litterat.xpl.TypeBaseInput;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.ReadValue;
import io.litterat.xpl.resolve.TransportHandles;

public class ReadAtomGenerator implements ExpressionGenerator {

	private transient final MethodHandle readType;

	public ReadAtomGenerator(ReadValue readType) throws NoSuchMethodException, IllegalAccessException, TypeException {
		this.readType = TransportHandles.getInputHandle(readType.type());
	}

	@Override
	public MethodHandle bind(LitteratMachine machine)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {

		// (machine, slot) -> am.getVariable( slot )
		MethodHandle varGetter = MethodHandles.lookup().findVirtual(LitteratMachine.class, "getVariable",
				MethodType.methodType(Object.class, int.class));

		// () -> return VAR_TRANSPORT
		MethodHandle av = MethodHandles.constant(int.class, LitteratMachine.VAR_TRANSPORT);

		// (machine) -> (TypeBaseInput) am.getVariable( VAR_TRANSPORT );
		MethodHandle in = MethodHandles.collectArguments(varGetter, 1, av)
				.asType(MethodType.methodType(TypeBaseInput.class, LitteratMachine.class));

		// (machine) -> ((TypeBaseInput) am.getVariable( VAR_TRANSPORT )).readX();
		return MethodHandles.collectArguments(readType, 0, in);
	}
}
