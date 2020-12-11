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
package io.litterat.schema.bind;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.PepException;
import io.litterat.schema.TypeException;
import io.litterat.schema.TypeLibrary;
import io.litterat.schema.types.Array;
import io.litterat.schema.types.Definition;
import io.litterat.schema.types.Field;
import io.litterat.schema.types.Record;
import io.litterat.schema.types.Reference;
import io.litterat.schema.types.TypeName;

/**
 *
 * The PEP Schema binders job is to either create a schema definition from the
 * information provided by the PepDataClass or using a schema definition, bind
 * the schema to an existing PepDataClass.
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

						TypeName typeName = library.getTypeName(component.dataClass().typeClass());

						// TODO - What about optional?

						fields.add(new Field(name, new Reference(typeName), false));

					}
				}

				Field[] finalFields = new Field[fields.size()];
				return new Record(fields.toArray(finalFields));
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
				return component.accessor();
			}
		}

		throw new PepException(
				String.format("Field '%s' not found in dataClass '%s'", field, dataClass.dataClass().getName()));
	}

	public static MethodHandle resolveFieldSetter(PepDataClass dataClass, String field) throws PepException {
		for (PepDataComponent component : dataClass.dataComponents()) {
			if (component.name().equalsIgnoreCase(field)) {
				return component.setter().orElseThrow(() -> new PepException("No setter available"));
			}
		}

		throw new PepException("Field not found");
	}

}
