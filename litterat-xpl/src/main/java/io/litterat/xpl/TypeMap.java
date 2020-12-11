/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.xpl;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.litterat.schema.TypeException;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.meta.SchemaTypes;
import io.litterat.schema.types.TypeName;
import io.litterat.xpl.util.IntObjectHashMap;

public class TypeMap {

	private final TypeLibrary library;
	private final IntObjectHashMap<TypeMapEntry> types;
	private final ConcurrentHashMap<Class<?>, TypeMapEntry> classes;
	private final ConcurrentHashMap<TypeName, TypeMapEntry> typeNames;

	private final AtomicInteger lastIdentifier;

	public TypeMap(TypeLibrary library) throws TypeException {
		Objects.requireNonNull(library, "TypeLibrary is required");

		this.library = library;

		this.types = new IntObjectHashMap<>();
		this.classes = new ConcurrentHashMap<>();
		this.typeNames = new ConcurrentHashMap<>();
		this.lastIdentifier = new AtomicInteger(0);
	}

	public void registerMetaData(TypeResolver resolver) {
		try {
			register(1, resolver.map(SchemaTypes.TYPE_NAME_DEFINITION));
			register(2, resolver.map(SchemaTypes.TYPE_NAME));
			register(3, resolver.map(TypeLibrary.STRING));
			register(4, resolver.map(SchemaTypes.DEFINITION));
			register(5, resolver.map(SchemaTypes.SEQUENCE));
			register(6, resolver.map(SchemaTypes.FIELD));
			register(7, resolver.map(SchemaTypes.ELEMENT));
			register(8, resolver.map(SchemaTypes.REFERENCE));
		} catch (TypeException e) {
			throw new RuntimeException("Initialization error", e);
		}
	}

	public TypeLibrary library() {
		return library;
	}

	public TypeMapEntry register(int streamId, TypeMapEntry newEntry) throws TypeException {

		// Very simplistic lock strategy. Revisit sometime.
		synchronized (lastIdentifier) {

			// Check it isn't already registered.
			TypeMapEntry entry = typeNames.get(newEntry.typeName());
			if (entry != null) {
				// just return what was there.
				return entry;
			}

			// Grab next identifier.
			if (streamId == 0) {

				while (true) {
					streamId = lastIdentifier.incrementAndGet();
					if (types.get(streamId) == null) {
						break;
					}
				}
			}

			// Crate new entry.
			entry = new TypeMapEntry(streamId, newEntry.typeName(), newEntry.definition(), newEntry.reader(),
					newEntry.writer());
			types.put(streamId, entry);
			typeNames.put(entry.typeName(), entry);

			// Atomic types will duplicate entries. First one wins.
			classes.putIfAbsent(library.getTypeClass(entry.typeName()).dataClass(), entry);

			return entry;
		}
	}

	public TypeMapEntry getEntry(TypeName typeName) {
		return typeNames.get(typeName);
	}

	public TypeMapEntry getEntry(Class<?> clzz) {
		return classes.get(clzz);
	}

	public TypeMapEntry getEntry(int streamId) {
		return types.get(streamId);
	}

}
