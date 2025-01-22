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
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassRecord;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.ConstructInstance;
import io.litterat.xpl.lang.LitteratMachine;

public class ConstructInstanceGenerator implements ExpressionGenerator {

	private final ConstructInstance createInstance;
	private final ExpressionGenerator[] params;
	private final MethodHandle constructor;

	public ConstructInstanceGenerator(final TypeMap typeMap, final ConstructInstance createInstance,
			ExpressionGenerator[] params) throws TypeException {
		this.createInstance = createInstance;
		this.params = params;

		DataClass dataClass = typeMap.context().getDescriptor(createInstance.type());
		if (dataClass instanceof DataClassRecord) {
			this.constructor = ((DataClassRecord) dataClass).constructor();
		} else {
			throw new TypeException("Type not a record type");
		}

	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		for (int x = 0; x < params.length; x++) {
			@SuppressWarnings("unused")
			MethodHandle arg = params[x].bind(am);
		}

		// (am) -> return new X();
		return MethodHandles.permuteArguments(constructor,
				MethodType.methodType(constructor.type().returnType(), LitteratMachine.class));
	}

	public ConstructInstance createInstance() {
		return createInstance;
	}

}
