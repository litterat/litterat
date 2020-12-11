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
package io.litterat.schema.types;

import io.litterat.pep.Data;
import io.litterat.schema.annotation.SchemaType;

@Data
@SchemaType(namespace = "schema", name = "signature")
public class Signature {

	private final Reference[] arguments;
	private final Reference returnType;

	public Signature(Reference returnType, Reference... arguments) {
		this.arguments = arguments;
		this.returnType = returnType;
	}

	public Reference[] arguments() {
		return arguments;
	}

	public Reference returnType() {
		return returnType;
	}
}
