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

import io.litterat.model.library.TypeException;
import io.litterat.xpl.TypeBaseOutput;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.WriteValue;
import io.litterat.xpl.resolve.TransportHandles;

public class WriteAtomGenerator implements StatementGenerator {

	private final MethodHandle writeType;
	private final ExpressionGenerator expression;

	public WriteAtomGenerator(WriteValue writeType, ExpressionGenerator expression)
			throws NoSuchMethodException, IllegalAccessException, TypeException {
		this.writeType = TransportHandles.getOutputHandle(writeType.type());
		this.expression = expression;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// am.getVariable( int.class );
		MethodHandle varGetter = MethodHandles.lookup().findVirtual(LitteratMachine.class, "getVariable",
				MethodType.methodType(Object.class, int.class));

		// constant VAR_TRANSPORT
		MethodHandle av = MethodHandles.constant(int.class, LitteratMachine.VAR_TRANSPORT);

		// (TypeBaseOutput) (am (LitteratMachine)).getVariable( VAR_TRANSPORT );
		MethodHandle out = MethodHandles.collectArguments(varGetter, 1, av)
				.asType(MethodType.methodType(TypeBaseOutput.class, LitteratMachine.class));

		// output.writeX( expressionNode.execute(am) );
		MethodHandle expr = MethodHandles.collectArguments(writeType, 1, expression.bind(am));

		// (am,am) -> (am (LitteratMachine)).getVariable( VAR_TRANSPORT ).writeX(
		// expressionNode.execute( am );
		MethodHandle write = MethodHandles.collectArguments(expr, 0, out);

		// (am) -> (am (LitteratMachine)).getVariable( VAR_TRANSPORT ).writeX(
		// expressionNode.execute( am );
		MethodHandle result = MethodHandles.permuteArguments(write,
				MethodType.methodType(void.class, LitteratMachine.class), 0, 0);

		return result;
	}
}
