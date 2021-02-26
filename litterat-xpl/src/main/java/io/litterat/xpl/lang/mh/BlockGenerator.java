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
import io.litterat.xpl.lang.ReturnNode;

public class BlockGenerator implements StatementGenerator {

	private final StatementGenerator[] statements;

	public BlockGenerator(StatementGenerator[] statements) {
		this.statements = statements;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// All statements must have the take a single param LitteratMachine.class and
		// optionally return a value. Currently it is assumed the return value is at the
		// end of the block. Any statements that are not ReturnNode that return a
		// value are ignored.
		MethodHandle statement = statements[0].bind(am);
		for (int x = 1; x < statements.length; x++) {

			MethodHandle h = statements[x].bind(am);

			if (h.type().returnType() != void.class && !(statements[x] instanceof ReturnNode)) {
				h = h.asType(MethodType.methodType(void.class, LitteratMachine.class));
			}

			statement = MethodHandles.foldArguments(h, statement);
		}

		return statement;
	}
}
