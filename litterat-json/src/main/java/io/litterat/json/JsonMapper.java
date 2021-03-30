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
package io.litterat.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.json.parser.JsonReader;
import io.litterat.json.parser.JsonToken;
import io.litterat.json.parser.JsonWriter;

public class JsonMapper {
	private final DataBindContext context;

	public static String toJson(Object object) throws DataBindException {
		StringWriter writer = new StringWriter();
		JsonMapper mapper = new JsonMapper();
		mapper.toJson(object, writer);
		return writer.toString();
	}

	public static <T> T fromJson(String json, Class<?> clss) throws DataBindException {
		StringReader reader = new StringReader(json);
		JsonMapper mapper = new JsonMapper();
		return mapper.fromJson(json, clss, reader);
	}

	public JsonMapper(DataBindContext context) {
		this.context = context;
	}

	public JsonMapper() {
		this(DataBindContext.builder().build());
	}

	public void toJson(Object object, Writer writer) throws DataBindException {
		DataClass dataClass = context.getDescriptor(object.getClass());
		toJson(object, dataClass, new JsonWriter(writer));
	}

	private void toJson(Object object, DataClass dataClass, JsonWriter writer) throws DataBindException {

		Objects.requireNonNull(object);

		int fieldIndex = 0;

		try {

			if (dataClass instanceof DataClassAtom) {
				DataClassAtom dataClassAtom = (DataClassAtom) dataClass;

				Object data = dataClassAtom.toData().invoke(object);

				writeAtom(data, writer);
			} else if (dataClass instanceof DataClassRecord) {
				DataClassRecord dataClassRecord = (DataClassRecord) dataClass;

				Object data = dataClassRecord.toData().invoke(object);

				writer.beginObject();

				DataClassField[] fields = dataClassRecord.fields();

				for (fieldIndex = 0; fieldIndex < dataClassRecord.fields().length; fieldIndex++) {
					DataClassField field = fields[fieldIndex];

					Object v = field.accessor().invoke(data);

					// Recursively convert object to map.
					if (v != null) {
						DataClass fieldDataClass = field.dataClass();

						writer.name(field.name());

						toJson(v, fieldDataClass, writer);
					}

				}

				writer.endObject();
			} else if (dataClass instanceof DataClassArray) {
				DataClassArray arrayClass = (DataClassArray) dataClass;

				writer.beginArray();

				Object arrayData = object;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object iterator = arrayClass.iterator().invoke(arrayData);
				DataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					Object av = arrayClass.get().invoke(arrayData, iterator);
					toJson(av, arrayDataClass, writer);
				}

				writer.endArray();
			} else if (dataClass instanceof DataClassUnion) {
				throw new DataBindException("union not implemented");
			} else {
				throw new DataBindException("unexpected data class type");
			}

		} catch (Throwable t) {
			throw new DataBindException(String.format("Failed to convert %s to Map.", dataClass.typeClass()), t);
		}
	}

	private void writeAtom(Object object, JsonWriter writer) throws IOException {

		if (object == null) {
			writer.nullValue();
		} else if (object instanceof String) {
			writer.value((String) object);
		} else if (object instanceof Number) {
			writer.value((Number) object);
		} else if (object instanceof Boolean) {
			writer.value((Boolean) object);
		} else if (object instanceof Character) {
			writer.value(object.toString());
		} else {
			throw new IOException(String.format("Could not write atom: %s", object.getClass()));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(String json, Class<?> clss, Reader reader) throws DataBindException {

		DataClass dataClass = context.getDescriptor(clss);

		return (T) fromJson(dataClass, new JsonReader(reader));
	}

	private Map<DataClassRecord, Map<String, DataClassField>> fieldMaps = new HashMap<>();

	private Map<String, DataClassField> componentMap(DataClassRecord dataClass) {
		return fieldMaps.computeIfAbsent(dataClass, (clss) -> {
			return Arrays.asList(clss.fields()).stream().collect(Collectors.toMap(DataClassField::name, item -> item));
		});

	}

	private Object fromJson(DataClass dataClass, JsonReader reader) throws DataBindException {

		try {

			JsonToken token = reader.peek();
			switch (token) {

			case NUMBER:
				// This needs to be more defensive. Currently assumes NUMBER, STRING and BOOLEAN are Atoms.
				DataClassAtom dataClassAtom = (DataClassAtom) dataClass;
				return dataClassAtom.toObject().invoke(readNumber(dataClassAtom, reader));

			case STRING:
				dataClassAtom = (DataClassAtom) dataClass;
				String v = reader.nextString();
				if (dataClassAtom.dataClass() == String.class) {
					return dataClassAtom.toObject().invoke(v);
				} else if (dataClassAtom.dataClass() == Character.class) {
					Character c = Character.valueOf(v.toCharArray()[0]);
					return dataClassAtom.toObject().invoke(c);
				} else if (dataClassAtom.dataClass() == char.class) {
					char c = v.toCharArray()[0];
					return dataClassAtom.toObject().invoke(c);
				}
				throw new DataBindException(
						String.format("Could not convert string to %s", dataClassAtom.dataClass().getName()));
			case BOOLEAN:
				dataClassAtom = (DataClassAtom) dataClass;
				if (dataClassAtom.dataClass() == boolean.class || dataClassAtom.dataClass() == Boolean.class) {
					return dataClassAtom.toObject().invoke(reader.nextBoolean());
				}
				throw new DataBindException(
						String.format("Could not convert boolean to %s", dataClassAtom.dataClass().getName()));
			case NULL:
				reader.nextNull();
				return null;

			case BEGIN_OBJECT:

				if (!(dataClass instanceof DataClassRecord || dataClass instanceof DataClassUnion)) {
					throw new IllegalStateException();
				}

				reader.beginObject();

				// For a union, we need to know which type has been embedded.
				// For JSON, this is the only time additional information needs to be added to the format
				// to support the data model.
				if (dataClass instanceof DataClassUnion) {
					String name = reader.nextName();
					if (!name.equalsIgnoreCase("type")) {
						// The other option here is to try and pattern match the field names against the record models.
						// That doesn't work great for a stream/token based reader like this.
						// Check https://serde.rs/enum-representations.html for future implementation options.
						throw new DataBindException("union expected a type");
					}
				}

				DataClassRecord dataClassRecord = (DataClassRecord) dataClass;

				DataClassField[] fields = dataClassRecord.fields();
				Object[] construct = new Object[fields.length];

				while (reader.hasNext()) {

					String name = reader.nextName();

					// Find the name in the fields.
					DataClassField field = componentMap(dataClassRecord).get(name);
					Objects.requireNonNull(field,
							String.format("field %s not found in class", name, dataClassRecord.dataClass()));

					construct[field.index()] = fromJson(field.dataClass(), reader);

				}

				reader.endObject();

				Object data = dataClassRecord.constructor().invoke(construct);

				return dataClassRecord.toObject().invoke(data);

			case BEGIN_ARRAY:

				// JSON schema allows embedding a record inside an array. This type of record flattenning is not
				// supported.
				if (!(dataClass instanceof DataClassArray)) {
					throw new IllegalStateException();
				}

				reader.beginArray();

				DataClassArray arrayDataClass = (DataClassArray) dataClass;

				// Token based reader needs to read everything in the array before creating.
				// TODO could potentially provide meta data saying if the collection type is
				// static or dynamic.
				// A dynamic sized collection could be created and then have each added.
				DataClass arrayValueClass = arrayDataClass.arrayDataClass();
				List<Object> list = new ArrayList<Object>();
				while (reader.hasNext()) {
					list.add(fromJson(arrayValueClass, reader));
				}

				reader.endArray();

				// Convert the ArrayList to the actual array implementation.
				int length = list.size();
				Object arrayData = arrayDataClass.constructor().invoke(length);
				Object iterator = arrayDataClass.iterator().invoke(arrayData);

				for (int x = 0; x < length; x++) {
					arrayDataClass.put().invoke(arrayData, iterator, list.get(x));
				}

				return arrayData;

			case END_DOCUMENT:
				return null;

			// These shouldn't happen here.
			case NAME:
			case END_ARRAY:
			case END_OBJECT:
			default:
				// Incorrect formatting.
				throw new IllegalStateException();

			}

		} catch (Throwable t) {
			throw new DataBindException(String.format("Failed to convert Map to %s.", dataClass.typeClass()), t);
		}
	}

	Object readNumber(DataClassAtom dataClass, JsonReader reader) throws IOException {
		Class<?> clss = dataClass.dataClass();
		if (clss == Integer.class || clss == int.class) {
			return reader.nextInt();
		} else if (clss == Long.class || clss == long.class) {
			return reader.nextLong();
		} else if (clss == Short.class || clss == short.class) {
			return Short.valueOf((short) reader.nextInt());
		} else if (clss == Byte.class || clss == byte.class) {
			return Byte.valueOf((byte) reader.nextInt());
		} else if (clss == Float.class || clss == float.class) {
			return Float.valueOf((float) reader.nextDouble());
		} else if (clss == Double.class || clss == double.class) {
			return Double.valueOf(reader.nextDouble());
		}

		// Should not get here.
		throw new IllegalStateException();
	}

}
