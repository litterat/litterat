package io.litterat.bind;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;

/**
 * 
 * Represents an Array data class. The Array could be implemented by either a Java array or
 * collection. This provide an interface made up of MethodHandles to with the array implementation.
 * 
 * Extracting the values from an array object:
 * 
 * <pre>
 * DataClassArray arrayClass = (DataClassArray) dataClass;
 * 
 * int length = (int) arrayClass.size().invoke(arrayData);
 * Object[] outputArray = new Object[length];
 * Object iterator = arrayClass.iterator().invoke(arrayData);
 * 
 * DataClassRecord arrayDataClass = arrayClass.arrayDataClass();
 * 
 * for (int x = 0; x < length; x++) {
 * 	Object av = arrayClass.get().invoke(iterator, arrayData);
 * 	outputArray[x] = toMap(arrayDataClass, av);
 * }
 * </pre>
 * 
 * Instantiating and loading values to the array:
 * 
 * <pre>
 * DataClassArray arrayClass = (DataClassArray) dataClass;
 * 
 * Object[] inputArray = (Object[]) data;
 * 
 * int length = inputArray.length; Object arrayData = arrayClass.constructor().invoke(length);
 * Object iterator = arrayClass.iterator().invoke(arrayData);
 * 
 * DataClassRecord arrayDataClass = arrayClass.arrayDataClass();
 * 
 * for (int x = 0; x < length; x++) { arrayClass.put().invoke(iterator, arrayData,
 * toObject(arrayDataClass, inputArray[x])); }
 * 
 * v = arrayData;
 * 
 * 
 * 
 */
public class DataClassArray extends DataClassRecord {

	// data class.
	private final DataClassRecord arrayDataClass;

	// int size( <Array> );
	private final MethodHandle size;

	// <Iterator> iterator( <Array> );
	private final MethodHandle iterator;

	// void put( <Iterator>, <Array>, <Value> );
	private final MethodHandle put;

	// <data> get( <Iterator> iterator, <Array> );
	private final MethodHandle get;

	public DataClassArray(Class<?> targetType, Class<?> serialType, MethodHandle creator, MethodHandle constructor,
			MethodHandle toData, MethodHandle toObject, DataClassComponent[] fields, DataType dataType,
			DataClassRecord arrayDataClass, Object bridge) throws NoSuchMethodException, IllegalAccessException {
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

	public DataClassRecord arrayDataClass() {
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
