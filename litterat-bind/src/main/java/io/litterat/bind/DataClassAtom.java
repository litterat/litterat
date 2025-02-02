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

public class DataClassAtom extends DataClass {

	// The embedded class type.
	private final Class<?> dataClass;

	// Method handle to convert object to data object.
	// Converts from typeClass -> dataClasss.
	private final MethodHandle toData;

	// Method handle to convert data object to target object.
	// Converts from dataClass -> typeClass.
	private final MethodHandle toObject;

	public DataClassAtom(Class<?> targetType, Class<?> serialType, MethodHandle toData,
			MethodHandle toObject) {
		super(targetType);

		this.dataClass = serialType;
		this.toData = toData;
		this.toObject = toObject;
	}

	// An Atom uses identity function for toData/toObject and construct.
	public DataClassAtom(Class<?> targetType) {
		this(targetType, targetType, identity(targetType), identity(targetType));
	}

	private static MethodHandle identity(Class<?> targetType) {
		return MethodHandles.identity(targetType);
	}

	/**
	 * @return The embedded class. This may be equal to the target class.
	 */
	public Class<?> dataClass() {
		return dataClass;
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

	@Override
	public String toString() {
		return "DataClassAtom [ typeClass=" + typeClass().getName() + ", dataClass=" + dataClass.getName() + "]";
	}
}
