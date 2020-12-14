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
import java.io.InputStream;

import io.litterat.xpl.TypeBaseInput;

public class StreamBaseInput implements TypeBaseInput {

	private final InputStream input;

	public StreamBaseInput(InputStream input) {
		this.input = input;
	}

	@Override
	public byte readInt8() throws IOException {
		int b = input.read();
		if (b < 0) {
			throw new EOFException();
		}
		return (byte) b;
	}

	@Override
	public short readUInt8() throws IOException {
		int b = input.read();
		if (b < 0) {
			throw new EOFException();
		}
		return (short) b;
	}

	@Override
	public short readInt16() throws IOException {

		int a = input.read();
		int b = input.read();
		if ((a | b) < 0) {
			throw new EOFException();
		}

		return (short) (((a & 0xff) << 8) | (b & 0xff));
	}

	@Override
	public int readUInt16() throws IOException {

		int a = input.read();
		int b = input.read();
		if ((a | b) < 0) {
			throw new EOFException();
		}

		return ((a & 0xff) << 8) | (b & 0xff);
	}

	@Override
	public int readInt32() throws IOException {
		int a = input.read();
		int b = input.read();
		int c = input.read();
		int d = input.read();
		if ((a | b | c | d) < 0) {
			throw new EOFException();
		}

		return (((a & 0xff) << 0) | ((b & 0xff) << 8) | ((c & 0xff) << 16) | (d & 0xff) << 24);
	}

	@Override
	public long readUInt32() throws IOException {
		int a = input.read();
		int b = input.read();
		int c = input.read();
		int d = input.read();
		if ((a | b | c | d) < 0) {
			throw new EOFException();
		}

		return (((a & 0xff) << 0) | ((b & 0xff) << 8) | ((c & 0xff) << 16) | (d & 0xff) << 24);
	}

	@Override
	public long readInt64() throws IOException {

		int a = input.read();
		int b = input.read();
		int c = input.read();
		int d = input.read();
		int e = input.read();
		int f = input.read();
		int g = input.read();
		int h = input.read();
		if ((a | b | c | d | e | f | g | h) < 0) {
			throw new EOFException();
		}

		return (((a & 0xff) << 0) | ((b & 0xff) << 8) | ((c & 0xff) << 16) | ((d & 0xff) << 24) | ((e & 0xff) << 32)
				| ((f & 0xff) << 40) | ((g & 0xff) << 48) | (h & 0xff) << 56);
	}

	@Override
	public long readUInt64() throws IOException {
		int a = input.read();
		int b = input.read();
		int c = input.read();
		int d = input.read();
		int e = input.read();
		int f = input.read();
		int g = input.read();
		int h = input.read();
		if ((a | b | c | d | e | f | g | h) < 0) {
			throw new EOFException();
		}

		return (((a & 0xff) << 0) | ((b & 0xff) << 8) | ((c & 0xff) << 16) | ((d & 0xff) << 24) | ((e & 0xff) << 32)
				| ((f & 0xff) << 40) | ((g & 0xff) << 48) | (h & 0xff) << 56);
	}

	@Override
	public int readUVarInt32() throws IOException {
		int result = 0;
		int shift = 0;
		while (true) {
			int b = input.read();
			if (b < 0) {
				throw new EOFException();
			}
			result |= (b & 0x7f) << shift;
			if ((b & 0x80) == 0)
				break;
			shift += 7;
		}
		return result;
	}

	@Override
	public long readUVarInt64() throws IOException {
		long result = 0;
		int shift = 0;
		while (true) {
			int b = input.read();
			if (b < 0) {
				throw new EOFException();
			}
			result |= (b & 0x7f) << shift;
			if ((b & 0x80) == 0)
				break;
			shift += 7;
		}
		return result;
	}

	@Override
	public void readBytes(byte[] buffer, int offset, int length) throws IOException {
		input.read(buffer, offset, length);
	}

}
