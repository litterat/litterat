/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.schema.meta;

import io.litterat.schema.TypeLibrary;
import io.litterat.schema.types.Array;
import io.litterat.schema.types.Definition;
import io.litterat.schema.types.Field;
import io.litterat.schema.types.Record;
import io.litterat.schema.types.Reference;
import io.litterat.schema.types.TypeName;
import io.litterat.schema.types.Union;

public class SchemaTypes {

	public static final TypeName ANY = new TypeName("schema", "any");
	public static final TypeName ARRAY = new TypeName("schema", "array");
	public static final TypeName ATOM = new TypeName("schema", "atom");
	public static final TypeName ATOM_ATTRIBUTE = new TypeName("schema", "atom_attribute");
	public static final TypeName ATOM_BIG_ENDIAN = new TypeName("schema", "atom_big_endian");
	public static final TypeName ATOM_LITTLE_ENDIAN = new TypeName("schema", "atom_little_endian");
	public static final TypeName ATOM_SIGNED = new TypeName("schema", "atom_signed");
	public static final TypeName ATOM_IEEE756 = new TypeName("schema", "atom_ieee756");
	public static final TypeName ATOM_UNSIGNED = new TypeName("schema", "atom_big_unsigned");
	public static final TypeName ATOM_FIXED_LENGTH = new TypeName("schema", "atom_fixed_length");
	public static final TypeName ATOM_VARIABLE_LENGTH = new TypeName("schema", "atom_variable_length");
	public static final TypeName DEFINITION = new TypeName("schema", "definition");
	public static final TypeName DICTIONARY = new TypeName("schema", "dictionary");
	public static final TypeName ELEMENT = new TypeName("schema", "element");
	public static final TypeName ENCODING = new TypeName("schema", "encoding");
	public static final TypeName ENUM = new TypeName("schema", "enum");
	public static final TypeName ENVELOPE = new TypeName("schema", "envelope");
	public static final TypeName EXPRESSION = new TypeName("schema", "expression");
	public static final TypeName FIELD = new TypeName("schema", "field");
	public static final TypeName INTERFACE = new TypeName("schema", "interface");
	public static final TypeName METHOD = new TypeName("schema", "method");
	public static final TypeName NAMESPACE = new TypeName("schema", "namespace");
	public static final TypeName RECORD = new TypeName("schema", "record");
	public static final TypeName REFERENCE = new TypeName("schema", "reference");
	public static final TypeName SEQUENCE = new TypeName("schema", "sequence");
	public static final TypeName SIGNATURE = new TypeName("schema", "signature");
	public static final TypeName TYPE_NAME = new TypeName("schema", "type_name");
	public static final TypeName TYPE_NAME_DEFINITION = new TypeName("schema", "type_name_definition");
	public static final TypeName UNION = new TypeName("schema", "union");

	// @formatter:off
	public static final Definition DEFINITION_DEF = new Union(
			new Reference[] {
					new Reference(SEQUENCE),
					new Reference(RECORD),
					new Reference(UNION),
					new Reference(ENCODING)
			});

	public static final Definition ELEMENT_DEF = new Union(
			new Reference[] {
					new Reference(ANY),
					new Reference(ARRAY),
					new Reference(REFERENCE)
			});

	// Any is really just an empty object with no fields.
	public static final Definition ANY_DEF = new Record(
			new Field[] {
			});

	public static final Definition ARRAY_DEF = new Record(
			new Field[] {
					new Field("type", new Reference(ELEMENT))
			});

	public static final Definition ATOM_DEF = new Record(
			new Field[] {
					new Field("attributes", new Reference(ATOM_ATTRIBUTE))
			});

	public static final Definition ATOM_ATTRIBUTE_DEF = new Union(
			new Reference[] {
					 new Reference(ATOM_BIG_ENDIAN),
					 new Reference(ATOM_LITTLE_ENDIAN),
					 new Reference(ATOM_SIGNED),
					 new Reference(ATOM_UNSIGNED),
					 new Reference(ATOM_IEEE756),
					 new Reference(ATOM_FIXED_LENGTH),
					 new Reference(ATOM_VARIABLE_LENGTH)
			});

	public static final Definition ATOM_BIG_ENDIAN_DEF = new Record(
			new Field[] {
			});

	public static final Definition ATOM_LITTLE_ENDIAN_DEF = new Record(
			new Field[] {
			});

	public static final Definition ATOM_SIGNED_DEF = new Record(
			new Field[] {
			});

	public static final Definition ATOM_UNSIGNED_DEF = new Record(
			new Field[] {
			});

	public static final Definition ATOM_IEEE756_DEF = new Record(
			new Field[] {
			});

	public static final Definition ATOM_FIXED_LENGTH_DEF = new Record(
			new Field[] {
					new Field("bytes", new Reference(TypeLibrary.UINT8))
			});


	public static final Definition SEQUENCE_DEF = new Record(
			new Field[] {
					new Field("fields", new Array(FIELD))
			});
	// @formatter:off
}
