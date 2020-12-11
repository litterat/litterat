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
import io.litterat.pep.PepException;
import io.litterat.schema.TypeException;
import io.litterat.schema.bind.PepSchemaBinder;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.LitteratMachine;

public class FieldSetInterpreter implements StatementInterpreter {

	private final FieldSet fieldSet;
	private final ExpressionInterpreter objectExpression;
	private final ExpressionInterpreter valueExpression;
	private final MethodHandle fieldSetter;

	public FieldSetInterpreter(TypeMap typeMap, FieldSet fieldSet, final ExpressionInterpreter objectExpression,
			final ExpressionInterpreter valueExpression) throws PepException, TypeException {
		this.fieldSet = fieldSet;
		this.objectExpression = objectExpression;
		this.valueExpression = valueExpression;
		this.fieldSetter = resolveFieldSetter(typeMap);
	}

	private MethodHandle resolveFieldSetter(TypeMap typeMap) throws PepException, TypeException {

		// Get the data class
		PepDataClass dataClass = typeMap.library().getTypeClass(fieldSet.type());

		// find the getter.
		return PepSchemaBinder.resolveFieldSetter(dataClass, fieldSet.field());
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {

		fieldSetter.invoke(objectExpression.execute(am), valueExpression.execute(am));
		return null;
	}
}
