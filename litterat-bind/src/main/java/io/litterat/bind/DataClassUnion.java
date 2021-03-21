package io.litterat.bind;

import java.lang.invoke.MethodHandle;

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

	private DataClass[] componentTypes;

	public DataClassUnion(Class<?> targetType, Class<?> serialType, MethodHandle toData, MethodHandle toObject) {
		super(targetType, serialType, toData, toObject, DataClassType.UNION);

		componentTypes = null;
	}

	public DataClass[] components() {
		return componentTypes;
	}

	/**
	 * As different implementations of an interface or abstract class will get loaded at different times
	 * the list of union types will not all be known at startup. Therefore it needs to be possible to
	 * add additional implementations to the list. One of the reasons why sealed classes are a better
	 * choice.
	 */
	public synchronized void addDataClass(DataClass unionClass) {

	}

}
