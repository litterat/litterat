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

import io.litterat.model.TypeName;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.TypeReader;
import io.litterat.xpl.TypeWriter;

public class UnionReaderWriter implements TypeReader, TypeWriter {

	@SuppressWarnings("unused")
	private final TypeName typeName;

	public UnionReaderWriter(TypeName typeName) {

		this.typeName = typeName;
	}

	@Override
	public void write(TypeOutputStream writer, Object o) throws IOException {

		writer.writeObject(o);

//		TypeBaseOutput output = writer.output();
//		if (o == null) {
//			// null output.
//			output.writeUVarInt32(0);
//		} else {
//			// Should do more work here.
//			// int streamId = writer.getStreamIdentifier(o.getClass());
//			// output.writeUVarInt32(streamId);
//			writer.writeObject(o);
//		}

	}

	@Override
	public Object read(TypeInputStream reader) throws IOException {

		return reader.readObject();

//		TypeBaseInput input = reader.input();
//		Object result = null;
//
//		int streamId = input.readUVarInt32();
//		if (streamId != 0) {
//
//			result = reader.readObject();
////			TypeMapEntry entry = reader.typeMap().getEntry(streamId);
////			if (entry != null) {
////				result = reader.readObject(entry.typeName());
////			} else {
////				throw new IOException("streamId not found: " + streamId);
////			}
//
//		}
//		return result;
	}

}
