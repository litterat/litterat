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

import java.io.IOException;

import io.litterat.xpl.TypeBaseInput;

public class ByteArrayBaseInput implements TypeBaseInput {

	private final byte[] buffer;
	private int pos;

	public ByteArrayBaseInput(byte[] buffer) {
		this.buffer = buffer;
		this.pos = 0;
	}

	@Override
	public byte readInt8() throws IOException {
		return (byte) ((buffer[pos++] & 0xff) << 0);
	}

	@Override
	public short readInt16() throws IOException {
		return (short) (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8));
	}

	@Override
	public int readInt32() throws IOException {
		return (((buffer[pos++] & 0xff) << 0) | ((buffer[pos++] & 0xff) << 8) | ((buffer[pos++] & 0xff) << 16)
				| ((buffer[pos++] & 0xff) << 24));

	}

	@Override
	public long readInt64() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readUInt8() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int readUInt16() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readUInt32() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readUInt64() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	// https://en.wikipedia.org/wiki/LEB128

	@Override
	public int readUVarInt32() throws IOException {
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
		System.arraycopy(buffer, pos, dst, offset, length);
		pos += length;

	}

}
