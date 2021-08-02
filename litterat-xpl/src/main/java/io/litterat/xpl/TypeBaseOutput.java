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
import java.math.BigInteger;

/**
 *
 * The base output interfaces specifies the underlying way of writing data to the underlying
 * transport (stream,byte[],ByteBuffer). You should have no need to interact with this interface and
 * is here for compatibility with older style serialization libraries. Check the @TypeReaderWriter
 * for further information.
 *
 */
public interface TypeBaseOutput {

	public static final int UINT8_MIN = 0;
	public static final int UINT8_MAX = 256 - 1;

	public static final int UINT16_MIN = 0;
	public static final int UINT16_MAX = 65536; // 2^16-1;

	public static final long UINT32_MIN = 0;
	// max value is 2^16 x 2^16.. just as easy way to write it.
	public static final long UINT32_MAX = 65536l * 65536l;// 2^32-1;

	// NOTE: As java can not store anything bigger than
	// a long. It means that the max value is one
	// bit short of the max of 2^64.
	public final long UINT64_MIN = 0;
	public final long UINT64_MAX = 9223372036854775807l; // 2^63-1;

	public void writeInt8(byte b) throws IOException;

	public void writeUInt8(short b) throws IOException;

	public void writeInt16(short b) throws IOException;

	public void writeUInt16(int b) throws IOException;

	default public void writeLeInt16(short b) throws IOException {
		writeInt16(Short.reverseBytes(b));
	}

	default public void writeLeUInt16(int b) throws IOException {
		writeUInt16(Integer.reverseBytes(b));
	}

	public void writeInt32(int b) throws IOException;

	public void writeUInt32(long b) throws IOException;

	default public void writeLeInt32(int b) throws IOException {
		writeInt32(Integer.reverseBytes(b));
	}

	default public void writeLeUInt32(long b) throws IOException {
		writeUInt32(Long.reverseBytes(b));
	}

	public void writeUVarInt32(int i) throws IOException;

	// Variable length signed integer using zig-zag encoding.
	default public void writeVarInt32(int s) throws IOException {
		writeUVarInt32((s << 1) ^ (s >> 31));
	}

	public void writeInt64(long b) throws IOException;

	public void writeUInt64(long b) throws IOException;

	default public void writeLeInt64(long b) throws IOException {
		writeInt64(Long.reverseBytes(b));
	}

	default public void writeLeUInt64(long b) throws IOException {
		writeUInt64(Long.reverseBytes(b));
	}

	public void writeLeUInt64(BigInteger v) throws IOException;

	public void writeUVarInt64(long v) throws IOException;

	public void writeUVarInt64(BigInteger v) throws IOException;

	default public void writeVarInt64(long s) throws IOException {
		writeUVarInt64((s << 1) ^ (s >> 63));
	}

	public default void writeFloat(float f) throws IOException {
		final int s = Float.floatToIntBits(f);
		writeInt32(s);
	}

	public default void writeDouble(double d) throws IOException {
		final long s = Double.doubleToLongBits(d);
		writeInt64(s);
	}

	public void writeBytes(byte[] buffer, int offset, int length) throws IOException;

	default public void writeBoolean(boolean b) throws IOException {
		writeInt8((byte) (b ? 1 : 0));
	}

}
