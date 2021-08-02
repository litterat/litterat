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

import io.litterat.xpl.TypeBaseOutput;

public class ByteArrayBaseOutput implements TypeBaseOutput {

	private final byte[] buffer;
	private int pos;

	public ByteArrayBaseOutput(byte[] buffer) {
		this.buffer = buffer;
		this.pos = 0;
	}

	public int getBytesWritten() {
		return pos;
	}

	private void checkSpace(int bytes) throws IOException {
		if (pos + bytes > buffer.length) {
			throw new EOFException();
		}
	}

	@Override
	public void writeBoolean(boolean b) throws IOException {
		writeInt8((byte) (b ? 1 : 0));
	}

	@Override
	public void writeInt8(byte b) throws IOException {
		checkSpace(1);
		buffer[pos++] = b;
	}

	@Override
	public void writeInt16(short s) throws IOException {
		checkSpace(2);
		buffer[pos++] = ((byte) (s & 0xff));
		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
	}

	@Override
	public void writeInt32(int s) throws IOException {
		checkSpace(4);
		buffer[pos++] = ((byte) (s & 0xff));
		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
		buffer[pos++] = ((byte) ((s >> 16) & 0xff));
		buffer[pos++] = ((byte) ((s >> 24) & 0xff));

	}

	@Override
	public void writeInt64(long s) throws IOException {
		checkSpace(8);
		buffer[pos++] = ((byte) (s & 0xff));
		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
		buffer[pos++] = ((byte) ((s >> 16) & 0xff));
		buffer[pos++] = ((byte) ((s >> 24) & 0xff));
		buffer[pos++] = ((byte) ((s >> 32) & 0xff));
		buffer[pos++] = ((byte) ((s >> 40) & 0xff));
		buffer[pos++] = ((byte) ((s >> 48) & 0xff));
		buffer[pos++] = ((byte) ((s >> 56) & 0xff));
		buffer[pos++] = ((byte) ((s >> 64) & 0xff));
	}

	@Override
	public void writeVarInt32(int s) throws IOException {
		checkSpace(4);
		do {
			int b = s & 0x7f;
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			buffer[pos++] = ((byte) b);
		} while (s != 0);
	}

	@Override
	public void writeVarInt64(long s) throws IOException {
		checkSpace(8);
		do {
			int b = (int) (s & 0x7f);
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			buffer[pos++] = ((byte) b);
		} while (s != 0);

	}

	@Override
	public void writeUInt8(short b) throws IOException {
		checkSpace(1);
		if (b < UINT8_MIN || b > UINT8_MAX)
			throw new IOException("uint8: out of range: " + b);
		buffer[pos++] = ((byte) b);

	}

	@Override
	public void writeUInt16(int s) throws IOException {
		checkSpace(2);
		if (s < UINT16_MIN || s > UINT16_MAX)
			throw new IOException("uint16: value out of range:" + s);

		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
		buffer[pos++] = ((byte) (s & 0xff));
	}

	@Override
	public void writeUInt32(long s) throws IOException {
		checkSpace(4);
		if (s < UINT32_MIN || s > UINT32_MAX)
			throw new IOException("uint32: value out of range:" + s);

		buffer[pos++] = ((byte) (s & 0xff));
		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
		buffer[pos++] = ((byte) ((s >> 16) & 0xff));
		buffer[pos++] = ((byte) ((s >> 24) & 0xff));
	}

	@Override
	public void writeUInt64(long s) throws IOException {
		checkSpace(8);
		buffer[pos++] = ((byte) (s & 0xff));
		buffer[pos++] = ((byte) ((s >> 8) & 0xff));
		buffer[pos++] = ((byte) ((s >> 16) & 0xff));
		buffer[pos++] = ((byte) ((s >> 24) & 0xff));
		buffer[pos++] = ((byte) ((s >> 32) & 0xff));
		buffer[pos++] = ((byte) ((s >> 40) & 0xff));
		buffer[pos++] = ((byte) ((s >> 48) & 0xff));
		buffer[pos++] = ((byte) ((s >> 56) & 0xff));
		buffer[pos++] = ((byte) ((s >> 64) & 0xff));
	}

	@Override
	public void writeUVarInt32(int s) throws IOException {
		checkSpace(5);
		do {
			int b = s & 0x7f;
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			buffer[pos++] = (byte) b;
		} while (s != 0);

	}

	@Override
	public void writeUVarInt64(long s) throws IOException {
		checkSpace(9);
		do {
			int b = (int) (s & 0x7f);
			s >>>= 7;
			if (s != 0) {
				b |= 0x80;
			}
			buffer[pos++] = (byte) b;
		} while (s != 0);

	}

	@Override
	public void writeLeUInt64(BigInteger v) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException("not implemented");
	}

	@Override
	public void writeUVarInt64(BigInteger v) throws IOException {
		// TODO Auto-generated method stub
		throw new IOException("not implemented");
	}

	@Override
	public void writeBytes(byte[] src, int offset, int length) throws IOException {
		checkSpace(length);
		System.arraycopy(src, offset, buffer, pos, length);
		pos += length;
	}

}
