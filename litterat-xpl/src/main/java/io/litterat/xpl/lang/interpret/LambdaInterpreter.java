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

import io.litterat.model.TypeException;
import io.litterat.xpl.TypeStream;
import io.litterat.xpl.lang.Lambda;
import io.litterat.xpl.lang.LambdaFunction;
import io.litterat.xpl.lang.LitteratMachine;

public class LambdaInterpreter implements LambdaFunction {

	private final Lambda lambda;
	private final BlockInterpreter expression;

	public LambdaInterpreter(Lambda lambda, BlockInterpreter expression) {
		this.lambda = lambda;
		this.expression = expression;
	}

	@Override
	public Object execute(Object... args) throws TypeException {
		try {
			if (args.length != lambda.signature().arguments().length) {
				throw new TypeException(String.format("incorrect arguments. Expected %s. Received %s ",
						lambda.signature().arguments().length, args.length));
			}
			LitteratMachine machine = new LitteratMachine(((TypeStream) args[0]).typeMap(), this.lambda.slots());

			// Set the arguments into variable slots in the machine.
			for (int x = 0; x < args.length; x++) {
				machine.setVariable(x, args[x]);
			}

			return this.expression.execute(machine);

		} catch (Throwable e) {
			throw new TypeException("Failed to execute lambda", e);
		}
	}

}
