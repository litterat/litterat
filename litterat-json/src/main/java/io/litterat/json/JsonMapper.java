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
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassComponent;
import io.litterat.bind.DataBindException;
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
		DataClassRecord dataClass = context.getDescriptor(object.getClass());
		toJson(object, dataClass, new JsonWriter(writer));
	}

	private void toJson(Object object, DataClassRecord dataClass, JsonWriter writer) throws DataBindException {

		Objects.requireNonNull(object);

		int fieldIndex = 0;

		try {

			Object data = dataClass.toData().invoke(object);

			if (dataClass.isAtom()) {
				writeAtom(data, writer);
			} else if (dataClass.isData()) {

				writer.beginObject();

				DataClassComponent[] fields = dataClass.dataComponents();

				for (fieldIndex = 0; fieldIndex < dataClass.dataComponents().length; fieldIndex++) {
					DataClassComponent field = fields[fieldIndex];

					Object v = field.accessor().invoke(data);

					// Recursively convert object to map.
					if (v != null) {
						DataClassRecord fieldDataClass = field.dataClass();

						writer.name(field.name());

						if (fieldDataClass.isAtom()) {
							v = fieldDataClass.toData().invoke(v);
							writeAtom(v, writer);
						} else {
							toJson(v, fieldDataClass, writer);
						}
					}

				}

				writer.endObject();
			} else {
				writer.beginArray();

				DataClassArray arrayClass = (DataClassArray) dataClass;

				Object arrayData = object;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object iterator = arrayClass.iterator().invoke(arrayData);
				DataClassRecord arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					Object av = arrayClass.get().invoke(iterator, arrayData);
					toJson(av, arrayDataClass, writer);
				}

				writer.endArray();
			}

		} catch (Throwable t) {
			throw new DataBindException(String.format("Failed to convert %s to Map. Could not convert field %s",
					dataClass.typeClass(), dataClass.dataComponents()[fieldIndex].name()), t);
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

		DataClassRecord dataClass = context.getDescriptor(clss);

		return (T) fromJson(dataClass, new JsonReader(reader));
	}

	private Map<DataClassRecord, Map<String, DataClassComponent>> fieldMaps = new HashMap<>();

	private Map<String, DataClassComponent> componentMap(DataClassRecord dataClass) {
		return fieldMaps.computeIfAbsent(dataClass, (clss) -> {
			return Arrays.asList(clss.dataComponents()).stream()
					.collect(Collectors.toMap(DataClassComponent::name, item -> item));
		});

	}

	private Object fromJson(DataClassRecord dataClass, JsonReader reader) throws DataBindException {

		try {

			JsonToken token = reader.peek();
			switch (token) {

			case NUMBER:
				return dataClass.toObject().invoke(readNumber(dataClass, reader));

			case STRING:
				String v = reader.nextString();
				if (dataClass.dataClass() == String.class) {
					return dataClass.toObject().invoke(v);
				} else if (dataClass.dataClass() == Character.class) {
					Character c = Character.valueOf(v.toCharArray()[0]);
					return dataClass.toObject().invoke(c);
				} else if (dataClass.dataClass() == char.class) {
					char c = v.toCharArray()[0];
					return dataClass.toObject().invoke(c);
				}
				throw new DataBindException(
						String.format("Could not convert string to %s", dataClass.dataClass().getName()));
			case BOOLEAN:
				if (dataClass.dataClass() == boolean.class || dataClass.dataClass() == Boolean.class) {
					return dataClass.toObject().invoke(reader.nextBoolean());
				}
				throw new DataBindException(
						String.format("Could not convert boolean to %s", dataClass.dataClass().getName()));
			case NULL:
				reader.nextNull();
				return null;

			case BEGIN_OBJECT:

				if (!dataClass.isData()) {
					throw new IllegalStateException();
				}

				reader.beginObject();

				DataClassComponent[] fields = dataClass.dataComponents();
				Object[] construct = new Object[fields.length];

				while (reader.hasNext()) {

					String name = reader.nextName();

					// Find the name in the fields.
					DataClassComponent field = componentMap(dataClass).get(name);
					Objects.requireNonNull(field,
							String.format("field %s not found in class", name, dataClass.dataClass()));

					construct[field.index()] = fromJson(field.dataClass(), reader);

				}

				reader.endObject();

				Object data = dataClass.constructor().invoke(construct);

				return dataClass.toObject().invoke(data);

			case BEGIN_ARRAY:

				if (!dataClass.isArray()) {
					throw new IllegalStateException();
				}

				reader.beginArray();

				DataClassArray arrayDataClass = (DataClassArray) dataClass;

				// Token based reader needs to read everything in the array before creating.
				// TODO could potentially provide meta data saying if the collection type is
				// static or dynamic.
				// A dynamic sized collection could be created and then have each added.
				DataClassRecord arrayValueClass = arrayDataClass.arrayDataClass();
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
					arrayDataClass.put().invoke(iterator, arrayData, list.get(x));
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

	Object readNumber(DataClassRecord dataClass, JsonReader reader) throws IOException {
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
