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
package io.litterat.test.data;

import io.litterat.annotation.Field;
import io.litterat.annotation.Union;

import java.util.List;

public class SealedRecordInterface {

	@Union({ RecordPoint.class, RecordCircle.class })
	public interface SealedShape {}

	public record RecordPoint(@Field("x") int xx, int y) implements SealedShape {}

	public record RecordCircle(int x, int y, int radius, @Union( { String.class, Integer.class }) Object tag)
			implements SealedShape{}

	// Intentionally not part of sealed interface.
	public record RecordRectangle(int x, int y, int width, int height) implements SealedShape {}

	// List of union type.
	public record SealedShapeList(List<SealedShape> list, SealedShape[] array) {}

}
