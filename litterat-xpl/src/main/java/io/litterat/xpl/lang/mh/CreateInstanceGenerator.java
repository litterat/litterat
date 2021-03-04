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
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassRecord;
import io.litterat.model.TypeException;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.CreateInstance;
import io.litterat.xpl.lang.LitteratMachine;

public class CreateInstanceGenerator implements ExpressionGenerator {

	private final CreateInstance createInstance;
	private final MethodHandle constructor;

	public CreateInstanceGenerator(final TypeMap typeMap, final CreateInstance createInstance) throws TypeException {
		this.createInstance = createInstance;

		DataClass dataClass = typeMap.library().getTypeClass(createInstance.type());
		if (dataClass instanceof DataClassRecord) {
			this.constructor = ((DataClassRecord) dataClass).creator().orElseThrow();
		} else {
			throw new TypeException("Type not a record type");
		}

	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// (am) -> return new X();
		return MethodHandles.permuteArguments(constructor,
				MethodType.methodType(constructor.type().returnType(), LitteratMachine.class));
	}

	public CreateInstance createInstance() {
		return createInstance;
	}

}
