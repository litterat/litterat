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
package io.litterat.xpl.resolve;

import java.io.IOException;

import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.TypeReader;
import io.litterat.xpl.TypeWriter;

public class StringReaderWriter {

	public static class StringReader implements TypeReader {

		@Override
		public Object read(TypeInputStream reader) throws IOException {

			int length = reader.input().readUVarInt32();
			if (length == 0) {
				return null;
			} else {
				byte[] bytes = new byte[length - 1];
				reader.input().readBytes(bytes, 0, length - 1);

				return new String(bytes);
			}
		}

	}

	public static class StringWriter implements TypeWriter {

		@Override
		public void write(TypeOutputStream writer, Object o) throws IOException {

			if (o == null) {
				writer.output().writeUVarInt32(0);
			} else {
				byte[] bytes = ((String) o).getBytes();
				writer.output().writeUVarInt32(bytes.length + 1);
				writer.output().writeBytes(bytes, 0, bytes.length);
			}
		}

	}
}
