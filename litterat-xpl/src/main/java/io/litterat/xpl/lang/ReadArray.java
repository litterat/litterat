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
import io.litterat.core.meta.Array;

/**
 *
 * This reads a particular atom type from the input stream.
 *
 */

@Record
@io.litterat.bind.annotation.Typename(namespace = "xpl", name = "read_array")
public class ReadArray implements Expression {

	private final DataClassArray dataArray;
	private final Array arrayDef;
	private final Expression readElement;

	public ReadArray(DataClassArray dataArray, Array arrayDef, Expression readElement)
			throws NoSuchMethodException, IllegalAccessException {
		this.dataArray = dataArray;
		this.arrayDef = arrayDef;
		this.readElement = readElement;
	}

	public DataClassArray dataClassArray() {
		return dataArray;
	}

	public Array array() {
		return arrayDef;
	}

	public Expression readExpression() {
		return readElement;
	}

}
