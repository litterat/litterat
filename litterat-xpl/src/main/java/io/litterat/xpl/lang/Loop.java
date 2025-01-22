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

import io.litterat.schema.TypeException;
import io.litterat.bind.annotation.Record;

/**
 * Not particularly happy with this loop statement. Should revisit when adding things like a binary
 * boolean operator.
 */

@Record
@io.litterat.bind.annotation.Typename(namespace = "xpl", name = "loop")
public class Loop extends Statement {

	private final int valSlot;
	private final Expression arrayExpression;
	private final Statement loopStatement;

	// private MethodHandle writerHandle;

	public Loop(int valSlot, Expression arrayExpression, Statement loopStatement) throws TypeException {
		this.valSlot = valSlot;
		this.arrayExpression = arrayExpression;
		this.loopStatement = loopStatement;
	}

	public int valSlot() {
		return valSlot;
	}

	public Expression arrayExpression() {
		return arrayExpression;
	}

	public Statement loopStatement() {
		return loopStatement;
	}

}
