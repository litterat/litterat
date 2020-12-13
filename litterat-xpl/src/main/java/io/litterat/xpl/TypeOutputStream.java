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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import io.litterat.schema.TypeException;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.meta.SchemaTypes;
import io.litterat.schema.types.TypeName;
import io.litterat.xpl.io.ByteArrayBaseOutput;
import io.litterat.xpl.io.ByteBufferBaseOutput;
import io.litterat.xpl.io.StreamBaseOutput;
import io.litterat.xpl.resolve.SchemaResolver;

public class TypeOutputStream implements TypeStream {

	private final TypeBaseOutput output;
	private final TypeMap typeMap;
	private final TypeResolver resolver;

	public TypeOutputStream(TypeMap map, TypeBaseOutput output) {
		this.output = output;
		this.typeMap = map;
		this.resolver = new SchemaResolver(map);

		// Register the base types to communicate schema definitions.
		typeMap.registerMetaData(resolver);
	}

	public TypeOutputStream(TypeBaseOutput output) {
		this(new TypeMap(new TypeLibrary()), output);
	}

	public TypeOutputStream(TypeMap map, byte[] buffer) {
		this(map, new ByteArrayBaseOutput(buffer));
	}

	public TypeOutputStream(byte[] buffer) {
		this(new ByteArrayBaseOutput(buffer));
	}

	public TypeOutputStream(TypeMap map, OutputStream output) {
		this(map, new StreamBaseOutput(output));
	}

	public TypeOutputStream(OutputStream output) {
		this(new StreamBaseOutput(output));
	}

	public TypeOutputStream(TypeMap map, ByteBuffer output) {
		this(map, new ByteBufferBaseOutput(output));
	}

	public TypeOutputStream(ByteBuffer output) {
		this(new ByteBufferBaseOutput(output));
	}

	@Override
	public TypeMap typeMap() {
		return typeMap;
	}

	public TypeBaseOutput output() {
		return output;
	}

	private TypeMapEntry getEntry(TypeName typeName) throws TypeException, IOException {
		TypeMapEntry entry = typeMap.getEntry(typeName);
		if (entry == null) {
			entry = resolver.map(typeName);
			if (entry == null) {
				throw new TypeException("Class not registered or defined in stream: " + typeName.toString());
			}

			// Register in the typeMap. update entry value with registered value.
			entry = typeMap.register(entry.streamId(), entry);

			// Write the definition before returning.
			this.output().writeUVarInt32(DEFINE_TYPE);
			this.writeObject(SchemaTypes.TYPE_NAME_DEFINITION, entry.nameDefinition());
		}
		return typeMap.getEntry(typeName);
	}

	public void writeObject(Object object) throws IOException {
		try {
			TypeMapEntry entry = getEntry(typeMap.library().getTypeName(object.getClass()));
			output().writeUVarInt32(entry.streamId());
			entry.writer().write(this, object);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public void writeObject(TypeName typeName, Object object) throws IOException {
		try {
			TypeMapEntry entry = getEntry(typeName);
			output().writeUVarInt32(entry.streamId());
			entry.writer().write(this, object);
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public int getStreamIdentifier(Class<?> clazz) throws IOException {
		try {
			TypeMapEntry entry = getEntry(typeMap.library().getTypeName(clazz));
			return entry.streamId();
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public int getStreamIdentifier(TypeName typeName) throws IOException {
		try {
			TypeMapEntry entry = getEntry(typeName);
			return entry.streamId();
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public void close() {

	}
}
