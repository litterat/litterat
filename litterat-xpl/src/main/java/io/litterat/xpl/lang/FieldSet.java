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
import io.litterat.annotation.Record;
import io.litterat.schema.meta.Typename;

/**
 * This is a specialization of the form out.writeX( o.getY() ); where X is the type being written
 * and Y is the field being read from an object. This only applies to primitive types. The getter
 * method handle can either be through a field or method. The writer handle must match the returned
 * type of the getter.
 */

@Record
@io.litterat.annotation.Typename(namespace = "xpl", name = "field_set")
public class FieldSet extends Statement {

	private final Expression objectExpression;
	private final Typename type;
	private final String field;
	private final Expression valueExpression;

	// private MethodHandle writerHandle;

	public FieldSet(Expression objectExpression, Expression valueExpression, Typename type, String field)
			throws TypeException {
		this.objectExpression = objectExpression;
		this.type = type;
		this.field = field;
		this.valueExpression = valueExpression;
	}

	public Expression objectExpression() {
		return objectExpression;
	}

	public Typename type() {
		return type;
	}

	public String field() {
		return field;
	}

	public Expression valueExpression() {
		return valueExpression;
	}

}
