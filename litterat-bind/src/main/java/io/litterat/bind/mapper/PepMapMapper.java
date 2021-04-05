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
package io.litterat.bind.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;

/**
 *
 * Sample showing how to use the Pep library to convert an Object to/from Map<String,Object>
 *
 */
public class PepMapMapper {

	private final DataBindContext context;

	public PepMapMapper(DataBindContext context) {
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> toMap(Object object) throws DataBindException {
		Objects.requireNonNull(object);

		return (Map<String, Object>) toMap(context.getDescriptor(object.getClass()), object);
	}

	@SuppressWarnings("unchecked")
	public Object toMap(DataClass dataClass, Object object) throws DataBindException {

		int fieldIndex = 0;

		try {
			Object v = null;
			if (dataClass instanceof DataClassAtom) {
				DataClassAtom dataClassAtom = (DataClassAtom) dataClass;
				v = dataClassAtom.toData().invoke(object);
			} else if (dataClass instanceof DataClassRecord) {
				DataClassRecord dataRecord = (DataClassRecord) dataClass;
				Object data = dataRecord.toData().invoke(object);

				Map<String, Object> map = new HashMap<>();

				DataClassField[] fields = dataRecord.fields();
				for (fieldIndex = 0; fieldIndex < dataRecord.fields().length; fieldIndex++) {
					DataClassField field = fields[fieldIndex];

					if (field.isPresent(data)) {
						DataClass fieldDataClass = field.dataClass();
						Object fv = toMap(fieldDataClass, field.get(data));
						map.put(field.name(), fv);
					}
				}

				v = map;

			} else if (dataClass instanceof DataClassArray) {
				DataClassArray arrayClass = (DataClassArray) dataClass;

				Object arrayData = object;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object[] outputArray = new Object[length];
				Object iterator = arrayClass.iterator().invoke(arrayData);

				DataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					Object av = arrayClass.get().invoke(arrayData, iterator);
					outputArray[x] = toMap(arrayDataClass, av);
				}

				v = outputArray;
			} else if (dataClass instanceof DataClassUnion) {
				DataClassUnion unionClass = (DataClassUnion) dataClass;

				// Make sure this class is a member of the union before writing it.
				DataClass unionInstanceClass = context.getDescriptor(object.getClass());
				if (!unionClass.isMemberType(unionInstanceClass)) {
					throw new IllegalArgumentException(
							String.format("Class '%s' not a member of union type.", object.getClass().getName()));
				}

				// A union needs to know the type being written so it can be picked up by
				// the reader later.
				v = toMap(object);
				if (v instanceof Map) {
					@SuppressWarnings("rawtypes")
					Map baseMap = (Map) v;

					// Using the full class name here as an example. A better/more complete implementation
					// would use a schema based name.
					baseMap.put("type", object.getClass().getName());
				} else {
					throw new IllegalArgumentException("Unable to write non-record type union");
				}
			} else {
				throw new IllegalArgumentException("Unknown data class");
			}
			return v;
		} catch (Throwable t) {
			throw new DataBindException(String.format("Failed to convert %s to Map", dataClass.typeClass()), t);
		}
	}

	public Object toObject(Class<?> clss, Map<String, Object> map) throws DataBindException {

		Objects.requireNonNull(clss);
		Objects.requireNonNull(map);

		return toObject(context.getDescriptor(clss), map);
	}

	@SuppressWarnings("unchecked")
	public Object toObject(DataClass dataClass, Object data) throws DataBindException {

		int fieldIndex = 0;

		try {
			Object v = null;
			if (dataClass instanceof DataClassAtom) {
				DataClassAtom dataClassAtom = (DataClassAtom) dataClass;
				v = dataClassAtom.toObject().invoke(data);
			} else if (dataClass instanceof DataClassRecord) {
				DataClassRecord dataRecord = (DataClassRecord) dataClass;
				Map<String, Object> map = (Map<String, Object>) data;
				DataClassField[] fields = dataRecord.fields();
				Object[] construct = new Object[fields.length];
				for (fieldIndex = 0; fieldIndex < dataRecord.fields().length; fieldIndex++) {
					DataClassField field = fields[fieldIndex];

					Object fv = map.get(field.name());

					// Recursively convert maps back to objects.
					if (fv != null) {
						DataClass fieldDataClass = field.dataClass();

						fv = toObject(fieldDataClass, fv);

					}
					construct[fieldIndex] = fv;
				}

				v = dataRecord.constructor().invoke(construct);

				v = dataRecord.toObject().invoke(v);
			} else if (dataClass instanceof DataClassArray) {
				DataClassArray arrayClass = (DataClassArray) dataClass;

				Object[] inputArray = (Object[]) data;

				int length = inputArray.length;
				Object arrayData = arrayClass.constructor().invoke(length);
				Object iterator = arrayClass.iterator().invoke(arrayData);

				DataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					arrayClass.put().invoke(arrayData, iterator, toObject(arrayDataClass, inputArray[x]));
				}

				v = arrayData;
			} else if (dataClass instanceof DataClassUnion) {
				DataClassUnion unionClass = (DataClassUnion) dataClass;

				Map<String, Object> map = (Map<String, Object>) data;

				// A tagged union uses "type" for the class name.
				String type = (String) map.get("type");

				DataClass instantType = context.getDescriptor(Class.forName(type));
				if (!unionClass.isMemberType(instantType)) {
					throw new DataBindException("instance not of expected union type");
				}

				v = toObject(instantType, data);

			} else {
				throw new IllegalArgumentException("unrecognised type");
			}
			return v;
		} catch (Throwable t) {
			throw new DataBindException(String.format("Failed to convert Map to %s.", dataClass.typeClass()), t);
		}

	}
}
