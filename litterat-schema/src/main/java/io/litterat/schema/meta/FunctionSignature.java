/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.schema.meta;

import io.litterat.annotation.Record;

@Record
@io.litterat.annotation.Typename(namespace = "meta", name = "signature")
public class FunctionSignature {

	private final Typename[] arguments;
	private final Typename returnType;

	public FunctionSignature(Typename returnType, Typename... arguments) {
		this.arguments = arguments;
		this.returnType = returnType;
	}

	public Typename[] arguments() {
		return arguments;
	}

	public Typename returnType() {
		return returnType;
	}
}
