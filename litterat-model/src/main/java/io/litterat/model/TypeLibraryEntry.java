/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.model;

import io.litterat.bind.PepDataClass;
import io.litterat.model.types.Definition;
import io.litterat.model.types.TypeName;

public class TypeLibraryEntry {

	final TypeLibraryState state;
	final TypeName name;
	final Definition definition;
	final PepDataClass clss;

	public TypeLibraryEntry(TypeLibraryState state, TypeName name, Definition definition, PepDataClass clss) {
		this.state = state;
		this.name = name;
		this.definition = definition;
		this.clss = clss;
	}

	public TypeLibraryState state() {
		return state;
	}

	public TypeName typeName() {
		return name;
	}

	public PepDataClass typeClass() {
		return clss;
	}

	public Definition definition() {
		return definition;
	}

}