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
package io.litterat.pep.test.data;

import io.litterat.pep.Data;

/**
 * 
 * This class tests a constructor with immutable fields.
 */

public class ImmutableAtom {

	private final SimpleEnum enumCount;

	private final String str;

	private final boolean bool;

	@Data
	public ImmutableAtom(SimpleEnum enumCount, String str, boolean bool) {
		this.enumCount = enumCount;
		this.str = str;
		this.bool = bool;
	}

	public SimpleEnum enumCount() {
		return enumCount;
	}

	public String str() {
		return str;
	}

	public boolean bool() {
		return bool;
	}

}
