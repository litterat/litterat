/*
 * Copyright (c) 2003-2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.xpl;

import java.util.Objects;

import io.litterat.annotation.Record;

public class Point {
	private final float latitude;
	private final float longitude;

	@Record
	public Point(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public float latitude() {
		return latitude;
	}

	public float longitude() {
		return longitude;
	}

	@Override
	public int hashCode() {
		return Objects.hash(latitude, longitude);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		return Float.floatToIntBits(latitude) == Float.floatToIntBits(other.latitude)
				&& Float.floatToIntBits(longitude) == Float.floatToIntBits(other.longitude);
	}

	@Override
	public String toString() {
		return "{ 'latitude':" + latitude + ", 'longitude': " + longitude + " }";
	}

}
