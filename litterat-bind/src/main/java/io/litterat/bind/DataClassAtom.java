package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class DataClassAtom extends DataClass {

	// The embedded class type.
	private final Class<?> dataClass;

	// Method handle to convert object to data object.
	// Converts from typeClass -> dataClasss.
	private final MethodHandle toData;

	// Method handle to convert data object to target object.
	// Converts from dataClass -> typeClass.
	private final MethodHandle toObject;

	protected DataClassAtom(Class<?> targetType, Class<?> serialType, MethodHandle toData, MethodHandle toObject,
			DataClassType dataType) {
		super(targetType, dataType);

		this.dataClass = serialType;
		this.toData = toData;
		this.toObject = toObject;
	}

	// An Atom with conversion functions. e.g. String <--> UUID
	public DataClassAtom(Class<?> targetType, Class<?> dataClass, MethodHandle toData, MethodHandle toObject) {
		this(targetType, dataClass, toData, toObject, DataClassType.ATOM);
	}

	// An Atom uses identity function for toData/toObject and construct.
	public DataClassAtom(Class<?> targetType) {
		this(targetType, targetType, identity(targetType), identity(targetType), DataClassType.ATOM);
	}

	private static MethodHandle identity(Class<?> targetType) {
		return MethodHandles.identity(targetType);
	}

	/**
	 * @return The embedded class. This may be equal to the target class.
	 */
	public Class<?> dataClass() {
		return dataClass;
	}

	/**
	 * @return A MethodHandle that has the signature T embed(Object[] values).
	 */
	public MethodHandle toObject() {
		return toObject;
	}

	/**
	 * @return A MethodHandle that has the signature Object[] project(T object)
	 */
	public MethodHandle toData() {
		return toData;
	}
}
