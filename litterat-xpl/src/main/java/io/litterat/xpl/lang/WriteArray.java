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
package io.litterat.xpl.lang;

import io.litterat.bind.annotation.Record;
import io.litterat.bind.DataClassArray;

/**
 * Writes an array
 */

@Record
@io.litterat.bind.annotation.Typename(namespace = "xpl", name = "write_array")
public class WriteArray extends Statement {

	private final DataClassArray dataClassArray;
	private final int valueSlot;
	private final Expression arrayExpression;
	private final Statement writeStatement;

	public WriteArray(DataClassArray dataArray, int valueSlot, Expression arrayExpression, Statement writeStatement)
			throws NoSuchMethodException, IllegalAccessException {
		this.dataClassArray = dataArray;
		this.valueSlot = valueSlot;
		this.arrayExpression = arrayExpression;
		this.writeStatement = writeStatement;
	}

	public DataClassArray dataClassArray() {
		return dataClassArray;
	}

	public int valueSlot() {
		return valueSlot;
	}

	public Expression arrayExpression() {
		return arrayExpression;
	}

	public Statement writeStatement() {
		return writeStatement;
	}

}
