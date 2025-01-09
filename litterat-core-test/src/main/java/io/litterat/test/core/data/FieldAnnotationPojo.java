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
package io.litterat.test.core.data;

import io.litterat.bind.annotation.Field;
import io.litterat.bind.annotation.Record;

@Record
public class FieldAnnotationPojo {

	// Change name and required.
	@Field(name = "a", required = true)
	private String ax;

	// Change name only
	@Field(name = "b")
	private String bx;

	// Change required only
	@Field(required = true)
	private String c;

	// Changed in constructor fields.
	private String dx;
	private String ex;
	private String f;

	private String gx;
	private String hx;
	private String i;

	public FieldAnnotationPojo() {
	}

	public void setAx(String ax) {
		this.ax = ax;
	}

	public void setBx(String bx) {
		this.bx = bx;
	}

	public void setC(String c) {
		this.c = c;
	}

	@Field(name = "d", required = true)
	public void setDx(String dx) {
		this.dx = dx;
	}

	@Field(name = "e")
	public void setEx(String ex) {
		this.ex = ex;
	}

	@Field(required = true)
	public void setF(String f) {
		this.f = f;
	}

	public void setGx(String gx) {
		this.gx = gx;
	}

	public void setHx(String hx) {
		this.hx = hx;
	}

	public void setI(String i) {
		this.i = i;
	}

	public String getAx() {
		return ax;
	}

	public String getBx() {
		return bx;
	}

	public String getC() {
		return c;
	}

	public String getDx() {
		return dx;
	}

	public String getEx() {
		return ex;
	}

	public String getF() {
		return f;
	}

	@Field(name = "g", required = true)
	public String getGx() {
		return gx;
	}

	@Field(name = "h")
	public String getHx() {
		return hx;
	}

	@Field(required = true)
	public String getI() {
		return i;
	}

}
