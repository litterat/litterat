package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * 
 * A DataClass represents the interface into data classes. The base class is used for all ATOM data
 * class types. This is also the parent class for DataClassRecord, DatClassArray and DataClassUnion.
 *
 */
public class DataClass {

	public enum DataClassType {
		ATOM, RECORD, ARRAY, UNION
	}

	// The class to be projected.
	private final Class<?> typeClass;

	// The embedded class type.
	private final Class<?> dataClass;

	// Method handle to convert object to data object.
	private final MethodHandle toData;

	// Method handle to convert data object to target object.
	private final MethodHandle toObject;

	// The data class type.
	private final DataClassType dataClassType;

	public DataClass(Class<?> targetType, Class<?> serialType, MethodHandle toData, MethodHandle toObject,
			DataClassType dataType) {

		this.typeClass = targetType;
		this.dataClass = serialType;
		this.toData = toData;
		this.toObject = toObject;

		this.dataClassType = dataType;
	}

	// An Atom uses identity function for toData/toObject and construct.
	public DataClass(Class<?> targetType) {
		this(targetType, targetType, identity(targetType), identity(targetType), DataClassType.ATOM);
	}

	// An Atom with conversion functions. e.g. String <--> UUID
	public DataClass(Class<?> targetType, Class<?> dataClass, MethodHandle toData, MethodHandle toObject) {
		this(targetType, dataClass, toData, toObject, DataClassType.ATOM);
	}

	private static MethodHandle identity(Class<?> targetType) {
		return MethodHandles.identity(targetType);
	}

	/**
	 * @return The class this descriptor is for.
	 */
	public Class<?> typeClass() {
		return typeClass;
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

	public DataClassType dataClassType() {
		return dataClassType;
	}

	public boolean isRecord() {
		return DataClassType.RECORD == dataClassType;
	}

	public boolean isAtom() {
		return DataClassType.ATOM == dataClassType;
	}

	public boolean isArray() {
		return DataClassType.ARRAY == dataClassType;
	}

	public boolean isUnion() {
		return DataClassType.UNION == dataClassType;
	}

}
