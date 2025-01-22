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
package io.litterat.schema.meta;

import io.litterat.annotation.Typename;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * A Record represents the
 *
 */

@io.litterat.annotation.Record
@Typename(namespace = "meta", name = "record")
public class Record implements Element {

	private final Field[] fields;

	public Record(Field[] fields) {
		this.fields = fields;
	}

	public Field[] fields() {
		return this.fields;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Record record = (Record) o;
		return Objects.deepEquals(fields, record.fields);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(fields);
	}
}
