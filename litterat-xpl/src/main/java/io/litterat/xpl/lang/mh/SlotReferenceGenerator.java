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
package io.litterat.xpl.lang.mh;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import io.litterat.xpl.lang.LitteratMachine;
import io.litterat.xpl.lang.SlotReference;

public class SlotReferenceGenerator implements ExpressionGenerator {

	private final SlotReference slotReference;

	public SlotReferenceGenerator(SlotReference slotReference) {
		this.slotReference = slotReference;
	}

	@Override
	public MethodHandle bind(LitteratMachine am)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {

		// (am, slot) -> am.getVariable( slot );
		MethodHandle varGetter = MethodHandles.lookup().findVirtual(LitteratMachine.class, "getVariable",
				MethodType.methodType(Object.class, int.class));

		// () -> return variable;
		MethodHandle av = MethodHandles.constant(int.class, slotReference.variable());

		// (am) -> am.getVariable( variable );
		return MethodHandles.collectArguments(varGetter, 1, av);
	}

}
