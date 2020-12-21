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
package io.litterat.test.pep.data;

import io.litterat.pep.Data;
import io.litterat.pep.Field;

/**
 * 
 * This is complex in the sense that it is modifying the input or output values of X and Y.
 * The byte code invariance verifier will not be able to resolve which parameters are X and Y.
 * Field annotations are used to assist the verifier for y. However, x is ok because the field
 * x setter can be checked by the invariance verifier for first param and the getter matches the field.
 *
 */
public class ComplexImmutable {

	public static int multiplier = 2;

	private final int x;
	private final int y;

	@Data
	public ComplexImmutable(int x, @Field(name = "y") int y) {
		this.x = x;

		// hopefully this isn't removed.
		int test = y * multiplier;
		this.y = test / multiplier;
	}

	public int x() {
		int test = x * multiplier;
		return test / multiplier;
	}

	public int y() {
		return y;
	}
}
