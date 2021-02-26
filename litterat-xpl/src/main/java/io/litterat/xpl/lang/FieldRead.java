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
package io.litterat.xpl.lang;

import io.litterat.bind.Data;
import io.litterat.model.TypeException;
import io.litterat.model.annotation.SchemaType;
import io.litterat.model.types.TypeName;

/**
 * This reads a getter from an object.
 */

@Data
@SchemaType(namespace = "xpl.lang", name = "field_read")
public class FieldRead implements Expression {

	private final Expression expressionNode;
	private final TypeName type;
	private final String field;

	public FieldRead(Expression expressionNode, TypeName type, String field) throws TypeException {

		this.expressionNode = expressionNode;
		this.type = type;
		this.field = field;
	}

	public Expression expression() {
		return expressionNode;
	}

	public TypeName type() {
		return type;
	}

	public String field() {
		return field;
	}

}
