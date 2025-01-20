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

import io.litterat.bind.Record;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.annotation.SchemaType;

/**
 * 
 * Definition of a string atom.
 * 
 * @formatter:off
 * TODO This should extend or implement Atom.
 * TODO Common string restrictions such as min/max length required. 
 * TODO Should be able to base one string on a previous definition.
 * @formatter:on
 * 
 */
@SchemaType(namespace = "schema", name = "encoding")
public class StringAtom implements Definition {

	private final String encoding;

	@Record
	public StringAtom(String encoding) {
		this.encoding = encoding;
	}

	public String encoding() {
		return encoding;
	}
}
