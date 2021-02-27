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
package io.litterat.model;

import io.litterat.bind.Data;
import io.litterat.model.annotation.SchemaType;

/**
 * This is used as part of creating an entry in a TypeMap and part of an encoding to map an
 * identifier to type and definition. This probably needs to be moved somewhere else.
 *
 */

@SchemaType(namespace = "schema", name = "type_name_definition")
public class TypeNameDefinition {
	private final int streamId;
	private final TypeName typeName;
	private final Definition definition;

	@Data
	public TypeNameDefinition(int streamId, TypeName typeName, Definition definition) {
		this.streamId = streamId;
		this.typeName = typeName;
		this.definition = definition;
	}

	public int streamId() {
		return streamId;
	}

	public TypeName typeName() {
		return typeName;
	}

	public Definition definition() {
		return definition;
	}
}
