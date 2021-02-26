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
package io.litterat.test.bind.data;

import io.litterat.bind.Data;
import io.litterat.bind.DataOrder;

/**
 *
 * An example of a simple immutable tuple.
 *
 */
@DataOrder({ "y", "x" })
public class SimpleImmutableDataOrder {

	private final int x;
	private final int y;

	@Data
	public SimpleImmutableDataOrder(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleImmutableDataOrder other = (SimpleImmutableDataOrder) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
