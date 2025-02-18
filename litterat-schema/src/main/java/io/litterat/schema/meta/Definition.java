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

import io.litterat.annotation.Typename;
import io.litterat.annotation.Union;

/**
 *
 * A definition is how each rule is defined in the library. A definition is itself a union type of
 * either an Element or Atom.
 *
 */

@Typename(namespace = "meta", name = "definition")
@Union(value = { Element.class, Atom.class }, sealed = true)
public interface Definition {

}
