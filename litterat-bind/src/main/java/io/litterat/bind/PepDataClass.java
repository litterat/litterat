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
package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 * A PepClassDescriptor provides a descriptor for a classes projected/embedded
 * pair for use in serialization libraries.
 *
 */
public class PepDataClass {

	public enum DataType {
		ATOM, TUPLE, ARRAY, BASE
	};

	// The class to be projected.
	private final Class<?> typeClass;

	// The embedded class type.
	private final Class<?> dataClass;

	// Empty constructor for data object.
	private final Optional<MethodHandle> creator;

	// Constructor for the data object.
	private final MethodHandle constructor;

	// Method handle to convert object to data object.
	private final MethodHandle toData;

	// Method handle to convert data object to target object.
	private final MethodHandle toObject;

	// All fields in the projected class.
	private final PepDataComponent[] dataComponents;

	// Target class is data. No extract/inject required.
	private final boolean isData;

	// An atom is any value that is passed through as is.
	private final boolean isAtom;

	// Target class is an array. Requires no-arg constructor.
	private final boolean isArray;

	// Base types e.g. interface or abstract classes.
	private final boolean isBase;

	public PepDataClass(Class<?> targetType, Class<?> serialType, MethodHandle creator, MethodHandle constructor,
			MethodHandle toData, MethodHandle toObject, PepDataComponent[] fields, DataType dataType) {
		this.typeClass = targetType;
		this.dataClass = serialType;
		this.dataComponents = fields;
		this.creator = Optional.ofNullable(creator);
		this.constructor = constructor;
		this.toData = toData;
		this.toObject = toObject;
		this.isData = DataType.TUPLE == dataType;
		this.isAtom = DataType.ATOM == dataType;
		this.isArray = DataType.ARRAY == dataType;
		this.isBase = DataType.BASE == dataType;
	}

	public PepDataClass(Class<?> targetType, Class<?> serialType, MethodHandle constructor, MethodHandle toData,
			MethodHandle toObject, PepDataComponent[] fields) {
		this(targetType, serialType, null, constructor, toData, toObject, fields, DataType.TUPLE);
	}

	// An Atom uses identity function for toData/toObject and construct.
	public PepDataClass(Class<?> targetType) {
		this(targetType, targetType, null, identity(targetType), identity(targetType), identity(targetType),
				new PepDataComponent[0], DataType.ATOM);
	}

	// An Atom with conversion functions. e.g. String <--> UUID
	public PepDataClass(Class<?> targetType, Class<?> dataClass, MethodHandle toData, MethodHandle toObject) {
		this(targetType, dataClass, null, identity(targetType), toData, toObject, new PepDataComponent[0],
				DataType.ATOM);
	}

	private static MethodHandle identity(Class<?> targetType) {
		return MethodHandles.identity(targetType);
	}

	/**
	 * @return The class this descriptor is for.
	 */
	public Class<?> typeClass() {
		return typeClass;
	}

	/**
	 * @return The embedded class. This may be equal to the target class.
	 */
	public Class<?> dataClass() {
		return dataClass;
	}

	public boolean isData() {
		return isData;
	}

	public boolean isAtom() {
		return isAtom;
	}

	public boolean isArray() {
		return isArray;
	}

	public boolean isBase() {
		return isBase;
	}

	public Optional<MethodHandle> creator() {
		return creator;
	}

	/**
	 * @return A MethodHandle that has the signature T embed(Object[] values).
	 */
	public MethodHandle constructor() {
		return constructor;
	}

	/**
	 * @return A MethodHandle that has the signature T embed(Object[] values).
	 */
	public MethodHandle toObject() {
		return toObject;
	}

	/**
	 * @return A MethodHandle that has the signature Object[] project(T object)
	 */
	public MethodHandle toData() {
		return toData;
	}

	/**
	 * @return The list of fields and their types returned by the embed function.
	 */
	public PepDataComponent[] dataComponents() {
		return dataComponents;
	}

}
