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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.litterat.model.TypeName;
import io.litterat.model.library.TypeException;
import io.litterat.model.library.TypeLibrary;
import io.litterat.model.library.TypeNameDefinition;
import io.litterat.model.meta.SchemaTypes;
import io.litterat.xpl.io.ByteArrayBaseInput;
import io.litterat.xpl.io.ByteBufferBaseInput;
import io.litterat.xpl.io.StreamBaseInput;
import io.litterat.xpl.resolve.SchemaResolver;

public class TypeInputStream implements TypeStream {

	private final TypeBaseInput input;
	private final TypeMap typeMap;
	private final TypeResolver resolver;

	public TypeInputStream(TypeMap typeMap, TypeBaseInput input) {
		this.typeMap = typeMap;
		this.input = input;
		this.resolver = new SchemaResolver(typeMap);

		// Register the base types to communicate schema definitions.
		typeMap.registerMetaData(resolver);
	}

	public TypeInputStream(TypeBaseInput input) {
		this(new TypeMap(new TypeLibrary()), input);
	}

	public TypeInputStream(byte[] buffer) throws TypeException {
		this(new ByteArrayBaseInput(buffer));
	}

	public TypeInputStream(TypeMap typeMap, byte[] buffer) {
		this(typeMap, new ByteArrayBaseInput(buffer));
	}

	public TypeInputStream(InputStream input) throws TypeException {
		this(new StreamBaseInput(input));
	}

	public TypeInputStream(TypeMap typeMap, InputStream input) {
		this(typeMap, new StreamBaseInput(input));
	}

	public TypeInputStream(ByteBuffer buffer) throws TypeException {
		this(new ByteBufferBaseInput(buffer));
	}

	public TypeInputStream(TypeMap typeMap, ByteBuffer buffer) {
		this(typeMap, new ByteBufferBaseInput(buffer));
	}

	@Override
	public TypeMap typeMap() {
		return typeMap;
	}

	public TypeBaseInput input() {
		return input;
	}

	private void readAndRegister() throws IOException, TypeException {

		TypeNameDefinition def = this.readObject(SchemaTypes.TYPE_NAME_DEFINITION);
		TypeMapEntry entry = typeMap.getEntry(def.streamId());
		if (entry == null) {
			// TODO Check if the definitions match.

			// Generate the reader/writer.
			entry = resolver.map(def.typeName());

			typeMap.register(def.streamId(), entry);
		} else {
			if (!def.typeName().equals(entry.typeName())) {
				throw new IOException("No match for type on stream");
			}
		}

	}

	private int readNextIdentifier() throws IOException, TypeException {
		int token = input().readUVarInt32();
		while (token == DEFINE_TYPE) {
			readAndRegister();
			token = input().readUVarInt32();
		}
		return token;
	}

	/**
	 *
	 * Reads the first uvarint32 as the identifier in this context and then reads the value.
	 *
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public <T> T readObject() throws IOException {
		try {
			int type = readNextIdentifier();
			// Read the value.
			TypeMapEntry entry = typeMap.getEntry(type);
			if (entry == null) {
				throw new IOException("type not known in stream: " + type);
			}
			return (T) entry.reader().read(this);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	/**
	 * Reads the stream using the given class reader.
	 *
	 * @param <T>
	 * @param clss
	 * @return
	 * @throws IOException
	 */

	@SuppressWarnings("unchecked")
	public <T> T readObject(Class<? extends T> clss) throws IOException {

		try {
			int type = readNextIdentifier();
			TypeMapEntry entry = typeMap.getEntry(type);
			TypeMapEntry clssEntry = typeMap.getEntry(clss);
			if (entry != clssEntry) {
				throw new IOException("wrong type on stream");
			}
			return (T) entry.reader().read(this);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T readObject(TypeName typeName) throws IOException {
		try {

			int type = readNextIdentifier();
			TypeMapEntry entry = typeMap.getEntry(type);
			if (entry == null) {
				throw new IOException("type not mapped to stream: " + typeName.toString());
			}
			if (!entry.typeName().equals(typeName)) {
				throw new IOException("wrong type on stream: expected " + typeName.toString() + " found: "
						+ entry.typeName().toString());
			}
			return (T) entry.reader().read(this);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}
}
