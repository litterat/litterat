/*
 * Copyright (c) 2003-2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.xpl.lang;

import io.litterat.bind.Record;
import io.litterat.model.Signature;
import io.litterat.model.annotation.SchemaType;

@Record
@SchemaType(namespace = "xpl.lang", name = "lambda")
public class Lambda {

	private final Signature signature;
	private final Block block;
	private final Class<?>[] slots;

	public Lambda(Signature signature, Class<?>[] slots, Block block) {
		this.signature = signature;
		this.slots = slots;
		this.block = block;
	}

	public Signature signature() {
		return signature;
	}

	public Block block() {
		return block;
	}

	public Class<?>[] slots() {
		return slots;
	}
}
