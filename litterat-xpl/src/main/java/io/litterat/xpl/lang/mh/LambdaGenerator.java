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

import io.litterat.model.library.TypeException;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.Lambda;
import io.litterat.xpl.lang.LambdaFunction;
import io.litterat.xpl.lang.LitteratMachine;

public class LambdaGenerator implements GeneratorNode, LambdaFunction {

	private final Lambda lambda;
	private final BlockGenerator block;
	private MethodHandle compiled;

	public LambdaGenerator(Lambda lambda, BlockGenerator block) {
		this.lambda = lambda;
		this.block = block;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {
		return block.bind(am);
	}

	public void compile(TypeMap typeMap) throws TypeException {

		try {
			LitteratMachine machine = new LitteratMachine(typeMap, this.lambda.slots());
			this.compiled = bind(machine);
		} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | TypeException e) {
			throw new TypeException(e);
		}
	}

	@Override
	public Object execute(Object... args) throws TypeException {
		try {
			if (args.length != lambda.signature().arguments().length) {
				throw new TypeException(String.format("incorrect arguments. Expected %s. Received %s ",
						lambda.signature().arguments().length, args.length));
			}
			LitteratMachine machine = new LitteratMachine((TypeMap) args[0], this.lambda.slots());

			// Set the arguments into variable slots in the machine.
			for (int x = 0; x < args.length; x++) {
				machine.setVariable(x, args[x]);
			}

			return this.compiled.invoke(machine);

		} catch (Throwable e) {
			throw new TypeException("Failed to execute lambda", e);
		}
	}

}
