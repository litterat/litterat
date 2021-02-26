/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.litterat.bind.describe;

import java.util.Optional;

import io.litterat.bind.DataBridge;
import io.litterat.bind.PepException;

public class OptionalBridge<T> implements DataBridge<T, Optional<T>> {

	@Override
	public T toData(Optional<T> value) throws PepException {

		return value.orElse(null);
	}

	@Override
	public Optional<T> toObject(T value) throws PepException {

		return Optional.ofNullable(value);
	}

}
