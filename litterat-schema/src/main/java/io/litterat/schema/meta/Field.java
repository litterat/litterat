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
import io.litterat.annotation.Typename;

import java.util.Objects;

/**
 * A field is a structural element of a record. It is of a specific element type and is either
 * required or not. The type can be a Typename, Array, Union, or Record.
 */

@Typename(namespace = "meta", name = "field")
public class Field {

	private final String name;
	private final Element type;
	private final boolean required;

	@Record
	public Field(String name, Element type, boolean required) {
		this.name = name;
		this.type = type;
		this.required = required;
	}

	public Field(String name, Element type) {
		this(name, type, false);
	}

	public String name() {
		return this.name;
	}

	public Element type() {
		return this.type;
	}

	public boolean isRequired() {
		return required;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Field field = (Field) o;
		return required == field.required && Objects.equals(name, field.name) && Objects.equals(type, field.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type, required);
	}
}
