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
package io.litterat.schema.meta;

import io.litterat.schema.TypeLibrary;

public class SchemaTypes {

	public static final Typename ANY = new Typename("schema", "any");
	public static final Typename ARRAY = new Typename("schema", "array");
	public static final Typename ATOM = new Typename("schema", "atom");
	public static final Typename ATOM_ATTRIBUTE = new Typename("schema", "atom_attribute");
	public static final Typename ATOM_BIG_ENDIAN = new Typename("schema", "atom_big_endian");
	public static final Typename ATOM_LITTLE_ENDIAN = new Typename("schema", "atom_little_endian");
	public static final Typename ATOM_SIGNED = new Typename("schema", "atom_signed");
	public static final Typename ATOM_IEEE756 = new Typename("schema", "atom_ieee756");
	public static final Typename ATOM_UNSIGNED = new Typename("schema", "atom_big_unsigned");
	public static final Typename ATOM_FIXED_LENGTH = new Typename("schema", "atom_fixed_length");
	public static final Typename ATOM_VARIABLE_LENGTH = new Typename("schema", "atom_variable_length");
	public static final Typename DEFINITION = new Typename("schema", "definition");
	public static final Typename DICTIONARY = new Typename("schema", "dictionary");
	public static final Typename ELEMENT = new Typename("schema", "element");
	public static final Typename ENCODING = new Typename("schema", "encoding");
	public static final Typename ENUM = new Typename("schema", "enum");
	public static final Typename ENVELOPE = new Typename("schema", "envelope");
	public static final Typename EXPRESSION = new Typename("schema", "expression");
	public static final Typename FIELD = new Typename("schema", "field");
	public static final Typename INTERFACE = new Typename("schema", "interface");
	public static final Typename METHOD = new Typename("schema", "method");
	public static final Typename NAMESPACE = new Typename("schema", "namespace");
	public static final Typename RECORD = new Typename("schema", "record");
	public static final Typename SEQUENCE = new Typename("schema", "sequence");
	public static final Typename SIGNATURE = new Typename("schema", "signature");
	public static final Typename TYPE_NAME = new Typename("schema", "type_name");
	public static final Typename TYPE_NAME_DEFINITION = new Typename("schema", "type_name_definition");
	public static final Typename UNION = new Typename("schema", "union");

	// @formatter:off
	public static final Element DEFINITION_DEF = new Union(
			new Typename[] { SEQUENCE, RECORD, UNION, ENCODING });

	public static final Element ELEMENT_DEF = new Union(
			new Typename[] { ANY, ARRAY });

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
			new Typename[] { ATOM_BIG_ENDIAN, ATOM_LITTLE_ENDIAN, ATOM_SIGNED, ATOM_UNSIGNED,
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
