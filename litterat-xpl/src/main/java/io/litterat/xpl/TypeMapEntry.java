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
package io.litterat.xpl;

import io.litterat.model.Definition;
import io.litterat.model.TypeName;
import io.litterat.model.TypeNameDefinition;

public class TypeMapEntry {

	private final TypeNameDefinition nameDefinition;
	private final TypeReader reader;
	private final TypeWriter writer;

	public TypeMapEntry(int streamId, TypeName typeName, Definition definition, TypeReader reader, TypeWriter writer) {
		this.nameDefinition = new TypeNameDefinition(streamId, typeName, definition);
		this.reader = reader;
		this.writer = writer;
	}

	public TypeMapEntry(int streamId, TypeMapEntry entry) {
		this(streamId, entry.typeName(), entry.definition(), entry.reader(), entry.writer());
	}

	public int streamId() {
		return nameDefinition.streamId();
	}

	public TypeName typeName() {
		return nameDefinition.typeName();
	}

	public Definition definition() {
		return nameDefinition.definition();
	}

	public TypeNameDefinition nameDefinition() {
		return nameDefinition;
	}

	public TypeReader reader() {
		return reader;
	}

	public TypeWriter writer() {
		return writer;
	}

}