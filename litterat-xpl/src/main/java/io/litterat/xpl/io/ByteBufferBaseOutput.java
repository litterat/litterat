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

// Based on...
// https://stackoverflow.com/questions/9883472/is-it-possible-to-have-an-unsigned-bytebuffer-in-java

public class ByteBufferBaseOutput implements TypeBaseOutput {

	private final ByteBuffer output;

	public ByteBufferBaseOutput(ByteBuffer output) {
		this.output = output;
	}

	@Override
	public void writeInt8(byte b) throws IOException {
		output.put(b);
	}

	@Override
	public void writeUInt8(short b) throws IOException {
		output.put((byte) (b & 0xff));
	}

	@Override
	public void writeInt16(short s) throws IOException {
		output.putShort(s);
	}

	@Override
	public void writeUInt16(int s) throws IOException {
		output.putShort((short) (s & 0xffff));
	}

	@Override
	public void writeInt32(int i) throws IOException {
		output.putInt(i);
	}

	@Override
	public void writeUInt32(long i) throws IOException {
		output.putInt((int) (i & 0xffffffffL));
	}

	@Override
	public void writeInt64(long l) throws IOException {
		output.putLong(l);
	}

	@Override
	public void writeUInt64(long l) throws IOException {
		// TODO Need to decide about representation of unsigned long in Java.
		output.putLong(l);
	}

	@Override
	public void writeUVarInt32(int s) throws IOException {
		do {
			int b = s & 0x7f;
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			output.put((byte) b);
		} while (s != 0);
	}

	@Override
	public void writeUVarInt64(long s) throws IOException {
		do {
			int b = (int) (s & 0x7f);
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			output.put((byte) b);
		} while (s != 0);
	}

	@Override
	public void writeBytes(byte[] src, int offset, int length) throws IOException {
		output.put(src, offset, length);
	}
}
