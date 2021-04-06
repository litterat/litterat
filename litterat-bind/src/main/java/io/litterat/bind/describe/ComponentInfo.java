/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.litterat.bind.describe;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.ParameterizedType;

import io.litterat.bind.DataBindException;
import io.litterat.bind.Field;

/**
 * Placeholder to collect information about field details. Used as a temporary hold during the
 * resolving process.
 */
public class ComponentInfo {

	private String name;

	private final Class<?> type;

	private MethodHandle writeMethod;

	private MethodHandle readMethod;

	private int constructorArgument;

	private ParameterizedType paramType;

	private Field field;

	public ComponentInfo(String name, Class<?> type) {
		this.name = name;
		this.type = type;
		this.constructorArgument = -1;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setField(Field field) throws DataBindException {
		if (this.field != null && this.field != field)
			throw new DataBindException(String.format("Field '%s' already has Field annotation", this.name));
		this.field = field;
	}

	public Field getField() {
		return this.field;
	}

	public Class<?> getType() {
		return type;
	}

	public MethodHandle getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(MethodHandle writeMethod) {
		this.writeMethod = writeMethod;
	}

	public MethodHandle getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(MethodHandle readMethod) {
		this.readMethod = readMethod;
	}

	public int getConstructorArgument() {
		return constructorArgument;
	}

	public void setConstructorArgument(int constructorArgument) {
		this.constructorArgument = constructorArgument;
	}

	public ParameterizedType getParamType() {
		return paramType;
	}

	public void setParamType(ParameterizedType paramType) {
		this.paramType = paramType;
	}

}
