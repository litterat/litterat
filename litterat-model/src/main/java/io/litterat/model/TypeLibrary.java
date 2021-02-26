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
package io.litterat.model;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.litterat.bind.PepContext;
import io.litterat.bind.PepDataClass;
import io.litterat.bind.PepException;
import io.litterat.model.annotation.SchemaType;
import io.litterat.model.bind.PepSchemaBinder;
import io.litterat.model.meta.SchemaTypes;
import io.litterat.model.meta.TypeDefinitions;
import io.litterat.model.types.Array;
import io.litterat.model.types.Definition;
import io.litterat.model.types.Element;
import io.litterat.model.types.Field;
import io.litterat.model.types.Record;
import io.litterat.model.types.Reference;
import io.litterat.model.types.TypeName;
import io.litterat.model.types.TypeNameDefinition;

public class TypeLibrary {

	public static final TypeName VOID = new TypeName("void");

	public static final TypeName BOOLEAN = new TypeName("boolean");

	public static final TypeName INT8 = new TypeName("int8");
	public static final TypeName UINT8 = new TypeName("uint8");

	public static final TypeName INT16 = new TypeName("int16");
	public static final TypeName UINT16 = new TypeName("uint16");
	public static final TypeName LE_INT16 = new TypeName("le_int16");
	public static final TypeName LE_UINT16 = new TypeName("le_uint16");

	public static final TypeName INT32 = new TypeName("int32");
	public static final TypeName UINT32 = new TypeName("uint32");
	public static final TypeName LE_INT32 = new TypeName("le_int32");
	public static final TypeName LE_UINT32 = new TypeName("le_uint32");

	public static final TypeName INT64 = new TypeName("int64");
	public static final TypeName UINT64 = new TypeName("uint64");
	public static final TypeName LE_INT64 = new TypeName("le_int64");
	public static final TypeName LE_UINT64 = new TypeName("le_uint64");

	public static final TypeName VARINT32 = new TypeName("varint32");
	public static final TypeName UVARINT32 = new TypeName("uvarint32");

	public static final TypeName VARINT64 = new TypeName("varint64");
	public static final TypeName UVARINT64 = new TypeName("uvarint64");

	public static final TypeName FLOAT = new TypeName("float");
	public static final TypeName DOUBLE = new TypeName("double");

	public static final TypeName STRING = new TypeName("string");

	private final PepContext pepContext;

	private final ConcurrentMap<TypeName, TypeLibraryEntry> types;

	private final ConcurrentMap<Class<?>, TypeName> classes;

	// This probably belongs somewhere else.
	private final PepSchemaBinder binder;

	public TypeLibrary(PepContext pepContext) {
		this.pepContext = pepContext;
		this.types = new ConcurrentHashMap<>();
		this.classes = new ConcurrentHashMap<>();
		this.binder = new PepSchemaBinder();

		try {

			// Atoms
			register(TypeLibrary.FLOAT, TypeDefinitions.FLOAT, pepContext.getDescriptor(float.class));
			registerAlias(TypeLibrary.FLOAT, Float.class);
			register(TypeLibrary.BOOLEAN, TypeDefinitions.BOOLEAN, pepContext.getDescriptor(boolean.class));
			register(TypeLibrary.STRING, TypeDefinitions.STRING, pepContext.getDescriptor(String.class));
			register(TypeLibrary.INT32, TypeDefinitions.INT32, pepContext.getDescriptor(int.class));
			registerAlias(TypeLibrary.INT32, Integer.class);

			// Schema types.
			register(SchemaTypes.SEQUENCE, SchemaTypes.SEQUENCE_DEF, pepContext.getDescriptor(Record.class));
			register(SchemaTypes.ELEMENT, SchemaTypes.ELEMENT_DEF, pepContext.getDescriptor(Element.class));
			register(SchemaTypes.FIELD, binder.createDefinition(this, Field.class),
					pepContext.getDescriptor(Field.class));
			register(SchemaTypes.DEFINITION, SchemaTypes.DEFINITION_DEF, pepContext.getDescriptor(Definition.class));
			register(SchemaTypes.TYPE_NAME_DEFINITION, binder.createDefinition(this, TypeNameDefinition.class),
					pepContext.getDescriptor(TypeNameDefinition.class));
			register(SchemaTypes.REFERENCE, binder.createDefinition(this, Reference.class),
					pepContext.getDescriptor(Reference.class));
			register(SchemaTypes.ARRAY, binder.createDefinition(this, Array.class),
					pepContext.getDescriptor(Array.class));

		} catch (PepException | TypeException e) {
			throw new RuntimeException("Unexpected error", e);
		}
	}

	public TypeLibrary() {
		this(PepContext.builder().build());
	}

	public PepContext pepContext() {
		return pepContext;
	}

	public void reserve(TypeName type) {
		TypeLibraryEntry entry = new TypeLibraryEntry(TypeLibraryState.RESERVED, type, null, null);
		types.putIfAbsent(type, entry);
	}

	public void register(TypeName type, Definition definition, PepDataClass dataClass) {

		TypeLibraryEntry entry = new TypeLibraryEntry(TypeLibraryState.BOUND, type, definition, dataClass);

		types.putIfAbsent(type, entry);
		classes.putIfAbsent(dataClass.dataClass(), type);
	}

	/**
	 * This is here because Java has multiple classes which are represented in the schema using the same
	 * Java class. This allows mapping the class e.g. float.class and Float.class to TypeLibrary.FLOAT
	 *
	 * @param typeName
	 * @param clss
	 */
	public void registerAlias(TypeName typeName, Class<?> clss) {
		classes.putIfAbsent(clss, typeName);
	}

	private TypeLibraryEntry registerOrThrow(TypeName typeName) throws TypeException {
		String className = typeName.namespace() + "." + typeName.name();
		try {

			Class<?> clss = Class.forName(className);
			try {
				PepDataClass dataClass = pepContext.getDescriptor(clss);
				Definition definition = binder.createDefinition(this, clss);

				register(typeName, definition, dataClass);

				return this.types.get(typeName);
			} catch (PepException e) {
				throw new TypeException("Failed to get class data", e);
			}

		} catch (ClassNotFoundException e) {
			throw new TypeException("not found: " + className);
		}
	}

	public Definition getDefinition(TypeName type) throws TypeException {
		TypeLibraryEntry entry = this.types.get(type);
		if (entry == null) {
			entry = registerOrThrow(type);
		}
		return entry.definition();
	}

	public PepDataClass getTypeClass(TypeName type) throws TypeException {
		TypeLibraryEntry entry = this.types.get(type);
		if (entry == null) {
			entry = registerOrThrow(type);
		}
		return entry.typeClass();
	}

	public TypeName getTypeName(Class<?> clss) throws TypeException {
		Objects.requireNonNull(clss, "class value required");

		TypeName typeName = this.classes.get(clss);
		if (typeName == null) {

			try {
				PepDataClass dataClass = pepContext.getDescriptor(clss);
				Definition definition = binder.createDefinition(this, clss);
				typeName = generateTypeName(clss);
				register(typeName, definition, dataClass);
			} catch (PepException e) {
				throw new TypeException("Failed to get class data", e);
			}

		}
		return typeName;
	}

	private TypeName generateTypeName(Class<?> clss) {

		// TODO this should be part of Pep.
		SchemaType data = clss.getAnnotation(SchemaType.class);
		if (data != null) {
			return new TypeName(data.namespace(), data.name());
		}

		return new TypeName(clss.getPackageName(), clss.getName().substring(clss.getName().lastIndexOf('.') + 1));
	}

}
