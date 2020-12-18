package io.litterat.pep;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;

public class PepDataArrayClass extends PepDataClass {

	// data class.
	private final PepDataClass arrayDataClass;

	// int size( <Array> );
	private final MethodHandle size;

	// <Iterator> iterator( <Array> );
	private final MethodHandle iterator;

	// void put( <Iterator>, <Array>, <Value> );
	private final MethodHandle put;

	// <data> get( <Iterator> iterator, <Array> );
	private final MethodHandle get;

	public PepDataArrayClass(Class<?> targetType, Class<?> serialType, MethodHandle creator, MethodHandle constructor,
			MethodHandle toData, MethodHandle toObject, PepDataComponent[] fields, DataType dataType,
			PepDataClass arrayDataClass, Object bridge) throws NoSuchMethodException, IllegalAccessException {
		super(targetType, serialType, creator, constructor, toData, toObject, fields, dataType);

		Class<?> iteratorClass;
		Class<?> arrayClass = targetType;
		this.arrayDataClass = arrayDataClass;
		Class<?> bridgeDataClass = arrayDataClass.typeClass();
		if (Collection.class.isAssignableFrom(targetType)) {
			arrayClass = Collection.class;
			bridgeDataClass = Object.class;
			iteratorClass = bridge.getClass().getMethod("iterator", Collection.class).getReturnType();
		} else {
			if (bridgeDataClass.isPrimitive()) {
				iteratorClass = bridge.getClass().getMethod("iterator", targetType).getReturnType();
			} else {
				arrayClass = Object[].class;
				bridgeDataClass = Object.class;
				iteratorClass = bridge.getClass().getMethod("iterator", arrayClass).getReturnType();
			}
		}

		size = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "size", MethodType.methodType(int.class, arrayClass)).bindTo(bridge);

		iterator = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "iterator", MethodType.methodType(iteratorClass, arrayClass))
				.bindTo(bridge);

		put = MethodHandles.lookup().findVirtual(bridge.getClass(), "put",
				MethodType.methodType(void.class, iteratorClass, arrayClass, bridgeDataClass)).bindTo(bridge);

		get = MethodHandles.lookup().findVirtual(bridge.getClass(), "get",
				MethodType.methodType(bridgeDataClass, iteratorClass, arrayClass)).bindTo(bridge);
	}

	public PepDataClass arrayDataClass() {
		return arrayDataClass;
	}

	public MethodHandle size() {
		return this.size;
	}

	public MethodHandle iterator() {
		return this.iterator;
	}

	public MethodHandle put() {
		return this.put;
	}

	public MethodHandle get() {
		return this.get;
	}

}
