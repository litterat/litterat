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

import io.litterat.annotation.Record;
import io.litterat.annotation.Field;

public class FieldAnnotationImmutable {

	// Change name and required.
	@Field(name = "a", required = true)
	private final String ax;

	// Change name only
	@Field(name = "b")
	private final String bx;

	// Change required only
	@Field(required = true)
	private final String c;

	// Changed in constructor fields.
	private final String dx;
	private final String ex;
	private final String f;

	private final String gx;
	private final String hx;
	private final String i;

	@Record
	public FieldAnnotationImmutable(String ax, String b, String c, @Field(name = "d", required = true) String dx,
			@Field(name = "e") String ex, @Field(required = true) String f, String gx, String hx, String i) {
		this.ax = ax;
		this.bx = b;
		this.c = c;

		// Modified by annotation in contructor params.
		this.dx = dx;
		this.ex = ex;
		this.f = f;

		// Modified by annotation on getters.
		this.gx = gx;
		this.hx = hx;
		this.i = i;

	}

	public String ax() {
		return ax;
	}

	public String b() {
		return bx;
	}

	public String c() {
		return c;
	}

	public String dx() {
		return dx;
	}

	public String ex() {
		return ex;
	}

	public String f() {
		return f;
	}

	@Field(name = "g", required = true)
	public String gx() {
		return gx;
	}

	@Field(name = "h")
	public String hx() {
		return hx;
	}

	@Field(required = true)
	public String i() {
		return i;
	}

}
