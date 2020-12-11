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
package io.litterat.xpl.lang;

import io.litterat.xpl.TypeMap;

public class LitteratMachine {

	public static final int VAR_TRANSPORT = 0;

	private final TypeMap typeMap;

	private final Class<?>[] slotTypes;
	private final Object[] slotValues;

	public LitteratMachine(TypeMap typeMap, Class<?>[] slotTypes) {
		this.typeMap = typeMap;

		this.slotTypes = slotTypes;
		this.slotValues = new Object[slotTypes.length];
	}

	public TypeMap typeMap() {
		return typeMap;
	}

	public void setVariable(int slot, Object value) {
		this.slotValues[slot] = value;
	}

	public Object getVariable(int slot) {
		return this.slotValues[slot];
	}

	public Class<?> getVariableType(int slot) {
		return this.slotTypes[slot];
	}

}
