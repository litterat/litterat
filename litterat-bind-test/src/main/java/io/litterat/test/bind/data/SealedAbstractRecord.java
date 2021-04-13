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
package io.litterat.test.bind.data;

import java.util.List;

import io.litterat.bind.Record;
import io.litterat.bind.Union;

public class SealedAbstractRecord {

	// Union annotation not required but should be ignored as permitted classes should take precedent.
	@Union
	public static abstract sealed class SealedShape permits RecordPoint,RecordCircle {};

	@Record
	public static non-sealed class RecordPoint extends SealedShape {
		private final int x;
		private final int y;

		public RecordPoint(int x, int y) {
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
			RecordPoint other = (RecordPoint) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	@Record
	public static final class RecordCircle extends SealedShape {
		private final int x;
		private final int y;
		private final int radius;

		public RecordCircle(int x, int y, int radius) {
			this.x = x;
			this.y = y;
			this.radius = radius;
		}

		public int x() {
			return x;
		}

		public int y() {
			return y;
		}

		public int radius() {
			return radius;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + radius;
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
			RecordCircle other = (RecordCircle) obj;
			if (radius != other.radius)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	// List of union type.
	public static record SealedShapeList(List<SealedShape> list, SealedShape[] array) {}

}
