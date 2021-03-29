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

	private DataClass[] componentTypes;

	public DataClassUnion(Class<?> targetType) {
		super(targetType, DataClassType.UNION);

		componentTypes = null;
	}

	public DataClass[] components() {
		return componentTypes;
	}

	/**
	 * As different implementations of an interface or abstract class will get loaded at different times
	 * the list of union types will not all be known at startup. Therefore it needs to be possible to
	 * add additional implementations to the list. One of the reasons why sealed classes are a better
	 * choice.
	 */
	public synchronized void addDataClass(DataClass unionClass) {

	}

}
