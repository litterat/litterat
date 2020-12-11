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
import java.io.OutputStream;

import io.litterat.xpl.TypeBaseOutput;

// https://tools.ietf.org/html/draft-newman-network-byte-order-01
// Fixed primitives use Little Endian Byte order.
// Variable length primitives are using Little Endian Byte order.
//
public class StreamBaseOutput implements TypeBaseOutput {

	private final OutputStream out;

	public StreamBaseOutput(OutputStream out) {
		this.out = out;
	}

	@Override
	public void writeInt8(byte b) throws IOException {
		out.write(b & 0xff);
	}

	@Override
	public void writeInt16(short s) throws IOException {
		out.write((s >> 8) & 0xff);
		out.write(s & 0xff);
	}

	@Override
	public final void writeInt32(int s) throws IOException {
		out.write((byte) (s & 0xff));
		out.write((byte) ((s >> 8) & 0xff));
		out.write((byte) ((s >> 16) & 0xff));
		out.write((byte) ((s >> 24) & 0xff));
	}

	@Override
	public void writeInt64(long s) throws IOException {
		out.write((byte) (s & 0xff));
		out.write((byte) ((s >> 8) & 0xff));
		out.write((byte) ((s >> 16) & 0xff));
		out.write((byte) ((s >> 24) & 0xff));
		out.write((byte) ((s >> 32) & 0xff));
		out.write((byte) ((s >> 40) & 0xff));
		out.write((byte) ((s >> 48) & 0xff));
		out.write((byte) ((s >> 56) & 0xff));
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		writeInt8((byte) (b ? 1 : 0));
	}

	@Override
	public void writeUInt8(short b) throws IOException {
		if (b < UINT8_MIN || b > UINT8_MAX)
			throw new IOException("uint8: out of range: " + b);
		out.write(b);
	}

	@Override
	public void writeUInt16(int s) throws IOException {
		if (s < UINT16_MIN || s > UINT16_MAX)
			throw new IOException("uint16: value out of range:" + s);

		out.write((s >> 8) & 0xff);
		out.write(s & 0xff);
	}

	@Override
	public void writeUInt32(long s) throws IOException {
		if (s < UINT32_MIN || s > UINT32_MAX)
			throw new IOException("uint32: value out of range:" + s);

		out.write((int) (s & 0xff));
		out.write((int) ((s >> 8) & 0xff));
		out.write((int) ((s >> 16) & 0xff));
		out.write((int) ((s >> 24) & 0xff));
	}

	@Override
	public void writeUInt64(long s) throws IOException {
		if (s < UINT64_MIN || s > UINT64_MAX)
			throw new IOException("uint64: value out of range: " + s);

		out.write((int) (s & 0xff));
		out.write((int) ((s >> 8) & 0xff));
		out.write((int) ((s >> 16) & 0xff));
		out.write((int) ((s >> 24) & 0xff));
		out.write((int) ((s >> 32) & 0xff));
		out.write((int) ((s >> 40) & 0xff));
		out.write((int) ((s >> 48) & 0xff));
		out.write((int) ((s >> 56) & 0xff));
	}

	// https://en.wikipedia.org/wiki/LEB128
	@Override
	public void writeUVarInt32(int s) throws IOException {

		do {
			int b = s & 0x7f;
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			out.write(b);
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
			out.write(b);
		} while (s != 0);
	}

	@Override
	public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
		writeUVarInt32(length);
		out.write(buffer, offset, length);
	}

}
