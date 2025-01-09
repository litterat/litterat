package io.litterat.bind;


public class DataClassProxy extends DataClass {
	public DataClassProxy(Class<?> targetType, DataClassType dataType) {
		super( targetType, dataType);
	}
/*
	private final DataClass proxyDataClass;

	// The embedded class type.
	private final Class<?> dataClass;

	// Method handle to convert object to data object.
	// Converts from typeClass -> dataClasss.
	private final MethodHandle toData;

	// Method handle to convert data object to target object.
	// Converts from dataClass -> typeClass.
	private final MethodHandle toObject;

	public DataClassProxy(Typename reference, Class<?> targetType, Class<?> serialType, DataClass serialDataClass,
                          MethodHandle toData, MethodHandle toObject) {
		super(reference, targetType, DataClassType.PROXY);

		this.dataClass = serialType;
		this.proxyDataClass = serialDataClass;

		this.toData = toData;
		this.toObject = toObject;
	}

	// An Atom with conversion functions. e.g. String <--> UUID
	// public DataClassProxy(Proxy proxy, Class<?> targetType, Class<?> dataClass, DataClass
	// serialDataClass,
	// MethodHandle toData, MethodHandle toObject) {
	// this(proxy, targetType, dataClass, serialDataClass, toData, toObject, DataClassType.ATOM);
	// }

	// An Atom uses identity function for toData/toObject and construct.
//	public DataClassReference(Typename reference, Class<?> targetType, DataClass serialDataClass) {
//		this(reference, targetType, targetType, serialDataClass, identity(targetType), identity(targetType));
//	}

	private static MethodHandle identity(Class<?> targetType) {
		return MethodHandles.identity(targetType);
	}

	public Class<?> dataClass() {
		return dataClass;
	}

	public DataClass proxyDataClass() {
		return proxyDataClass;
	}

	public MethodHandle toObject() {
		return toObject;
	}

	public MethodHandle toData() {
		return toData;
	}

	@Override
	public String toString() {
		return "DataClassAtom [ typeClass=" + typeClass().getName() + ", dataClass=" + dataClass.getName() + "]";
	}
	*/
}
