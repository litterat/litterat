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

import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassRecord;
import io.litterat.schema.TypeException;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.CreateInstance;
import io.litterat.xpl.lang.LitteratMachine;

import java.lang.invoke.MethodHandle;

public class CreateInstanceInterpreter implements ExpressionInterpreter {

	private final CreateInstance createInstance;
	private final MethodHandle constructor;

	public CreateInstanceInterpreter(final TypeMap typeMap, final CreateInstance createInstance) throws TypeException {
		this.createInstance = createInstance;

		DataClass dataClass = typeMap.context().getDescriptor(createInstance.type());
		if (dataClass instanceof DataClassRecord) {
			this.constructor = ((DataClassRecord) dataClass).creator();
		} else {
			throw new TypeException("Type not a record type");
		}
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {

		return constructor.invoke();
	}

	public CreateInstance createInstance() {
		return createInstance;
	}
}
