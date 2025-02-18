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

import java.util.Objects;
import java.util.regex.Pattern;

import io.litterat.annotation.Record;

/**
 * @formatter:off
 *
 * A type name provides a unique identifier into the type library. A type name
 * is uniquely identified using the namespace, name, optional attribute and
 * version.
 * <p>
 * For example:
 * <p>
 *    schema.sequence#1 : Identifies version 1 of the schema.sequence definition.
 *    schema.atom:atom_size : Identifies the abstract mapping from atom to atom_size s
 *    foo.example:bar#1 : Identifies the bar field in the record definition foo.example.
 * <p>
 * A type version must be specified for any type in the type library. A TypeName used for a
 * reference can specify a version of 0 to specify an unknown version.
 *
 * @formatter:on
 */

@io.litterat.annotation.Typename(namespace = "meta", name = "type_name")
public class Typename implements Element {

	public static final String ROOT_NAMESPACE = "";

	private static final Pattern namespacePattern = Pattern
			.compile("([a-zA-Z_][a-zA-Z\\d_]*\\.)*[a-zA-Z_][a-zA-Z\\d_]*");
	private static final Pattern namePattern = Pattern.compile("[a-zA-Z_$][a-zA-Z\\d_]*");

	private final String namespace;
	private final String name;

	@Record
	public Typename(String namespace, String name) {
		Objects.requireNonNull(namespace, "Namespace can not be null");
		Objects.requireNonNull(name, "Name can not be null");

		if (!namespace.equals(ROOT_NAMESPACE) & !namespacePattern.matcher(namespace).matches()) {
			throw new IllegalArgumentException("namespace is invalid:" + namespace);
		}

		if (!namePattern.matcher(name).matches()) {
			throw new IllegalArgumentException("name is invalid: " + name);
		}

		this.namespace = namespace;
		this.name = name;

	}

	public Typename(String name) {
		this(ROOT_NAMESPACE, name);
	}

	public String namespace() {
		return this.namespace;
	}

	public String name() {
		return this.name;
	}

	@Override
	public String toString() {
		return (!namespace.equals(ROOT_NAMESPACE) ? (namespace + ".") : "") + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Typename other = (Typename) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
}
