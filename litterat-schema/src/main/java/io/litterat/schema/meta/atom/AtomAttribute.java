/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.schema.meta.atom;

import io.litterat.bind.annotation.Field;
import io.litterat.bind.annotation.Record;
import io.litterat.bind.annotation.Typename;
import io.litterat.bind.annotation.Union;

@Union
@Typename(namespace = "schema", name = "atom_attribute")
public abstract class AtomAttribute {

	@Record
	@Typename(namespace = "schema", name = "atom_fixed_length")
	public static class AtomFixedLength extends AtomAttribute {

		@Typename(name = "uint8")
		private final int bytes;

		public AtomFixedLength(int bytes) {
			this.bytes = bytes;
		}

		public int bytes() {
			return this.bytes;
		}
	}

	@Record
	@Typename(namespace = "schema", name = "atom_variable_length")
	public static class AtomVariableLength extends AtomAttribute {

		@Typename(name = "uint8")
		private final int minBytes;

		@Typename(name = "uint8")
		private final int maxBytes;

		public AtomVariableLength(@Field(name = "min_bytes") int minBytes, @Field(name = "max_bytes") int maxBytes) {
			this.minBytes = minBytes;
			this.maxBytes = maxBytes;
		}

		public int minBytes() {
			return this.minBytes;
		}

		public int maxBytes() {
			return this.maxBytes;
		}
	}

}
