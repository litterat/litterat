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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Entry;
import io.litterat.schema.meta.Typename;
import io.litterat.xpl.io.ByteArrayBaseOutput;
import io.litterat.xpl.io.ByteBufferBaseOutput;
import io.litterat.xpl.io.StreamBaseOutput;

public class TypeOutputStream implements TypeStream {

	private final TypeBaseOutput output;
	private final TypeMap typeMap;

	public TypeOutputStream(TypeMap map, TypeBaseOutput output) {
		this.output = output;
		this.typeMap = map;
	}

	public TypeOutputStream(TypeBaseOutput output) {
		this(new TypeMap(TypeContext.builder().build()), output);
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

	private TypeMapEntry register(Typename typeName) throws TypeException, IOException {
		TypeMapEntry entry = typeMap.registerStreamEntry(typeName);

		// Write the definition before returning.
		logOutput("writing TypeStreamEntry " + entry.streamId() + " " + entry.typename());
		this.output().writeUVarInt32(DEFINE_TYPE);
		this.writeObject(
				new TypeStreamEntry(entry.streamId(), new Entry(entry.typename(), entry.definition())));
		logOutput("finished writing TypeStreamEntry " + entry.streamId() + " " + entry.typename() );
		return typeMap.getEntry(typeName);
	}

	public void writeObject(Object object) throws IOException {
		try {
			Objects.requireNonNull(object, "writeObject(Object) requires non null value");

			TypeMapEntry entry = typeMap.getEntry(object.getClass());
			if (entry == null) {
				entry = register(typeMap.context().getTypename(object.getClass()));
			}

			logOutput("writing " + object.getClass() + " streamId " + entry.streamId());
			output().writeUVarInt32(entry.streamId());

			logOutput( "writing object");
			entry.writer().write(this, object);

		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public void writeObject(Typename typename, Object object) throws IOException {
		try {
			Objects.requireNonNull(typename, "writeObject(Typename, Object) requires non null value");

			TypeMapEntry entry = typeMap.getEntry(typename);
			if (entry == null) {
				entry = register(typename);
			}

			logOutput("writing " + typename + " streamId " + entry.streamId());
			output().writeUVarInt32(entry.streamId());

			logOutput( "writing object");
			entry.writer().write(this, object);

		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public void logOutput(String msg) {
		if (output instanceof ByteArrayBaseOutput out) {
			System.err.println("Output pos: " + out.getBytesWritten() + " " + msg);
		}
	}

	public int getStreamIdentifier(Class<?> clazz) throws IOException {
		try {
			TypeMapEntry entry = typeMap.getEntry(clazz);
			if (entry == null) {
				entry = register(typeMap.context().getTypename(clazz));
			}

			return entry.streamId();
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public int getStreamIdentifier(Typename typename) throws IOException {
		try {
			TypeMapEntry entry = typeMap.getEntry(typename);

			if (entry == null) {
				entry = register(typename);
			}

			return entry.streamId();
		} catch (Throwable e) {
			throw new IOException(e);
		}
	}

	public void close() {

	}
}
