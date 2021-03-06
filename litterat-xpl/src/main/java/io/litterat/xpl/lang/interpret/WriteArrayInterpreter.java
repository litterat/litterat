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

import io.litterat.bind.DataClassArray;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.WriteArray;

public class WriteArrayInterpreter implements StatementInterpreter {

	private final WriteArray writeArray;
	private final ExpressionInterpreter arrayExpr;
	private final StatementInterpreter writeElement;

	public WriteArrayInterpreter(WriteArray writeArray, ExpressionInterpreter arrayExpr,
			StatementInterpreter writeElement) {

		this.writeArray = writeArray;
		this.arrayExpr = arrayExpr;
		this.writeElement = writeElement;
	}

	@Override
	public Object execute(LitteratMachine m) throws Throwable {

		DataClassArray arrayClass = writeArray.dataClassArray();

		Object arrayData = arrayExpr.execute(m);
		int length = (int) arrayClass.size().invoke(arrayData);
		Object iterator = arrayClass.iterator().invoke(arrayData);

		((TypeOutputStream) m.getVariable(LitteratMachine.VAR_TRANSPORT)).output().writeUVarInt32(length);
		for (int x = 0; x < length; x++) {
			Object av = arrayClass.get().invoke(arrayData, iterator);
			m.setVariable(writeArray.valueSlot(), av);
			writeElement.execute(m);
		}

		return null;

	}

}
