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
package io.litterat.xpl.resolve;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Typename;
import io.litterat.xpl.TypeBaseInput;
import io.litterat.xpl.TypeBaseOutput;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.TypeReader;
import io.litterat.xpl.TypeWriter;

public class TransportHandles {

	private static final Lookup lookup = MethodHandles.lookup();

	private static final Map<Typename, MethodHandle> readHandles = new HashMap<>();
	private static final Map<Typename, MethodHandle> writeHandles = new HashMap<>();

	static {

		try {
			readHandles.put(Meta.FLOAT,
					lookup.findVirtual(TypeBaseInput.class, "readFloat", MethodType.methodType(float.class)));

			readHandles.put(Meta.BOOLEAN,
					lookup.findVirtual(TypeBaseInput.class, "readBoolean", MethodType.methodType(boolean.class)));

			readHandles.put(Meta.INT32,
					lookup.findVirtual(TypeBaseInput.class, "readInt32", MethodType.methodType(int.class)));

			writeHandles.put(Meta.FLOAT, lookup.findVirtual(TypeBaseOutput.class, "writeFloat",
					MethodType.methodType(void.class, float.class)));

			writeHandles.put(Meta.BOOLEAN, lookup.findVirtual(TypeBaseOutput.class, "writeBoolean",
					MethodType.methodType(void.class, boolean.class)));

			writeHandles.put(Meta.INT32, lookup.findVirtual(TypeBaseOutput.class, "writeInt32",
					MethodType.methodType(void.class, int.class)));

		} catch (NoSuchMethodException | IllegalAccessException e) {
			// not expecting an exception here.
			e.printStackTrace();
		}

	}

	public static MethodHandle getInputHandle(Typename typeName)
			throws TypeException {
		MethodHandle handle = readHandles.get(typeName);
		if (handle == null) {
			throw new TypeException("No input handle for type: " + typeName.toString());
		}
		return handle;
	}

	public static MethodHandle getOutputHandle(Typename typeName)
			throws TypeException {

		MethodHandle handle = writeHandles.get(typeName);
		if (handle == null) {
			throw new TypeException("No output handle for type: " + typeName.toString());
		}
		return handle;
	}

	public static TypeReader getReader(Typename typeName) throws TypeException {
		return new TransportTypeReader(getInputHandle(typeName));
	}

	public static class TransportTypeReader implements TypeReader {

		private final MethodHandle mh;

		public TransportTypeReader(MethodHandle mh) {
			this.mh = mh;
		}

		@Override
		public Object read(TypeInputStream reader) throws IOException {

			try {
				return mh.invoke(reader);
			} catch (Throwable e) {
				throw new IOException("Failed to read", e);
			}
		}

	}

	public static TypeWriter getWriter(Typename typeName) throws TypeException {
		return new TransportTypeWriter(getOutputHandle(typeName));
	}

	public static class TransportTypeWriter implements TypeWriter {

		private final MethodHandle mh;

		public TransportTypeWriter(MethodHandle mh) {
			Objects.requireNonNull(mh);
			this.mh = mh;
		}

		@Override
		public void write(TypeOutputStream writer, Object o) throws IOException {
			try {
				mh.invoke(writer.output(), o);
			} catch (Throwable e) {
				throw new IOException("Failed to read", e);
			}

		}

	}
}
