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
package io.litterat.xpl.lang.interpret;

import java.lang.invoke.MethodHandle;

import io.litterat.schema.TypeException;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.ReadValue;
import io.litterat.xpl.resolve.TransportHandles;

public class ReadAtomInterpreter implements ExpressionInterpreter {

	private transient final MethodHandle readType;

	public ReadAtomInterpreter(ReadValue readType) throws NoSuchMethodException, IllegalAccessException, TypeException {
		this.readType = TransportHandles.getInputHandle(readType.type());
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {
		return readType.invoke(((TypeInputStream) am.getVariable(LitteratMachine.VAR_TRANSPORT)).input());
	}

}
