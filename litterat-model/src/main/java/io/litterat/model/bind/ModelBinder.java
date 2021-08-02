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

import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.model.Array;
import io.litterat.model.Definition;
import io.litterat.model.Element;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.TypeName;
import io.litterat.model.Union;
import io.litterat.model.library.TypeException;
import io.litterat.model.library.TypeLibrary;
import io.litterat.model.meta.TypeDefinitions;

/**
 *
 * The Litterat model binder creates a model definition from a DataClass. This allows 'code first'
 * schema development.
 * 
 * Another task that is required to to bind a @DataClass to a Litterat model @Definition. In this
 * case it should attempt to match as closely as possible to the @DataClass and create any
 * additional filters required to bind successfully to the @DataClass. If no binding can be found
 * then an exception should be thrown.
 *
 */

public class ModelBinder {

	public Element elementFromDataClass(TypeLibrary library, DataClass dataClass) throws TypeException {

		Element result = null;

		if (dataClass instanceof DataClassRecord) {
			DataClassRecord dataClassRecord = (DataClassRecord) dataClass;

			List<Field> fields = new ArrayList<>();

			DataClassField[] dataFields = dataClassRecord.fields();
			for (DataClassField dataClassField : dataFields) {
				String name = dataClassField.name();

				DataClass fieldDataClass = dataClassField.dataClass();

				if (fieldDataClass instanceof DataClassArray) {
					DataClassArray dataClassArray = (DataClassArray) fieldDataClass;

					TypeName typeName = library.getTypeName(dataClassArray.arrayDataClass());

					fields.add(new Field(name, new Array(typeName)));

				} else if (fieldDataClass instanceof DataClassAtom || fieldDataClass instanceof DataClassRecord
						|| fieldDataClass instanceof DataClassUnion) {
					// This currently assumes that any atom,record,or union will have its own type name rather than
					// be an embedded type.
					TypeName typeName = library.getTypeName(fieldDataClass);

					// TODO - What about required?

					fields.add(new Field(name, typeName, false));
				}

			}

			Field[] finalFields = new Field[fields.size()];
			result = new Record(fields.toArray(finalFields));

		} else if (dataClass instanceof DataClassUnion) {
			DataClassUnion dataClassUnion = (DataClassUnion) dataClass;

			TypeName[] members = new TypeName[dataClassUnion.memberTypes().length];
			for (int x = 0; x < members.length; x++) {
				members[x] = library.getTypeName(dataClassUnion.memberTypes()[x]);
			}

			result = new Union(members);

		} else if (dataClass instanceof DataClassArray) {
			DataClassArray dataClassArray = (DataClassArray) dataClass;
			// TODO implement this.

			TypeName arrayElementType = library.getTypeName(dataClassArray.arrayDataClass());

			result = new Array(arrayElementType);

		} else if (dataClass instanceof DataClassAtom) {
			@SuppressWarnings("unused")
			DataClassAtom dataClassAtom = (DataClassAtom) dataClass;

			// Atomic types need to be already registered.
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		} else {
			throw new TypeException("Could not generate descriptor for " + dataClass.typeClass().getName());
		}

		return result;

	}

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

					TypeName typeName = library.getTypeName(dataClassArray.arrayDataClass());

					fields.add(new Field(name, new Array(typeName)));

				} else if (fieldDataClass instanceof DataClassAtom) {
					DataClassAtom fieldDataClassAtom = (DataClassAtom) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassAtom);

					// TODO - What about required?

					fields.add(new Field(name, typeName, false));
				} else if (fieldDataClass instanceof DataClassRecord) {
					DataClassRecord fieldDataClassRecord = (DataClassRecord) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassRecord);

					// TODO - What about required?

					fields.add(new Field(name, typeName, false));
				} else if (fieldDataClass instanceof DataClassUnion) {
					DataClassUnion fieldDataClassUnion = (DataClassUnion) fieldDataClass;

					TypeName typeName = library.getTypeName(fieldDataClassUnion);

					// TODO - What about required?

					fields.add(new Field(name, typeName, false));
				}
			}

			Field[] finalFields = new Field[fields.size()];
			return new Record(fields.toArray(finalFields));
		} else if (dataClass instanceof DataClassAtom) {

			// TODO this needs more work.
			DataClassAtom dataClassAtom = (DataClassAtom) dataClass;
			if (dataClassAtom.dataClass() == String.class) {
				return TypeDefinitions.STRING;
			} else if (dataClassAtom.dataClass() == int.class) {
				return TypeDefinitions.INT32;
			}
			System.out.println("dataClassAtom:" + dataClassAtom);

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

}
