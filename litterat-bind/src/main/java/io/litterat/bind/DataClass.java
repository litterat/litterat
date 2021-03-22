package io.litterat.bind;

/**
 * 
 * A DataClass represents the interface into data classes. This is also the parent class for
 * DataClassAtom, DataClassRecord, DatClassArray and DataClassUnion.
 *
 */
public abstract class DataClass {

	public enum DataClassType {
		ATOM, RECORD, ARRAY, UNION
	}

	// The application type data class.
	private final Class<?> typeClass;

	// The data class type.
	private final DataClassType dataClassType;

	public DataClass(Class<?> targetType, DataClassType dataType) {

		this.typeClass = targetType;

		this.dataClassType = dataType;
	}

	/**
	 * @return The class this descriptor is for.
	 */
	public Class<?> typeClass() {
		return typeClass;
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
