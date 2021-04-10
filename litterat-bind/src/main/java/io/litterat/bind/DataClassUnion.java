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

import java.util.Arrays;

/**
 * 
 * A Union data class is tagged union type. It can be represented in a number of ways in Java:
 * 
 * <ul>
 * <li>interface: An interface allows multiple data representations as child classes.
 * <li>abstract class: Similar to an interface, an abstract or base class can have multiple data
 * representations.
 * <li>embedded union: A class with one or more fields where only one is present at any one time.
 * </ul>
 *
 */
public class DataClassUnion extends DataClass {

	private DataClass[] memberTypes;

	private boolean isSealed;

	public DataClassUnion(Class<?> targetType, DataClass[] members, boolean isSealed) {
		super(targetType, DataClassType.UNION);

		this.memberTypes = members;
		this.isSealed = isSealed;

	}

	public DataClassUnion(Class<?> targetType) {
		this(targetType, new DataClass[0], false);
	}

	public DataClass[] memberTypes() {
		return memberTypes;
	}

	public boolean isSealed() {
		return isSealed;
	}

	public boolean isMemberType(DataClass dataClass) {
		boolean found = false;

		// Get a reference to latest version as this method isn't synchronized.
		// Probably a better way to do this.
		DataClass[] types = this.memberTypes;
		for (DataClass dClass : types) {
			if (dClass.equals(dataClass)) {
				found = true;
				break;
			}
		}

		return found;
	}

	/**
	 * As different implementations of an interface or abstract class will get loaded at different times
	 * the list of union types will not all be known at startup. Therefore it needs to be possible to
	 * add additional implementations to the list. One of the reasons why sealed classes are a better
	 * choice.
	 * 
	 * @throws DataBindException
	 */
	public synchronized void addMemberType(DataClass newType) throws DataBindException {

		if (isSealed) {
			throw new DataBindException("Union type is sealed. No addition member types can be added.");
		}

		DataClass[] newMemberTypes = Arrays.copyOf(memberTypes, memberTypes.length + 1);
		newMemberTypes[newMemberTypes.length - 1] = newType;

		this.memberTypes = newMemberTypes;
	}

	public Object checkIsMember(Object value) throws DataBindException {

		if (value == null) {
			return value;
		}

		DataClass[] values = this.memberTypes;
		for (DataClass dataClass : values) {
			if (dataClass.typeClass() == value.getClass()) {
				return value;
			}
		}

		throw new DataBindException("Union value not valid for type");
	}

}
