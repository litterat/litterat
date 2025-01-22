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

@Record
public class AbstractUnionCircle extends AbstractUnion {
	private final int radius;

	public AbstractUnionCircle(int x, int y, int radius) {
		super(x, y);
		this.radius = radius;
	}

	public int radius() {
		return this.radius;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + radius;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractUnionCircle other = (AbstractUnionCircle) obj;
        return radius == other.radius;
    }

}
