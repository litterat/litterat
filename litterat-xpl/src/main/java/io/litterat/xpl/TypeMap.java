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
package io.litterat.xpl;

import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Array;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Element;
import io.litterat.schema.meta.Entry;
import io.litterat.schema.meta.Field;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Record;
import io.litterat.schema.meta.Typename;
import io.litterat.xpl.resolve.SchemaResolver;
import io.litterat.xpl.util.IntObjectHashMap;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TypeMap {

	private final TypeContext context;
	private final IntObjectHashMap<TypeMapEntry> types;
	private final ConcurrentHashMap<Type, TypeMapEntry> classes;
	private final ConcurrentHashMap<Typename, TypeMapEntry> typeNames;

	private final SchemaResolver resolver;

	private final AtomicInteger lastIdentifier;

	public TypeMap(TypeContext context) {
		Objects.requireNonNull(context, "TypeContext is required");

		this.context = context;

		this.types = new IntObjectHashMap<>();
		this.classes = new ConcurrentHashMap<>();
		this.typeNames = new ConcurrentHashMap<>();
		this.lastIdentifier = new AtomicInteger(0);

		this.resolver = new SchemaResolver(this);

		// Register the base types to communicate schema definitions.
		registerMetaData(resolver);
	}

	private void registerMetaData(SchemaResolver resolver) {
		try {
			register(1, resolver.register(Meta.STRING, String.class));
			register(2, resolver.register(Meta.ENTRY, Entry.class));
			register(3, resolver.register(Meta.TYPENAME, Typename.class));
			register(4, resolver.register(Meta.DEFINITION, Definition.class));
			register(5, resolver.register(Meta.RECORD, Record.class));
			register(6, resolver.register(Meta.FIELD, Field.class));
			register(7, resolver.register(Meta.ELEMENT, Element.class));
			register(8, resolver.register(Meta.ARRAY, Array.class));
			register(9, resolver.register(TypeStreamEntry.STREAM_ENTRY, TypeStreamEntry.class));
			register( 10, resolver.register(Meta.INT32, int.class));
			register( 10, resolver.register(Meta.BOOLEAN, boolean.class));
		} catch (TypeException e) {
			throw new RuntimeException("Initialization error", e);
		}
	}

	public TypeContext context() {
		return context;
	}

	private TypeMapEntry register(int streamId, TypeMapEntry newEntry) throws TypeException {

		// Very simplistic lock strategy. Revisit sometime.
		synchronized (lastIdentifier) {

			if (lastIdentifier.get() < streamId ) {
				lastIdentifier.set(streamId);
			}

			// Check it isn't already registered.
			TypeMapEntry entry = typeNames.get(newEntry.typename());
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
			entry = new TypeMapEntry(streamId, newEntry.typename(), newEntry.definition(), newEntry.dataClass(), newEntry.reader(),
					newEntry.writer());
			types.put(streamId, entry);
			typeNames.put(entry.typename(), entry);

			// Atomic types will duplicate entries. First one wins.
			classes.putIfAbsent(entry.dataClass().typeClass(), entry);

			return entry;
		}
	}

	public TypeMapEntry getEntry(Typename typeName) {
		TypeMapEntry entry = typeNames.get(typeName);

		return entry;
	}

	public TypeMapEntry getEntry(Class<?> clzz) throws TypeException {
		TypeMapEntry entry = classes.get(clzz);

		return entry;
	}

	public TypeMapEntry getEntry(int streamId) {
		TypeMapEntry entry = types.get(streamId);

		return entry;
	}

	public TypeMapEntry registerStreamEntry(Typename typename) throws TypeException {
		TypeMapEntry entry = resolver.map(typename);
		if (entry == null) {
			throw new TypeException("Class not registered or defined in stream: " + typename.toString());
		}

		// Register in the typeMap. update entry value with registered value.
		return register(entry.streamId(), entry);
	}

	public void registerEntry(TypeStreamEntry def) throws TypeException {
		TypeMapEntry entry = getEntry(def.streamId());
		if (entry == null) {

			// Register the typename and definition to the context.
			// will attempt to find a native class to bind to.
			context.register(def.typename(),def.definition());

			// Generate the reader/writer.
			entry = resolver.map(def.typename());

			register(def.streamId(), entry);

		} else {
			if (!def.typename().equals(entry.typename())) {
				throw new TypeException("No match for type on stream");
			}
		}

	}

}
