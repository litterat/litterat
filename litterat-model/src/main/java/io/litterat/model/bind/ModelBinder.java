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
package io.litterat.model.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.model.Array;
import io.litterat.model.Definition;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.Reference;
import io.litterat.model.TypeException;
import io.litterat.model.TypeLibrary;
import io.litterat.model.TypeName;

/**
 *
 * The PEP Schema binders job is to either create a schema definition from the information provided
 * by the PepDataClass or using a schema definition, bind the schema to an existing PepDataClass.
 *
 */

public class ModelBinder {

	public Definition createDefinition(TypeLibrary library, Class<?> clss) throws TypeException {

		try {

			// This might throw an exception.
			DataClass dataClass = library.pepContext().getDescriptor(clss);

			if (dataClass.isRecord()) {
				DataClassRecord dataClassRecord = (DataClassRecord) dataClass;
				List<Field> fields = new ArrayList<>();

				DataClassField[] dataFields = dataClassRecord.fields();
				for (DataClassField dataClassField : dataFields) {
					String name = dataClassField.name();

					DataClass fieldDataClass = dataClassField.dataClass();
					if (fieldDataClass.isArray()) {

						TypeName typeName = library
								.getTypeName(dataClassField.dataClass().typeClass().getComponentType());

						fields.add(new Field(name, new Array(typeName)));

					} else if (fieldDataClass.isAtom() || fieldDataClass.isRecord()) {
						DataClassAtom fieldDataClassAtom = (DataClassAtom) fieldDataClass;

						TypeName typeName = library.getTypeName(fieldDataClassAtom.dataClass());

						// TODO - What about optional?

						fields.add(new Field(name, new Reference(typeName), false));

					} else if (fieldDataClass.isUnion()) {
						DataClassUnion fieldDataClassUnion = (DataClassUnion) fieldDataClass;

						TypeName typeName = library.getTypeName(fieldDataClassUnion.typeClass());

						// TODO - What about optional?

						fields.add(new Field(name, new Reference(typeName), false));
					}
				}

				Field[] finalFields = new Field[fields.size()];
				return new Record(fields.toArray(finalFields));
			} else if (dataClass.isAtom()) {
				throw new TypeException("Could not generate descriptor for " + clss.getName());
			} else {
				throw new TypeException("Could not generate descriptor for " + clss.getName());
			}
		} catch (DataBindException e) {
			throw new TypeException("Could not get data descriptor for class.", e);
		}

	}

	public static MethodHandle resolveFieldGetter(DataClassRecord dataClass, String field) throws DataBindException {

		for (DataClassField component : dataClass.fields()) {
			if (component.name().equalsIgnoreCase(field)) {

				// Pass the accessor through the toData handle to get the correct data type.
				// toData will be identity function if no change required.
				DataClass componentClass = component.dataClass();
				if (componentClass instanceof DataClassAtom || componentClass instanceof DataClassRecord) {
					MethodHandle toData = ((DataClassAtom) componentClass).toData();

					return MethodHandles.filterArguments(toData, 0, component.accessor());
				} else {
					return component.accessor();
				}
			}
		}

		throw new DataBindException(
				String.format("Field '%s' not found in dataClass '%s'", field, dataClass.dataClass().getName()));
	}

	public static MethodHandle resolveFieldSetter(DataClassRecord dataClass, String field) throws DataBindException {
		for (DataClassField component : dataClass.fields()) {
			if (component.name().equalsIgnoreCase(field)) {

				MethodHandle setter = component.setter()
						.orElseThrow(() -> new DataBindException("No setter available"));

				// Pass the object through the toObject method handle to get the correct type
				// for the setter. toObject will be identity function if no change required.
				DataClass componentClass = component.dataClass();
				if (componentClass instanceof DataClassAtom || componentClass instanceof DataClassRecord) {
					MethodHandle toObject = ((DataClassAtom) componentClass).toObject();

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

			if (fieldDataClass.isAtom() || fieldDataClass.isRecord()) {
				DataClassAtom fieldAtom = (DataClassAtom) fieldDataClass;
				toObject[x] = fieldAtom.toObject();
			} else {
				toObject[x] = MethodHandles.identity(field.dataClass().typeClass());
			}
		}

		return toObject;
	}

}
