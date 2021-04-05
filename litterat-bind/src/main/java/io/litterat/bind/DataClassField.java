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
package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.util.Optional;

/**
 * 
 * This is analogous to the Java reflection RecordComponent class but for Data classes.
 *
 */
public class DataClassField {

	// constructor index.
	private final int index;

	// name of the field
	private final String name;

	// type of the field
	private final Class<?> type;

	private final DataClass dataClass;

	private final MethodHandle isPresent;

	// accessor read handle. signature: type t = object.getT();
	private final MethodHandle accessor;

	// setter write handle. signature object.setT( type t);
	private final Optional<MethodHandle> setter;

	public DataClassField(int index, String name, Class<?> type, DataClass dataClass, MethodHandle isPresent,
			MethodHandle readHandle, MethodHandle setter) {
		this.index = index;
		this.name = name;
		this.type = type;
		this.dataClass = dataClass;
		this.isPresent = isPresent;
		this.accessor = readHandle;
		this.setter = Optional.ofNullable(setter);
	}

	public int index() {
		return index;
	}

	public String name() {
		return name;
	}

	public Class<?> type() {
		return type;
	}

	public DataClass dataClass() {
		return dataClass;
	}

	public MethodHandle isPresent() {
		return isPresent;
	}

	public boolean isPresent(Object record) {
		try {
			return (boolean) isPresent.invoke(record);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public MethodHandle accessor() {
		return accessor;
	}

	public Object get(Object record) {
		try {
			return accessor.invoke(record);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public Optional<MethodHandle> setter() {
		return setter;
	}

	public void set(Object record, Object value) {
		try {
			setter.get().invoke(record, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
