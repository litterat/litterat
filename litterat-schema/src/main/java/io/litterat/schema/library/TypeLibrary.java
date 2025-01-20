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
package io.litterat.schema.library;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.schema.meta.Array;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Element;
import io.litterat.schema.meta.Field;
import io.litterat.schema.meta.Record;
import io.litterat.schema.meta.TypeName;
import io.litterat.schema.annotation.SchemaType;
import io.litterat.schema.bind.ModelBinder;
import io.litterat.schema.meta.SchemaTypes;
import io.litterat.schema.meta.TypeDefinitions;

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

	private final DataBindContext bindContext;

	private final ConcurrentMap<TypeName, TypeLibraryEntry> types;

	private final ConcurrentMap<DataClass, TypeName> classes;

	// This probably belongs somewhere else.
	private final ModelBinder binder;

	public TypeLibrary(DataBindContext bindContext) {
		this.bindContext = bindContext;
		this.types = new ConcurrentHashMap<>();
		this.classes = new ConcurrentHashMap<>();
		this.binder = new ModelBinder();

		try {

			// Atoms
			register(TypeLibrary.FLOAT, TypeDefinitions.FLOAT, bindContext.getDescriptor(float.class));
			registerAlias(TypeLibrary.FLOAT, bindContext.getDescriptor(Float.class));
			register(TypeLibrary.BOOLEAN, TypeDefinitions.BOOLEAN, bindContext.getDescriptor(boolean.class));
			register(TypeLibrary.STRING, TypeDefinitions.STRING, bindContext.getDescriptor(String.class));
			register(TypeLibrary.INT32, TypeDefinitions.INT32, bindContext.getDescriptor(int.class));
			registerAlias(TypeLibrary.INT32, bindContext.getDescriptor(Integer.class));

			// Schema types.
			register(SchemaTypes.SEQUENCE, SchemaTypes.SEQUENCE_DEF, bindContext.getDescriptor(Record.class));
			register(SchemaTypes.ELEMENT, SchemaTypes.ELEMENT_DEF, bindContext.getDescriptor(Element.class));
			register(SchemaTypes.FIELD, binder.createDefinition(this, bindContext.getDescriptor(Field.class)),
					bindContext.getDescriptor(Field.class));
			register(SchemaTypes.DEFINITION, SchemaTypes.DEFINITION_DEF, bindContext.getDescriptor(Definition.class));
			register(SchemaTypes.TYPE_NAME_DEFINITION,
					binder.createDefinition(this, bindContext.getDescriptor(TypeNameDefinition.class)),
					bindContext.getDescriptor(TypeNameDefinition.class));
			register(SchemaTypes.ARRAY, binder.createDefinition(this, bindContext.getDescriptor(Array.class)),
					bindContext.getDescriptor(Array.class));

		} catch (DataBindException | TypeException e) {
			throw new RuntimeException("Unexpected error", e);
		}
	}

	public TypeLibrary() {
		this(DataBindContext.builder().build());
	}

	public DataBindContext bindContext() {
		return bindContext;
	}

	public void reserve(TypeName type) {
		TypeLibraryEntry entry = new TypeLibraryEntry(TypeLibraryState.RESERVED, type, null, null);
		types.putIfAbsent(type, entry);
	}

	public void register(TypeName type, Definition definition, DataClass dataClass) {

		TypeLibraryEntry entry = new TypeLibraryEntry(TypeLibraryState.BOUND, type, definition, dataClass);

		types.putIfAbsent(type, entry);
		classes.putIfAbsent(dataClass, type);
	}

	/**
	 * This is here because Java has multiple classes which are represented in the schema using the same
	 * Java class. This allows mapping the class e.g. float.class and Float.class to TypeLibrary.FLOAT
	 *
	 * @param typeName
	 * @param clss
	 */
	public void registerAlias(TypeName typeName, DataClass dataClass) {
		classes.putIfAbsent(dataClass, typeName);
	}

	private TypeLibraryEntry registerOrThrow(TypeName typeName) throws TypeException {
		String className = typeName.namespace() + "." + typeName.name();
		try {

			Class<?> clss = Class.forName(className);
			try {
				DataClass dataClass = bindContext.getDescriptor(clss);
				Definition definition = binder.createDefinition(this, dataClass);

				register(typeName, definition, dataClass);

				return this.types.get(typeName);
			} catch (DataBindException e) {
				throw new TypeException("Failed to get class data", e);
			}

		} catch (ClassNotFoundException e) {
			throw new TypeException("not found: " + className);
		}
	}

	public TypeLibraryEntry getEntry(TypeName type) throws TypeException {
		TypeLibraryEntry entry = this.types.get(type);
		if (entry == null) {
			entry = registerOrThrow(type);
		}
		return entry;
	}

	public Definition getDefinition(TypeName type) throws TypeException {
		TypeLibraryEntry entry = this.types.get(type);
		if (entry == null) {
			entry = registerOrThrow(type);
		}
		return entry.definition();
	}

	public DataClass getTypeClass(TypeName type) throws TypeException {
		TypeLibraryEntry entry = this.types.get(type);
		if (entry == null) {
			entry = registerOrThrow(type);
		}
		return entry.typeClass();
	}

	public TypeName getTypeName(DataClass dataClass) throws TypeException {
		Objects.requireNonNull(dataClass, "class value required");

		TypeName typeName = this.classes.get(dataClass);
		if (typeName == null) {

			Definition definition = binder.createDefinition(this, dataClass);
			typeName = generateTypeName(dataClass.typeClass());
			register(typeName, definition, dataClass);

		}
		return typeName;
	}

	public TypeName getTypeName(Class<?> clss) throws TypeException {
		Objects.requireNonNull(clss, "class value required");

		try {
			return getTypeName(bindContext.getDescriptor(clss));
		} catch (DataBindException e) {
			throw new TypeException("Failed to class descriptor", e);
		}
	}

	private TypeName generateTypeName(Class<?> clss) {

		SchemaType data = clss.getAnnotation(SchemaType.class);
		if (data != null) {
			return new TypeName(data.namespace(), data.name());
		}

		return new TypeName(clss.getPackageName(), clss.getName().substring(clss.getName().lastIndexOf('.') + 1));
	}

}
