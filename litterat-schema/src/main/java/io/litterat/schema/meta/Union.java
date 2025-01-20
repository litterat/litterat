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

import io.litterat.bind.annotation.Record;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * A Union is a record that represents a choice between a list of rules. The list of rules are
 * included as a Reference using TypeNames.
 *
 */

@Record
@io.litterat.bind.annotation.Typename(namespace = "meta", name = "union")
public class Union implements Element {


	private final Typename[] map;

	private final boolean isSealed;

	@Record
	public Union(Typename[] map, boolean isSealed) {
		this.map = map;
		this.isSealed = isSealed;
	}

	public Union(Typename[] map) {
		this(map, true);
	}

	public Typename[] map() {
		return map;
	}

	public boolean isSealed() {
		return isSealed;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Union union = (Union) o;
		return isSealed == union.isSealed && Objects.deepEquals(map, union.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(map), isSealed);
	}
}
