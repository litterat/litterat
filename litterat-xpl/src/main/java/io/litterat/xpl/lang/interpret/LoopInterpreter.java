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

import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.Loop;

public class LoopInterpreter implements StatementInterpreter {

	Loop loop;
	ExpressionInterpreter arrayExpr;
	StatementInterpreter loopStatement;

	public LoopInterpreter(Loop loop, ExpressionInterpreter arrayExpr, StatementInterpreter loopStatement) {
		this.loop = loop;
		this.arrayExpr = arrayExpr;
		this.loopStatement = loopStatement;
	}

	@Override
	public Object execute(LitteratMachine am) throws Throwable {
		Object[] array = (Object[]) arrayExpr.execute(am);

		for (int x = 0; x < array.length; x++) {
			am.setVariable(loop.valSlot(), array[x]);
			loopStatement.execute(am);
		}

		return null;
	}

}
