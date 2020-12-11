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

import io.litterat.xpl.TypeBaseOutput;

public class ByteBufferBaseOutput implements TypeBaseOutput {

	private final ByteBuffer buffer;


	public ByteBufferBaseOutput(ByteBuffer buffer) {
		this.buffer = buffer;
	}


	@Override
	public void writeInt8(byte b) throws IOException {
		buffer.put(b);
	}


	@Override
	public void writeUInt8(short b) throws IOException {
		buffer.put((byte) b);
	}


	@Override
	public void writeInt16(short s) throws IOException {
		buffer.putShort(s);
	}


	@Override
	public void writeUInt16(int s) throws IOException {
		buffer.putShort((short) s);
	}


	@Override
	public void writeInt32(int i) throws IOException {
		buffer.putInt(i);
	}


	@Override
	public void writeUInt32(long i) throws IOException {
		buffer.putInt((int) i);
	}


	@Override
	public void writeUVarInt32(int i) throws IOException {
		buffer.putInt(i);
	}


	@Override
	public void writeInt64(long l) throws IOException {
		buffer.putLong(l);
	}


	@Override
	public void writeUInt64(long l) throws IOException {
		buffer.putLong(l);
	}


	@Override
	public void writeUVarInt64(long l) throws IOException {
		buffer.putLong(l);
	}


	@Override
	public void writeBytes(byte[] src, int offset, int length) throws IOException {
		buffer.put(src, offset, length);
	}
}
