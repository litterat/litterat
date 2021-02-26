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

import io.litterat.model.TypeException;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.SlotSet;

public class SlotSetGenerator implements StatementGenerator {

	private final SlotSet slotSet;
	private final ExpressionGenerator expression;

	public SlotSetGenerator(SlotSet slotSet, ExpressionGenerator expression) {
		this.slotSet = slotSet;
		this.expression = expression;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// (am) -> return express.execute(am)
		MethodHandle expr = expression.bind(am);

		// (am, int, Object) -> am.setVariable( int, Object);
		MethodHandle varSetter = MethodHandles.lookup().findVirtual(LitteratMachine.class, "setVariable",
				MethodType.methodType(void.class, new Class[] { int.class, Object.class }));

		// () -> return variable;
		MethodHandle av = MethodHandles.constant(int.class, slotSet.variable());

		// (am, Object) -> am.setVariable( variable, Object );
		MethodHandle setPos = MethodHandles.collectArguments(varSetter, 1, av);

		// (am1, am2) -> am1.setVariable( variable, expression.execute(am2) );
		MethodHandle result = MethodHandles.collectArguments(setPos, 1,
				expr.asType(MethodType.methodType(Object.class, LitteratMachine.class)));

		// (am) -> am.setVariable( variable, expression.execute(am) );
		return MethodHandles.permuteArguments(result, MethodType.methodType(void.class, LitteratMachine.class), 0, 0);
	}
}
