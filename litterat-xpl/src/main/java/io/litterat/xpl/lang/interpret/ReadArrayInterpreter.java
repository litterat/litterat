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
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.ReadArray;

public class ReadArrayInterpreter implements ExpressionInterpreter {

	@SuppressWarnings("unused")
	private final ReadArray readArray;

	private final DataClassArray arrayClass;

	private final ExpressionInterpreter readElement;

	public ReadArrayInterpreter(ReadArray readArray, ExpressionInterpreter readElement) {

		this.readArray = readArray;
		this.arrayClass = readArray.dataClassArray();
		this.readElement = readElement;
	}

	@Override
	public Object execute(LitteratMachine m) throws Throwable {

		TypeInputStream in = ((TypeInputStream) m.getVariable(LitteratMachine.VAR_TRANSPORT));
		int length = in.input().readUVarInt32();

		Object arrayData = arrayClass.constructor().invoke(length);
		Object iterator = arrayClass.iterator().invoke(arrayData);

		for (int x = 0; x < length; x++) {
			Object v = readElement.execute(m);
			arrayClass.put().invoke(arrayData, iterator, v);
		}

		return arrayData;

	}

}
