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

import io.litterat.json.parser.JsonReader;
import io.litterat.json.parser.JsonToken;
import io.litterat.json.parser.JsonWriter;
import io.litterat.pep.PepContext;
import io.litterat.pep.PepDataArrayClass;
import io.litterat.pep.PepDataClass;
import io.litterat.pep.PepDataComponent;
import io.litterat.pep.PepException;

public class JsonMapper {
	private final PepContext context;

	public static String toJson(Object object) throws PepException {
		StringWriter writer = new StringWriter();
		JsonMapper mapper = new JsonMapper();
		mapper.toJson(object, writer);
		return writer.toString();
	}

	public static <T> T fromJson(String json, Class<?> clss) throws PepException {
		StringReader reader = new StringReader(json);
		JsonMapper mapper = new JsonMapper();
		return mapper.fromJson(json, clss, reader);
	}

	public JsonMapper(PepContext context) {
		this.context = context;
	}

	public JsonMapper() {
		this(PepContext.builder().build());
	}

	public void toJson(Object object, Writer writer) throws PepException {
		PepDataClass dataClass = context.getDescriptor(object.getClass());
		toJson(object, dataClass, new JsonWriter(writer));
	}

	private void toJson(Object object, PepDataClass dataClass, JsonWriter writer) throws PepException {

		Objects.requireNonNull(object);

		int fieldIndex = 0;

		try {

			Object data = dataClass.toData().invoke(object);

			if (dataClass.isAtom()) {
				writeAtom(data, writer);
			} else if (dataClass.isData()) {

				writer.beginObject();

				PepDataComponent[] fields = dataClass.dataComponents();

				for (fieldIndex = 0; fieldIndex < dataClass.dataComponents().length; fieldIndex++) {
					PepDataComponent field = fields[fieldIndex];

					Object v = field.accessor().invoke(data);

					// Recursively convert object to map.
					if (v != null) {
						PepDataClass fieldDataClass = field.dataClass();

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

				PepDataArrayClass arrayClass = (PepDataArrayClass) dataClass;

				Object arrayData = object;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object iterator = arrayClass.iterator().invoke(arrayData);
				PepDataClass arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					Object av = arrayClass.get().invoke(iterator, arrayData);
					toJson(av, arrayDataClass, writer);
				}

				writer.endArray();
			}

		} catch (Throwable t) {
			throw new PepException(String.format("Failed to convert %s to Map. Could not convert field %s",
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
		} else {
			throw new IOException(String.format("Could not write atom ", object.getClass()));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(String json, Class<?> clss, Reader reader) throws PepException {

		PepDataClass dataClass = context.getDescriptor(clss);

		return (T) fromJson(dataClass, new JsonReader(reader));
	}

	private Map<PepDataClass, Map<String, PepDataComponent>> fieldMaps = new HashMap<>();

	private Map<String, PepDataComponent> componentMap(PepDataClass dataClass) {
		return fieldMaps.computeIfAbsent(dataClass, (clss) -> {
			return Arrays.asList(clss.dataComponents()).stream()
					.collect(Collectors.toMap(PepDataComponent::name, item -> item));
		});

	}

	private Object fromJson(PepDataClass dataClass, JsonReader reader) throws PepException {

		try {

			JsonToken token = reader.peek();
			switch (token) {

			case NUMBER:
				return dataClass.toObject().invoke(readNumber(dataClass, reader));

			case STRING:
				if (dataClass.dataClass() == String.class) {
					return dataClass.toObject().invoke(reader.nextString());
				}
				throw new PepException("expected boolean");
			case BOOLEAN:
				if (dataClass.dataClass() == boolean.class || dataClass.dataClass() == Boolean.class) {
					return dataClass.toObject().invoke(reader.nextBoolean());
				}
				throw new PepException("expected boolean");
			case NULL:
				reader.nextNull();
				return null;

			case BEGIN_OBJECT:

				if (!dataClass.isData()) {
					throw new IllegalStateException();
				}

				reader.beginObject();

				PepDataComponent[] fields = dataClass.dataComponents();
				Object[] construct = new Object[fields.length];

				while (reader.hasNext()) {

					String name = reader.nextName();

					// Find the name in the fields.
					PepDataComponent field = componentMap(dataClass).get(name);
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

				PepDataArrayClass arrayDataClass = (PepDataArrayClass) dataClass;

				// Token based reader needs to read everything in the array before creating.
				// TODO could potentially provide meta data saying if the collection type is
				// static or dynamic.
				// A dynamic sized collection could be created and then have each added.
				PepDataClass arrayValueClass = arrayDataClass.arrayDataClass();
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
			throw new PepException(String.format("Failed to convert Map to %s.", dataClass.typeClass()), t);
		}
	}

	Object readNumber(PepDataClass dataClass, JsonReader reader) throws IOException {
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
