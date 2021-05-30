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
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.ReturnNode;

public class ReturnGenerator implements StatementGenerator {

	@SuppressWarnings("unused")
	private ReturnNode retrn;
	private ExpressionGenerator expression;

	public ReturnGenerator(ReturnNode retrn, ExpressionGenerator expression) {
		this.retrn = retrn;
		this.expression = expression;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, TypeException {

		// (am) -> expression.execute(am);
		return expression.bind(am);
	}

}
