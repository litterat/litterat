/*
 * Copyright (c) 2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.bind.describe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 
 * This uses MethodHandles reflection to see if new Java features are available to be checked. In
 * particular we're looking for Records and Sealed Interfaces/Classes.
 *
 */
public class NewFeatures {

	MethodHandle isRecord;
	MethodHandle isSealed;

	public NewFeatures() {
		isRecord = getBooleanMethod("isRecord");
		isSealed = getBooleanMethod("isSealed");
	}

	private MethodHandle getBooleanMethod(String methodName) {
		MethodHandle method;

		// Get the Class class.
		Class<?> classClass = Class.class;

		try {
			method = MethodHandles.publicLookup().findVirtual(classClass, methodName,
					MethodType.methodType(boolean.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			method = MethodHandles.constant(boolean.class, false);

			method = MethodHandles.dropArguments(isRecord, 0, Class.class);
		}

		return method;
	}

	public boolean isRecord(Class<?> clss) {
		try {
			return (boolean) isRecord.invoke(clss);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isSealed(Class<?> clss) {
		try {
			return (boolean) isSealed.invoke(clss);
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
}
