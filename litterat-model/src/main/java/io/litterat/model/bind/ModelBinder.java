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
package io.litterat.model.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.model.Array;
import io.litterat.model.Definition;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.Reference;
import io.litterat.model.TypeName;
import io.litterat.model.library.TypeException;
import io.litterat.model.library.TypeLibrary;

/**
 *
 * The Litterat model binders job is to either create a schema definition from the information provided
 * by the DataClass or using a schema definition, bind the schema to an existing DataClass.
 *
 */

public class ModelBinder {

	/*
	 *
	 * TODO this needs to be rewritten.
	 */
	public Definition createDefinition(TypeLibrary library, DataClass dataClass) throws TypeException {

		if (dataClass instanceof DataClassRecord) {
			DataClassRecord dataClassRecord = (DataClassRecord) dataClass;
			List<Field> fields = new ArrayList<>();

			DataClassField[] dataFields = dataClassRecord.fields();
			for (DataClassField dataClassField : dataFields) {
				String name = dataClassField.name();

				DataClass fieldDataClass = dataClassField.dataClass();
				if (fieldDataClass instanceof DataClassArray) {
					DataClassArray dataClassArray = (DataClassArray) fieldDataClass;

					TypeName typeName = library.getTypeName(dataClassArray.arrayDataClass().typeClass());

					fields.add(new Field(name, new Array(typeName)));

				} else if (fieldDataClass instanceof DataClassAtom) {
					DataClassAtom fieldDataClassAtom = (DataClassAtom) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassAtom.dataClass());

					// TODO - What about required?

					fields.add(new Field(name, new Reference(typeName), false));
				} else if (fieldDataClass instanceof DataClassRecord) {
					DataClassRecord fieldDataClassRecord = (DataClassRecord) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassRecord.dataClass());

					// TODO - What about required?

					fields.add(new Field(name, new Reference(typeName), false));
				} else if (fieldDataClass instanceof DataClassUnion) {
					DataClassUnion fieldDataClassUnion = (DataClassUnion) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassUnion.typeClass());

					// TODO - What about required?

					fields.add(new Field(name, new Reference(typeName), false));
				}
			}

			Field[] finalFields = new Field[fields.size()];
			return new Record(fields.toArray(finalFields));
		} else if (dataClass instanceof DataClassAtom) {
			// TODO rewrite this
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		} else if (dataClass instanceof DataClassArray) {
			// TODO implement this.
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		} else if (dataClass instanceof DataClassUnion) {
			// TODO implement this.
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		} else {
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		}

	}

	public static MethodHandle resolveFieldGetter(DataClassRecord dataClass, String fieldName)
			throws DataBindException {

		for (DataClassField dataField : dataClass.fields()) {
			if (dataField.name().equalsIgnoreCase(fieldName)) {

				MethodHandle toData = null;
				// Pass the accessor through the toData handle to get the correct data type.
				// toData will be identity function if no change required.
				DataClass componentClass = dataField.dataClass();
				if (componentClass instanceof DataClassAtom) {
					DataClassAtom dataClassAtom = (DataClassAtom) componentClass;

					toData = MethodHandles.filterArguments(dataClassAtom.toData(), 0, dataField.accessor());

					if (!dataClassAtom.dataClass().isPrimitive()) {
						toData = checkIsPresent(dataClass, dataField, toData, dataClassAtom.dataClass());
					}

				} else if (componentClass instanceof DataClassRecord) {

					DataClassRecord dataClassRecord = (DataClassRecord) componentClass;

					MethodHandle fieldToData = dataClassRecord.toData();

					toData = MethodHandles.filterArguments(fieldToData, 0, dataField.accessor());

					if (!dataClassRecord.dataClass().isPrimitive()) {
						toData = checkIsPresent(dataClass, dataField, toData, dataClassRecord.dataClass());
					}

				} else {
					toData = dataField.accessor();
				}

				return toData;

			}
		}

		throw new DataBindException(
				String.format("Field '%s' not found in dataClass '%s'", fieldName, dataClass.dataClass().getName()));
	}

	// If the value is primitive don't wrap it in guard with test. This is mainly because
	// MethodHandles.constant will throw a NPE for primitives. Also relates to a bigger issue
	// of design of XPL passing nulls around which will need to be refactored to remove.
	// TODO refactor so that design of interpreter doesn't need to pass around nulls.
	private static MethodHandle checkIsPresent(DataClassRecord dataClass, DataClassField dataField, MethodHandle toData,
			Class<?> fieldDataClass) {
		// Check for is a value is present prior to reading.
		MethodHandle isPresent = dataField.isPresent();

		MethodHandle returnNull = MethodHandles.constant(fieldDataClass, null);
		returnNull = MethodHandles.dropArguments(returnNull, 0, dataClass.typeClass());

		return MethodHandles.guardWithTest(isPresent, toData, returnNull);
	}

	public static MethodHandle resolveFieldSetter(DataClassRecord dataClass, String field) throws DataBindException {
		for (DataClassField component : dataClass.fields()) {
			if (component.name().equalsIgnoreCase(field)) {

				MethodHandle setter = component.setter()
						.orElseThrow(() -> new DataBindException("No setter available"));

				// Pass the object through the toObject method handle to get the correct type
				// for the setter. toObject will be identity function if no change required.
				DataClass componentClass = component.dataClass();
				if (componentClass instanceof DataClassAtom) {
					MethodHandle toObject = ((DataClassAtom) componentClass).toObject();

					return MethodHandles.filterArguments(setter, 0, toObject);
				} else if (componentClass instanceof DataClassRecord) {
					MethodHandle toObject = ((DataClassRecord) componentClass).toObject();

					return MethodHandles.filterArguments(setter, 0, toObject);
				} else {
					return setter;
				}
			}
		}

		throw new DataBindException("Field not found");
	}

	public static MethodHandle[] collectToObject(DataClassRecord dataClass) {

		MethodHandle[] toObject = new MethodHandle[dataClass.fields().length];

		DataClassField[] fields = dataClass.fields();

		for (int x = 0; x < fields.length; x++) {
			DataClassField field = dataClass.fields()[x];
			DataClass fieldDataClass = field.dataClass();

			if (fieldDataClass instanceof DataClassAtom) {
				DataClassAtom fieldAtom = (DataClassAtom) fieldDataClass;
				toObject[x] = fieldAtom.toObject();
			} else if (fieldDataClass instanceof DataClassRecord) {
				DataClassRecord fieldRecord = (DataClassRecord) fieldDataClass;
				toObject[x] = fieldRecord.toObject();
			} else {
				toObject[x] = MethodHandles.identity(field.dataClass().typeClass());
			}
		}

		return toObject;
	}

}
