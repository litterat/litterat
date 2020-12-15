package io.litterat.pep;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class PepDataArrayClass extends PepDataClass {

	// int size( <Array> );
	private final MethodHandle size;

	// <Iterator> iterator( <Array> );
	private final MethodHandle iterator;

	// void put( <Iterator>, <Array>, <Value> );
	private final MethodHandle put;

	// <data> get( <Iterator> iterator, <Array> );
	private final MethodHandle get;

	public PepDataArrayClass(Class<?> targetType, Class<?> serialType, MethodHandle creator, MethodHandle constructor,
			MethodHandle toData, MethodHandle toObject, PepDataComponent[] fields, DataType dataType, Object bridge)
			throws NoSuchMethodException, IllegalAccessException {
		super(targetType, serialType, creator, constructor, toData, toObject, fields, dataType);

		Class<?> iteratorClass = bridge.getClass().getMethod("iterator", targetType).getReturnType();

		size = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "size", MethodType.methodType(int.class, targetType)).bindTo(bridge);

		iterator = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "iterator", MethodType.methodType(iteratorClass, targetType))
				.bindTo(bridge);

		put = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "put", MethodType.methodType(iteratorClass, targetType)).bindTo(bridge);

		get = MethodHandles.lookup()
				.findVirtual(bridge.getClass(), "iterator", MethodType.methodType(iteratorClass, targetType))
				.bindTo(bridge);
	}

	MethodHandle size() {
		return this.size;
	}

	MethodHandle iterator() {
		return this.iterator;
	}

	MethodHandle put() {
		return this.put;
	}

	MethodHandle get() {
		return this.get;
	}

}
