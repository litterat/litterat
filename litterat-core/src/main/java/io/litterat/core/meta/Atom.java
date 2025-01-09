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
package io.litterat.core.meta;

import io.litterat.bind.annotation.Union;
import io.litterat.core.meta.atom.AtomAttribute;

import java.util.Arrays;
import java.util.Objects;

/**
 * 
 * An atom is an unsealed union type. Various atom definitions can be bound to this type.
 *
 */

@io.litterat.bind.annotation.Typename(namespace = "meta", name = "atom")
@Union(value = {}, sealed = false)
public abstract class Atom implements Definition {

	private final AtomAttribute[] attributes;

	public Atom(AtomAttribute[] attributes) {
		this.attributes = attributes;
	}

	public AtomAttribute[] attributes() {
		return attributes;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Atom atom = (Atom) o;
		return Objects.deepEquals(attributes, atom.attributes);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(attributes);
	}
}
