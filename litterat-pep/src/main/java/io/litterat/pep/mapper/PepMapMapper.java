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
package io.litterat.pep.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.litterat.pep.PepContext;
import io.litterat.pep.PepDataArrayClass;
import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.PepException;

/**
 *
 * Sample showing how to use the Pep library to convert an Object to/from
 * Map<String,Object>
 *
 */
public class PepMapMapper {

	private final PepContext context;

	public PepMapMapper(PepContext context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> toMap(Object object) throws PepException {
		Objects.requireNonNull(object);

		return (Map<String, Object>) toMap(context.getDescriptor(object.getClass()), object);
	}

	@SuppressWarnings("unchecked")
	public Object toMap(PepDataClass dataClass, Object object) throws PepException {

		int fieldIndex = 0;

		try {
			Object v = null;
			if (dataClass.isAtom()) {
				v = dataClass.toData().invoke(object);
			} else if (dataClass.isData()) {

				Object data = dataClass.toData().invoke(object);

				Map<String, Object> map = new HashMap<>();

				PepDataComponent[] fields = dataClass.dataComponents();
				for (fieldIndex = 0; fieldIndex < dataClass.dataComponents().length; fieldIndex++) {
					PepDataComponent field = fields[fieldIndex];

					Object fv = field.accessor().invoke(data);

					// Recursively convert object to map.
					if (fv != null) {
						PepDataClass fieldDataClass = field.dataClass();
						fv = toMap(fieldDataClass, fv);
					}

					map.put(field.name(), fv);
				}

				v = map;

			} else if (dataClass.isBase()) {
				// An interface needs to know the type being written so it can be picked up by
				// the reader later.
				v = toMap(object);
				if (v instanceof Map) {
					@SuppressWarnings("rawtypes")
					Map baseMap = (Map) v;
					baseMap.put("type", object.getClass().getName());
				}
			} else if (dataClass.isArray()) {

				PepDataArrayClass arrayClass = (PepDataArrayClass) dataClass;

				Object arrayData = object;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object[] outputArray = new Object[length];
				Object iterator = arrayClass.iterator().invoke(arrayData);

				PepDataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					Object av = arrayClass.get().invoke(iterator, arrayData);
					outputArray[x] = toMap(arrayDataClass, av);
				}

				v = outputArray;
			} else {
				throw new IllegalArgumentException("Unknown data class");
			}
			return v;
		} catch (Throwable t) {
			throw new PepException(String.format("Failed to convert %s to Map. Could not convert field %s",
					dataClass.typeClass(), dataClass.dataComponents()[fieldIndex].name()), t);
		}
	}

	public Object toObject(Class<?> clss, Map<String, Object> map) throws PepException {

		Objects.requireNonNull(clss);
		Objects.requireNonNull(map);

		return toObject(context.getDescriptor(clss), map);
	}

	@SuppressWarnings("unchecked")
	public Object toObject(PepDataClass dataClass, Object data) throws PepException {

		int fieldIndex = 0;

		try {
			Object v = null;
			if (dataClass.isAtom()) {
				v = dataClass.toObject().invoke(data);
			} else if (dataClass.isData()) {
				Map<String, Object> map = (Map<String, Object>) data;
				PepDataComponent[] fields = dataClass.dataComponents();
				Object[] construct = new Object[fields.length];
				for (fieldIndex = 0; fieldIndex < dataClass.dataComponents().length; fieldIndex++) {
					PepDataComponent field = fields[fieldIndex];

					Object fv = map.get(field.name());

					// Recursively convert maps back to objects.
					if (fv != null) {
						PepDataClass fieldDataClass = field.dataClass();

						fv = toObject(fieldDataClass, fv);

					}
					construct[fieldIndex] = fv;
				}

				v = dataClass.constructor().invoke(construct);

				v = dataClass.toObject().invoke(v);
			} else if (dataClass.isArray()) {
				PepDataArrayClass arrayClass = (PepDataArrayClass) dataClass;

				Object[] inputArray = (Object[]) data;

				int length = inputArray.length;
				Object arrayData = arrayClass.constructor().invoke(length);
				Object iterator = arrayClass.iterator().invoke(arrayData);

				PepDataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					arrayClass.put().invoke(iterator, arrayData, toObject(arrayDataClass, inputArray[x]));
				}

				v = arrayData;
			} else {
				throw new IllegalArgumentException("unrecognised type");
			}
			return v;
		} catch (Throwable t) {
			if (fieldIndex < dataClass.dataComponents().length) {
				throw new PepException(String.format("Failed to convert Map to %s. Incorrect value for field %s",
						dataClass.typeClass(), dataClass.dataComponents()[fieldIndex].name()), t);
			} else {
				throw new PepException(String.format("Failed to convert Map to %s.", dataClass.typeClass()), t);
			}
		}

	}
}
