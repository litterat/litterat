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
package io.litterat.xpl.lang.interpret;

import java.lang.invoke.MethodHandle;

import io.litterat.pep.PepDataClass;
import io.litterat.schema.TypeException;
import io.litterat.schema.bind.PepSchemaBinder;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.ConstructInstance;
import io.litterat.xpl.lang.LitteratMachine;

public class ConstructInstanceInterpreter implements ExpressionInterpreter {

	private final ConstructInstance createInstance;
	private final ExpressionInterpreter[] params;

	private final MethodHandle[] toObject;
	private final MethodHandle constructor;

	public ConstructInstanceInterpreter(TypeMap typeMap, final ConstructInstance createInstance,
			ExpressionInterpreter[] params) throws TypeException {
		this.createInstance = createInstance;
		this.params = params;

		PepDataClass typeClass = typeMap.library().getTypeClass(createInstance.type());
		this.constructor = typeClass.constructor();
		this.toObject = PepSchemaBinder.collectToObject(typeClass);
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {

		Object[] args = new Object[params.length];
		for (int x = 0; x < params.length; x++) {
			args[x] = toObject[x].invoke(params[x].execute(am));
		}

		return constructor.invoke(args);
	}

	public ConstructInstance createInstance() {
		return createInstance;
	}
}
