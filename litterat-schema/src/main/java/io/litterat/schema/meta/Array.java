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

import io.litterat.annotation.Record;

import java.util.Objects;

/**
 *
 * An array is a structural element which defines a repeated element type.
 *
 * In the future it might be useful to introduce restrictions on the array type to specify a min/max
 * elements.
 *
 */

@Record
@io.litterat.annotation.Typename(namespace = "meta", name = "array")
public class Array implements Element {

	private final Typename type;

	public Array(Typename type) {
		this.type = type;
	}

	public Typename type() {
		return this.type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Array array = (Array) o;
		return Objects.equals(type, array.type);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
	}
}
