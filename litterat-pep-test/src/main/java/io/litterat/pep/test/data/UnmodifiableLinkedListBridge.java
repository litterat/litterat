/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.pep.test.data;

import java.lang.invoke.MethodHandle;
import java.util.Collection;

import io.litterat.pep.DataBridge;

/**
 *
 * Default Collection to array bridge.
 *
 */
public class UnmodifiableLinkedListBridge implements DataBridge<Object[], Collection<?>> {

	MethodHandle collectionConstructor;

	public UnmodifiableLinkedListBridge(MethodHandle collectionConstructor) {
		this.collectionConstructor = collectionConstructor;
	}

	@Override
	public Object[] toData(Collection<?> b) {
		return b.toArray();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<?> toObject(Object[] s) {
		try {

			Collection collection = (Collection) collectionConstructor.invoke();

			for (int x = 0; x < s.length; x++) {
				collection.add(s[x]);
			}
		} catch (Throwable e) {
			throw new RuntimeException("Failed to convert to Collection");
		}
		return null;
	}

}
