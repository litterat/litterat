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
package io.litterat.bind.describe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import io.litterat.bind.DataBindException;
import io.litterat.bind.Field;

public class GetSetFinder implements ComponentFinder {

	@Override
	public void findComponents(Class<?> clss, Constructor<?> constructor, List<ComponentInfo> fields)
			throws SecurityException, DataBindException {

		List<ComponentInfo> getSetList = new ArrayList<>();

		Lookup lookup = MethodHandles.publicLookup();

		// Look for public non-transient fields.
		java.lang.reflect.Field[] classFields = clss.getFields();
		for (java.lang.reflect.Field classField : classFields) {
			int modifiers = classField.getModifiers();
			if (Modifier.isPublic(modifiers) && !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers)) {

				ComponentInfo info = new ComponentInfo(classField.getName(), classField.getType());

				Field fieldAnnotation = classField.getAnnotation(Field.class);
				if (fieldAnnotation != null) {
					// Found a matching setter/getter pair, so add to fields.
					info.setField(fieldAnnotation);
				}

				try {
					info.setReadMethod(lookup.unreflectGetter(classField));
					info.setWriteMethod(lookup.unreflectSetter(classField));
				} catch (IllegalAccessException e) {
					// not expecting an error here.
					throw new SecurityException("Failed to unreflect field", e);
				}

				Type returnType = classField.getGenericType();
				if (returnType instanceof ParameterizedType) {
					info.setParamType((ParameterizedType) returnType);
				}

				getSetList.add(info);

			}
		}

		// https://stackoverflow.com/questions/5001172/java-reflection-getting-fields-and-methods-in-declaration-order/38929813
		// declared methods are in no particular order and it is not possible to discover it.
		// Many mention byte code analysis and line number tables, but that is asking for trouble when code
		// is compiled for production.
		Method[] methods = clss.getDeclaredMethods();
		for (Method method : methods) {

			// find the setters.
			if (method.getName().startsWith("set") && method.getName().length() > 3) {
				String getterName = "g" + method.getName().substring(1);

				try {
					// If this throws NoSuchMethodException we ignore field.
					Method getter = clss.getDeclaredMethod(getterName);

					String fieldName = Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);

					// Found a matching setter/getter pair, so add to fields.
					ComponentInfo info = new ComponentInfo(fieldName, getter.getReturnType());

					Field getterField = getter.getAnnotation(Field.class);
					if (getterField != null) {
						// Found a matching setter/getter pair, so add to fields.
						info.setField(getterField);
					}

					Field setterField = method.getAnnotation(Field.class);
					if (setterField != null) {
						// Found a matching setter/getter pair, so add to fields.
						info.setField(setterField);
					}

					try {
						info.setReadMethod(lookup.unreflect(getter));
						info.setWriteMethod(lookup.unreflect(method));
					} catch (IllegalAccessException e) {
						// not expecting an error here.
						throw new SecurityException("Failed to unreflect field", e);
					}

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

				String o1Name = o1.getName();
				if (o1.getField() != null && !o1.getField().name().strip().equals("")) {
					o1Name = o1.getField().name();
				}

				String o2Name = o2.getName();
				if (o2.getField() != null && !o2.getField().name().strip().equals("")) {
					o2Name = o2.getField().name();
				}

				return o1Name.compareTo(o2Name);
			}
		});

		// merge any fields already found previously
		ListIterator<ComponentInfo> iter = getSetList.listIterator();
		while (iter.hasNext()) {
			ComponentInfo field = iter.next();

			for (ComponentInfo existingField : fields) {
				if (field.getName().equalsIgnoreCase(existingField.getName())) {

					// remove from list to add.
					iter.remove();

					existingField.setWriteMethod(field.getWriteMethod());
				}
			}

		}

		// Add any remaining.
		fields.addAll(getSetList);

		// Check any private fields to see if we need to capture Field annotations.
		// After adding getters/setters because we might pick up fields that are found using immutable
		// finder.
		java.lang.reflect.Field[] privateFields = clss.getDeclaredFields();
		for (java.lang.reflect.Field classField : privateFields) {
			int modifiers = classField.getModifiers();
			if (Modifier.isPrivate(modifiers) && !Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers)) {

				Field fieldAnnotation = classField.getAnnotation(Field.class);
				if (fieldAnnotation != null) {

					ComponentInfo info = fields.stream().filter(e -> e.getName().equals(classField.getName()))
							.findFirst().orElse(null);
					if (info == null) {
						throw new DataBindException(String
								.format("Could not find matching getter/setter for field '%s'", classField.getName()));
					}

					// Found a matching setter/getter pair, so add to fields.
					info.setField(fieldAnnotation);
				}
			}
		}

	}

}
