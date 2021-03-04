package io.litterat.bind;

import java.lang.invoke.MethodHandle;

/**
 * 
 * A Union data class is tagged union type. It can be represented in a number of ways in Java:
 * 
 * <ul>
 * <li>interface: An interface allows multiple data representations as child classes.
 * <li>base class: Similar to an interface, a base class can have multiple data representations.
 * </ul>
 *
 *
 */
public class DataClassUnion extends DataClass {

	public DataClassUnion(Class<?> targetType, Class<?> serialType, MethodHandle toData, MethodHandle toObject) {
		super(targetType, serialType, toData, toObject, DataClassType.UNION);

	}

}
