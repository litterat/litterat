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
package io.litterat.xpl;

import io.litterat.bind.DataClass;
import io.litterat.core.meta.Definition;
import io.litterat.core.meta.Typename;

public class TypeMapEntry {

	private final int streamId;
	private final Typename typename;
	private final Definition definition;
	private final DataClass dataClass;
	private final TypeReader reader;
	private final TypeWriter writer;

	public TypeMapEntry(int streamId, Typename typename, Definition definition, DataClass dataClass, TypeReader reader, TypeWriter writer) {
		this.streamId = streamId;
		this.dataClass = dataClass;
		this.typename = typename;
		this.definition = definition;
		this.reader = reader;
		this.writer = writer;
	}

	public TypeMapEntry(int streamId, TypeMapEntry entry) {
		this(streamId, entry.typename(),entry.definition, entry.dataClass(), entry.reader(), entry.writer());
	}

	public int streamId() {
		return streamId;
	}

	public DataClass dataClass() {
		return dataClass;
	}

	public Typename typename() {
		return typename;
	}

	public Definition definition() {
		return definition;
	}

	public TypeReader reader() {
		return reader;
	}

	public TypeWriter writer() {
		return writer;
	}

}