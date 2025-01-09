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

import io.litterat.core.TypeException;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClassRecord;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.FieldRead;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.resolve.ModelHelper;

public class FieldReadInterpreter implements ExpressionInterpreter {

	private final FieldRead fieldRead;
	private final ExpressionInterpreter expression;
	private final MethodHandle fieldGetter;

	public FieldReadInterpreter(TypeMap typeMap, FieldRead fieldRead, final ExpressionInterpreter expression)
			throws DataBindException, TypeException {
		this.expression = expression;
		this.fieldRead = fieldRead;
		this.fieldGetter = resolveFieldGetter(typeMap);
	}

	private MethodHandle resolveFieldGetter(TypeMap typeMap) throws TypeException {

		// Get the class
		DataClassRecord dataClass = (DataClassRecord) typeMap.context().getDescriptor(fieldRead.type());

		// find the getter.
		return ModelHelper.resolveFieldGetter(dataClass, fieldRead.field());
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {

		// (obj) -> obj.getField();
		return fieldGetter.invoke(expression.execute(am));
	}
}
