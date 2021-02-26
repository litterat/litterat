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

import io.litterat.bind.PepDataClass;
import io.litterat.bind.PepDataComponent;
import io.litterat.bind.PepException;
import io.litterat.model.TypeException;
import io.litterat.model.TypeLibrary;
import io.litterat.model.types.Array;
import io.litterat.model.types.Definition;
import io.litterat.model.types.Field;
import io.litterat.model.types.Record;
import io.litterat.model.types.Reference;
import io.litterat.model.types.TypeName;

/**
 *
 * The PEP Schema binders job is to either create a schema definition from the information provided
 * by the PepDataClass or using a schema definition, bind the schema to an existing PepDataClass.
 *
 */

public class PepSchemaBinder {

	public Definition createDefinition(TypeLibrary library, Class<?> clss) throws TypeException {

		try {

			// This might throw an exception.
			PepDataClass dataClass = library.pepContext().getDescriptor(clss);

			if (dataClass.isData()) {
				List<Field> fields = new ArrayList<>();

				PepDataComponent[] components = dataClass.dataComponents();
				for (PepDataComponent component : components) {
					String name = component.name();

					if (component.dataClass().isArray()) {

						TypeName typeName = library.getTypeName(component.dataClass().typeClass().getComponentType());

						fields.add(new Field(name, new Array(typeName)));

					} else {

						TypeName typeName = library.getTypeName(component.dataClass().dataClass());

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
		} catch (PepException e) {
			throw new TypeException("Could not get data descriptor for class.", e);
		}

	}

	public static MethodHandle resolveFieldGetter(PepDataClass dataClass, String field) throws PepException {

		for (PepDataComponent component : dataClass.dataComponents()) {
			if (component.name().equalsIgnoreCase(field)) {

				// Pass the accessor through the toData handle to get the correct data type.
				// toData will be identity function if no change required.
				PepDataClass componentClass = component.dataClass();

				MethodHandle toData = componentClass.toData();

				return MethodHandles.filterArguments(toData, 0, component.accessor());
			}
		}

		throw new PepException(
				String.format("Field '%s' not found in dataClass '%s'", field, dataClass.dataClass().getName()));
	}

	public static MethodHandle resolveFieldSetter(PepDataClass dataClass, String field) throws PepException {
		for (PepDataComponent component : dataClass.dataComponents()) {
			if (component.name().equalsIgnoreCase(field)) {

				MethodHandle setter = component.setter().orElseThrow(() -> new PepException("No setter available"));

				// Pass the object through the toObject method handle to get the correct type
				// for the setter. toObject will be identity function if no change required.
				PepDataClass componentClass = component.dataClass();

				MethodHandle toObject = componentClass.toObject();

				return MethodHandles.filterArguments(setter, 0, toObject);
			}
		}

		throw new PepException("Field not found");
	}

	public static MethodHandle[] collectToObject(PepDataClass dataClass) {

		MethodHandle[] toObject = new MethodHandle[dataClass.dataComponents().length];

		PepDataComponent[] components = dataClass.dataComponents();

		for (int x = 0; x < components.length; x++) {
			PepDataComponent component = dataClass.dataComponents()[x];

			toObject[x] = component.dataClass().toObject();
		}

		return toObject;
	}

}
