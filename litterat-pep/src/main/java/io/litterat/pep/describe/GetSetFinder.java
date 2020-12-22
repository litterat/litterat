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
package io.litterat.pep.describe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetSetFinder implements ComponentFinder {

	@Override
	public void findComponents(Class<?> clss, Constructor<?> constructor, List<ComponentInfo> fields)
			throws SecurityException {

		List<ComponentInfo> getSetList = new ArrayList<>();

		// https://stackoverflow.com/questions/5001172/java-reflection-getting-fields-and-methods-in-declaration-order/38929813
		// declared methods are in no particular order and it is not possible to discover it.
		// Many mention byte code analysis and line number tables, but that is asking for trouble when code is compiled for production.
		Method[] methods = clss.getDeclaredMethods();
		for (Method method : methods) {

			// find the setters.
			if (method.getName().startsWith("set") && method.getName().length() > 3) {
				String getterName = "g" + method.getName().substring(1);

				try {
					Method getter = clss.getDeclaredMethod(getterName);

					String fieldName = Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);

					// Found a matching setter/getter pair, so add to fields.
					ComponentInfo info = new ComponentInfo(fieldName, getter.getReturnType());
					info.setReadMethod(getter);
					info.setWriteMethod(method);

					Type returnType = method.getGenericReturnType();
					if (returnType instanceof ParameterizedType) {
						info.setParamType((ParameterizedType) returnType);
					}

					getSetList.add(info);

				} catch (NoSuchMethodException ex) {
					// ignore this field, no matching getter.
					continue;
				}
			}
		}

		// We can find the list in different orders from Java, so sort them before
		// adding them as fields.
		Collections.sort(getSetList, new Comparator<ComponentInfo>() {

			@Override
			public int compare(ComponentInfo o1, ComponentInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		fields.addAll(getSetList);
	}

}
