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

/**
 *
 * The base input interfaces specifies the underlying way of reading data from
 * the underlying transport (stream,byte[],ByteBuffer). You should have no need
 * to interact with this interface and is here for compatibility with older
 * style serialization libraries. Check the @TypeReaderWriter for further
 * information.
 *
 */
public interface TypeBaseInput {

	default public boolean readBoolean() throws IOException {
		return readInt8() == 0 ? false : true;
	}

	public byte readInt8() throws IOException;

	public short readUInt8() throws IOException;

	public short readInt16() throws IOException;

	public int readUInt16() throws IOException;

	default public short readLeInt16() throws IOException {
		return Short.reverseBytes(readInt16());
	}

	default public int readLeUInt16() throws IOException {
		return Integer.reverseBytes(readUInt16());
	}

	public int readInt32() throws IOException;

	public long readUInt32() throws IOException;

	default public int readLeInt32() throws IOException {
		return Integer.reverseBytes(readInt32());
	}

	default public long readLeUInt32() throws IOException {
		return Long.reverseBytes(readUInt32());
	}

	public long readInt64() throws IOException;

	public long readUInt64() throws IOException;

	default public long readLeInt64() throws IOException {
		return Long.reverseBytes(readInt64());
	}

	// TODO This needs to be updated to BigInteger.
	default public long readLeUInt64() throws IOException {
		return Long.reverseBytes(readUInt64());
	}

	// Variable length signed integer using zig-zag encoding.
	default public int readVarInt32() throws IOException {
		int i = readUVarInt32();
		return (i >>> 1) ^ -(i & 1);
	}

	default public long readVarInt64() throws IOException {
		long i = readUVarInt64();
		return (i >>> 1) ^ -(i & 1);
	}

	public int readUVarInt32() throws IOException;

	public long readUVarInt64() throws IOException;

	default public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt32());
	}

	default public double readDouble() throws IOException {
		return Double.longBitsToDouble(readInt64());
	}

	public void readBytes(byte[] buffer, int offset, int length) throws IOException;

}
