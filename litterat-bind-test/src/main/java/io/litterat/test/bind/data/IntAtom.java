/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.test.bind.data;

import java.util.HashMap;
import java.util.Map;

import io.litterat.bind.Atom;

/**
 * This tests the concept of an immutable atom. The constructor uses a static method to return the
 * value from a known set of objects. This might be useful where deserialization might generate a
 * lot of duplicate objects and put pressure on the garbage collector.
 *
 */
public class IntAtom {

	private final int id;

	private IntAtom(int id) {
		this.id = id;
	}

	@Atom
	public int id() {
		return id;
	}

	private static final Map<Integer, IntAtom> atomList = new HashMap<>();

	@Atom
	public static final IntAtom getAtom(int id) {
		IntAtom atom = atomList.get(id);
		if (atom == null) {
			atom = new IntAtom(id);
			atomList.put(id, atom);
		}
		return atom;
	}
}
