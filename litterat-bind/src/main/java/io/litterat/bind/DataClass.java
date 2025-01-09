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

/**
 * 
 * A DataClass represents the interface into data classes. This is also the parent class for
 * DataClassAtom, DataClassRecord, DatClassArray and DataClassUnion.
 *
 */
public abstract class DataClass {

	public enum DataClassType {
		ATOM, RECORD, ARRAY, UNION
	}

	// The application type data class.
	private final Class<?> typeClass;

	// The data class type.
	private final DataClassType dataClassType;

	public DataClass( Class<?> targetType, DataClassType dataType) {

		this.typeClass = targetType;

		this.dataClassType = dataType;
	}

	/**
	 * @return The class this descriptor is for.
	 */
	public Class<?> typeClass() {
		return typeClass;
	}

	public DataClassType dataClassType() {
		return dataClassType;
	}

}
