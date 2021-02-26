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
package io.litterat.model.meta;

import io.litterat.model.types.Atom;
import io.litterat.model.types.AtomAttribute;
import io.litterat.model.types.Definition;
import io.litterat.model.types.Encoding;
import io.litterat.model.types.AtomAttribute.AtomBigEndian;
import io.litterat.model.types.AtomAttribute.AtomFixedLength;
import io.litterat.model.types.AtomAttribute.AtomIEEE756;
import io.litterat.model.types.AtomAttribute.AtomInteger;
import io.litterat.model.types.AtomAttribute.AtomLittleEndian;
import io.litterat.model.types.AtomAttribute.AtomSigned;
import io.litterat.model.types.AtomAttribute.AtomUnsigned;
import io.litterat.model.types.AtomAttribute.AtomVariableLength;

public class TypeDefinitions {

	// @formatter:off
	public static final Definition INT8 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(1),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition UINT8 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(1),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition INT16 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(2),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition UINT16 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(2),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition LE_INT16 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(2),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition LE_UINT16 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(2),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition INT32 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(4),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition UINT32 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(4),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition LE_INT32 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(4),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition LE_UINT32 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(4),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition UVARINT32 = new Atom( new AtomAttribute[] {
			new AtomVariableLength(1,5),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition VARINT32 = new Atom( new AtomAttribute[] {
			new AtomVariableLength(1,5),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomSigned()
	});


	public static final Definition INT64 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(8),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition UINT64 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(8),
			new AtomBigEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition LE_INT64 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(8),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomSigned()
	});

	public static final Definition LE_UINT64 = new Atom( new AtomAttribute[] {
			new AtomFixedLength(8),
			new AtomLittleEndian(),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition UVARINT64 = new Atom( new AtomAttribute[] {
			new AtomVariableLength(1,9),
			new AtomInteger(),
			new AtomUnsigned()
	});

	public static final Definition VARINT64 = new Atom( new AtomAttribute[] {
			new AtomVariableLength(1,9),
			new AtomInteger(),
			new AtomSigned()
	});


	public static final Definition FLOAT = new Atom( new AtomAttribute[] {
			new AtomFixedLength(4),
			new AtomIEEE756(),
			new AtomSigned()
	});

	public static final Definition DOUBLE = new Atom( new AtomAttribute[] {
			new AtomFixedLength(8),
			new AtomIEEE756(),
			new AtomSigned()
	});

	public static final Definition BOOLEAN = new Atom( new AtomAttribute[] {
			new AtomFixedLength(1)
	});

	// TODO Need a better definition for strings.
	public static final Definition STRING = new Encoding("utf8");


	// @formatter:on
}
