/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.pep.describe;
/**
 * 
 * 
 * 
 * @formatter:off
 * 
 * Class describers attempt to discover if a Class fits into one
 * of the following categories
 * 
 * atom - Primitive or accepts single argument to construct.
 * data - Has multiple values and tagged as data class.
 * class with data - a class that produces a data class.
 * class - A class that doesn't fit the previous three throw an exception.
 * 
 * @formatter:on
 */
