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
package io.litterat.model.meta;

import io.litterat.model.Array;
import io.litterat.model.Element;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.TypeName;
import io.litterat.model.Union;
import io.litterat.model.library.TypeLibrary;

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
	public static final TypeName SEQUENCE = new TypeName("schema", "sequence");
	public static final TypeName SIGNATURE = new TypeName("schema", "signature");
	public static final TypeName TYPE_NAME = new TypeName("schema", "type_name");
	public static final TypeName TYPE_NAME_DEFINITION = new TypeName("schema", "type_name_definition");
	public static final TypeName UNION = new TypeName("schema", "union");

	// @formatter:off
	public static final Element DEFINITION_DEF = new Union(
			new TypeName[] { SEQUENCE, RECORD, UNION, ENCODING });

	public static final Element ELEMENT_DEF = new Union(
			new TypeName[] { ANY, ARRAY });

	// Any is really just an empty object with no fields.
	public static final Element ANY_DEF = new Record(
			new Field[] {
			});

	public static final Element ARRAY_DEF = new Record(
			new Field[] {
					new Field("type", ELEMENT)
			});

	public static final Element ATOM_DEF = new Record(
			new Field[] {
					new Field("attributes", ATOM_ATTRIBUTE)
			});

	public static final Element ATOM_ATTRIBUTE_DEF = new Union(
			new TypeName[] { ATOM_BIG_ENDIAN, ATOM_LITTLE_ENDIAN, ATOM_SIGNED, ATOM_UNSIGNED,
							ATOM_IEEE756, ATOM_FIXED_LENGTH, ATOM_VARIABLE_LENGTH
			});

	public static final Element ATOM_BIG_ENDIAN_DEF = new Record(
			new Field[] {
			});

	public static final Element ATOM_LITTLE_ENDIAN_DEF = new Record(
			new Field[] {
			});

	public static final Element ATOM_SIGNED_DEF = new Record(
			new Field[] {
			});

	public static final Element ATOM_UNSIGNED_DEF = new Record(
			new Field[] {
			});

	public static final Element ATOM_IEEE756_DEF = new Record(
			new Field[] {
			});

	public static final Element ATOM_FIXED_LENGTH_DEF = new Record(
			new Field[] {
					new Field("bytes", TypeLibrary.UINT8)
			});


	public static final Element SEQUENCE_DEF = new Record(
			new Field[] {
					new Field("fields", new Array(FIELD))
			});
	// @formatter:off
}
