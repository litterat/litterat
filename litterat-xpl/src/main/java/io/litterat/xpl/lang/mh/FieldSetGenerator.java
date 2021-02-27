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

import io.litterat.bind.PepDataClass;
import io.litterat.bind.PepException;
import io.litterat.model.TypeException;
import io.litterat.model.bind.ModelBinder;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.LitteratMachine;

public class FieldSetGenerator implements StatementGenerator {

	private final FieldSet fieldSet;
	private final ExpressionGenerator objectExpression;
	private final ExpressionGenerator valueExpression;

	public FieldSetGenerator(FieldSet fieldSet, final ExpressionGenerator objectExpression,
			final ExpressionGenerator valueExpression) {
		this.fieldSet = fieldSet;
		this.objectExpression = objectExpression;
		this.valueExpression = valueExpression;
	}

	private MethodHandle resolveFieldSetter(TypeMap typeMap) throws TypeException {

		try {
			// Get the data class
			PepDataClass dataClass = typeMap.library().getTypeClass(fieldSet.type());

			// find the getter.
			return ModelBinder.resolveFieldSetter(dataClass, fieldSet.field());
		} catch (PepException e) {
			throw new TypeException("Failed to get getter", e);
		}
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// (obj, value) -> obj.setField( value );
		MethodHandle fieldSetter = resolveFieldSetter(am.typeMap());

		// (am) -> (X) obj.execute(am);
		MethodHandle obj = objectExpression.bind(am)
				.asType(MethodType.methodType(fieldSetter.type().parameterType(0), LitteratMachine.class));

		// (am) -> val.exectue(am);
		MethodHandle val = valueExpression.bind(am);

		// (am, val) -> ((X) obj.execute(am).setField( val )
		MethodHandle exObj = MethodHandles.collectArguments(fieldSetter, 0, obj);

		// (am1, am2) -> ((X) obj.execute(am1).setField( val.exectue(am2) )
		MethodHandle set = MethodHandles.collectArguments(exObj, 1, val);

		// (am) -> ((X) obj.execute(am)).setField( val.exectue(am) )
		return MethodHandles.permuteArguments(set, MethodType.methodType(void.class, LitteratMachine.class), 0, 0);
	}
}
