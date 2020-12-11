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

import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepException;
import io.litterat.schema.TypeException;
import io.litterat.schema.bind.PepSchemaBinder;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.FieldRead;
import io.litterat.xpl.lang.LitteratMachine;

public class FieldReadGenerator implements ExpressionGenerator {

	private final FieldRead fieldRead;
	private final ExpressionGenerator expression;

	public FieldReadGenerator(FieldRead fileRead, final ExpressionGenerator expression) {
		this.expression = expression;
		this.fieldRead = fileRead;
	}

	private MethodHandle resolveFieldGetter(TypeMap typeMap) throws TypeException {

		try {
			// Get the data class
			PepDataClass dataClass = typeMap.library().getTypeClass(fieldRead.type());

			// find the getter.
			return PepSchemaBinder.resolveFieldGetter(dataClass, fieldRead.field());
		} catch (PepException e) {
			throw new TypeException("Failed to get getter", e);
		}
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// (obj) -> obj.getField();
		MethodHandle fieldGetter = resolveFieldGetter(am.typeMap());

		// (am) -> ( (X) expression.execute(am) ).getField()
		return MethodHandles.collectArguments(fieldGetter, 0, expression.bind(am)
				.asType(MethodType.methodType(fieldGetter.type().parameterType(0), LitteratMachine.class)));
	}
}
