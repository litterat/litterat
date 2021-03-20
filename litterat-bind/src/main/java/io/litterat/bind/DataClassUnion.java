package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.util.List;

/**
 * 
 * A Union data class is tagged union type. It can be represented in a number of ways in Java:
 * 
 * <ul>
 * <li>interface: An interface allows multiple data representations as child classes.
 * <li>abstract class: Similar to an interface, an abstract or base class can have multiple data
 * representations.
 * <li>embedded union: A class with one or more fields where only one is present at any one time.
 * </ul>
 *
 */
public class DataClassUnion extends DataClass {

	private List<DataClass> componentTypes;

	public DataClassUnion(Class<?> targetType, Class<?> serialType, MethodHandle toData, MethodHandle toObject) {
		super(targetType, serialType, toData, toObject, DataClassType.UNION);

		componentTypes = List.of();
	}

	List<DataClass> components() {
		return componentTypes;
	}

}
