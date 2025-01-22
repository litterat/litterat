/*
 * Copyright (c) 2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.test.core.union;

import io.litterat.annotation.Record;

// This is not part of the SealedInterfaceUnion member list so is considered invalid.
@Record
public class SealedInterfaceUnionRectangle implements SealedInterfaceUnion {

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public SealedInterfaceUnionRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SealedInterfaceUnionRectangle other = (SealedInterfaceUnionRectangle) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
        return y == other.y;
    }
}
