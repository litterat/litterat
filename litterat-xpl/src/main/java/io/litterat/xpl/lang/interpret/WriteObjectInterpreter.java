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

import java.io.IOException;

import io.litterat.schema.types.TypeName;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.WriteValue;

public class WriteObjectInterpreter implements StatementInterpreter {

	private final ExpressionInterpreter expression;
	private final TypeName typeName;

	public WriteObjectInterpreter(WriteValue writeType, ExpressionInterpreter expression) {
		this.expression = expression;
		this.typeName = writeType.type();
	}

	@Override
	public Object execute(LitteratMachine m) throws Throwable {

		try {

			((TypeOutputStream) m.getVariable(LitteratMachine.VAR_TRANSPORT)).writeObject(typeName,
					expression.execute(m));
		} catch (Throwable e) {
			throw new IOException(e);
		}

		return null;
	}
}
