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
package io.litterat.model.function;

import io.litterat.bind.Record;
import io.litterat.model.TypeName;
import io.litterat.model.annotation.SchemaType;

@Record
@SchemaType(namespace = "schema", name = "signature")
public class FunctionSignature {

	private final TypeName[] arguments;
	private final TypeName returnType;

	public FunctionSignature(TypeName returnType, TypeName... arguments) {
		this.arguments = arguments;
		this.returnType = returnType;
	}

	public TypeName[] arguments() {
		return arguments;
	}

	public TypeName returnType() {
		return returnType;
	}
}
