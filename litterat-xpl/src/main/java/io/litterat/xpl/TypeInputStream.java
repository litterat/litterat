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

import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.core.meta.Typename;
import io.litterat.xpl.io.ByteArrayBaseInput;
import io.litterat.xpl.io.ByteBufferBaseInput;
import io.litterat.xpl.io.StreamBaseInput;

public class TypeInputStream implements TypeStream {

	private final TypeBaseInput input;
	private final TypeMap typeMap;

	public TypeInputStream(TypeMap typeMap, TypeBaseInput input) {
		this.typeMap = typeMap;
		this.input = input;
	}

	public TypeInputStream(TypeBaseInput input) {
		this(new TypeMap(TypeContext.builder().build()), input);
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

	/**
	 * Reads the next identifier which signifies the next value. If the identifier is a registration
	 * value then the meta data value is read and the type registered.
	 * 
	 * @return
	 * @throws IOException
	 * @throws TypeException
	 */
	private int readNextIdentifier() throws IOException, TypeException {
		int token = input().readUVarInt32();
		while (token == DEFINE_TYPE) {
			TypeStreamEntry def = this.readObject(TypeStreamEntry.class);
			typeMap.registerEntry(def);
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
	public <T> T readObject(Typename typeName) throws IOException {
		try {

			int type = readNextIdentifier();
			TypeMapEntry entry = typeMap.getEntry(type);
			if (entry == null) {
				throw new IOException("type not mapped to stream: " + typeName.toString());
			}
			if (!entry.typename().equals(typeName)) {
				throw new IOException("wrong type on stream: expected " + typeName.toString() + " found: "
						+ entry.typename().toString());
			}
			return (T) entry.reader().read(this);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}
}
