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
package io.litterat.xpl.io;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;

import io.litterat.xpl.TypeBaseInput;

public class ByteArrayBaseInput implements TypeBaseInput {

	private final byte[] buffer;
	private int pos;

	public ByteArrayBaseInput(byte[] buffer) {
		this.buffer = buffer;
		this.pos = 0;
	}

	private void checkAvailable(int bytes) throws IOException {
		if (pos + bytes > buffer.length) {
			throw new EOFException();
		}
	}

	@Override
	public byte readInt8() throws IOException {
		checkAvailable(1);
		return (byte) ((buffer[pos++] & 0xff) << 0);
	}

	@Override
	public short readInt16() throws IOException {
		checkAvailable(2);
		return (short) (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8));
	}

	@Override
	public int readInt32() throws IOException {
		checkAvailable(4);
		return (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24));

	}

	@Override
	public long readInt64() throws IOException {
		checkAvailable(8);
		return (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24)) | ((buffer[pos++] & 0xff) << 32) | ((buffer[pos++] & 0xff) << 40)
				| ((buffer[pos++] & 0xff) << 48) | ((buffer[pos++] & 0xff) << 56);
	}

	@Override
	public short readUInt8() throws IOException {
		checkAvailable(1);
		return (short) ((buffer[pos++] & 0xff) << 0);
	}

	@Override
	public int readUInt16() throws IOException {
		checkAvailable(2);
		return ((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8);
	}

	@Override
	public long readUInt32() throws IOException {
		checkAvailable(4);
		return (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24));
	}

	// https://stackoverflow.com/questions/55752927/how-to-convert-an-unsigned-long-to-biginteger
	private static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);

	@Override
	public BigInteger readUInt64() throws IOException {
		checkAvailable(8);
		long rawBits = (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24)) | ((buffer[pos++] & 0xff) << 32) | ((buffer[pos++] & 0xff) << 40)
				| ((buffer[pos++] & 0xff) << 48) | ((buffer[pos++] & 0xff) << 56);

		if (rawBits > 0) {
			return BigInteger.valueOf(rawBits);
		} else {
			return BigInteger.valueOf(rawBits).and(UNSIGNED_LONG_MASK);
		}
	}

	@Override
	public BigInteger readLeUInt64() throws IOException {
		checkAvailable(8);
		long rawBits = (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24)) | ((buffer[pos++] & 0xff) << 32) | ((buffer[pos++] & 0xff) << 40)
				| ((buffer[pos++] & 0xff) << 48) | ((buffer[pos++] & 0xff) << 56);

		rawBits = Long.reverseBytes(rawBits);
		if (rawBits > 0) {
			return BigInteger.valueOf(rawBits);
		} else {
			return BigInteger.valueOf(rawBits).and(UNSIGNED_LONG_MASK);
		}
	}

	// https://en.wikipedia.org/wiki/LEB128

	@Override
	public int readUVarInt32() throws IOException {
		checkAvailable(5);
		int result = 0;
		int shift = 0;
		while (true) {
			byte b = buffer[pos++];
			result |= (b & 0x7f) << shift;
			if ((b & 0x80) == 0)
				break;
			shift += 7;
		}
		return result;
	}

	@Override
	public long readUVarInt64() throws IOException {
		checkAvailable(9);
		long result = 0;
		int shift = 0;
		while (true) {
			byte b = buffer[pos++];
			result |= (b & 0x7f) << shift;
			if ((b & 0x80) == 0)
				break;
			shift += 7;
		}
		return result;
	}

	@Override
	public void readBytes(byte[] dst, int offset, int length) throws IOException {
		checkAvailable(length);
		System.arraycopy(buffer, pos, dst, offset, length);
		pos += length;

	}

}
