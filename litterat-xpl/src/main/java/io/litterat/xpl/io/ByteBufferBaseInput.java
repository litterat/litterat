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
import java.nio.ByteBuffer;

import io.litterat.xpl.TypeBaseInput;

public class ByteBufferBaseInput implements TypeBaseInput {

	private final ByteBuffer input;

	// TODO Need to catch BufferUnderflowException somewhere.

	public ByteBufferBaseInput(ByteBuffer input) {
		this.input = input;
	}

	@Override
	public byte readInt8() throws IOException {
		return input.get();
	}

	@Override
	public short readUInt8() throws IOException {
		return (short) (input.get() & 0xff);
	}

	@Override
	public short readInt16() throws IOException {
		return input.getShort();
	}

	@Override
	public int readUInt16() throws IOException {
		return (input.getShort() & 0xffff);
	}

	@Override
	public int readInt32() throws IOException {
		return input.getInt();
	}

	@Override
	public long readUInt32() throws IOException {
		return (input.getInt() & 0xffffffffL);
	}

	@Override
	public long readInt64() throws IOException {
		return input.getLong();
	}

	@Override
	public long readUInt64() throws IOException {
		return input.getLong();
	}

	@Override
	public int readUVarInt32() throws IOException {
		int result = 0;
		int shift = 0;
		while (true) {
			int b = input.get();
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
			int b = input.get();
			result |= (b & 0x7f) << shift;
			if ((b & 0x80) == 0)
				break;
			shift += 7;
		}
		return result;
	}

	@Override
	public void readBytes(byte[] buffer, int offset, int length) throws IOException {
		input.get(buffer, offset, length);
	}

}
