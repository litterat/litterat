package io.litterat.bind.describe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.invoke.VarHandle.AccessMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;

/**
 * 
 * This class is used to generate the MethodHandle collection for the DataClassArray type. It
 * generates MethodHandles that allows a data marshaler to interact with both java arrays and
 * Collections using the same "interface" accessed through the MethodHandles. See the DataClassArray
 * for how to interact using the MethodHandles.
 *
 */

public class ArrayAccessBridge {

	private static Map<Class<?>, Class<?>> collectionInterfaces = new HashMap<>();

	static {
		collectionInterfaces.put(List.class, ArrayList.class);
		collectionInterfaces.put(BlockingDeque.class, LinkedBlockingDeque.class);
		collectionInterfaces.put(Deque.class, ArrayDeque.class);
		collectionInterfaces.put(Queue.class, ArrayDeque.class);
		collectionInterfaces.put(Set.class, HashSet.class);
	}

	private final Class<?> targetClass;
	private final DataClass arrayDataClass;

	public ArrayAccessBridge(Class<?> targetClass, DataClass arrayDataClass) {
		this.targetClass = targetClass;
		this.arrayDataClass = arrayDataClass;
	}

	/**
	 * 
	 * Returns a MethodHandle that accepts an integer and returns a new empty targetClass instance.
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws DataBindException
	 */
	public MethodHandle constructor()
			throws IllegalAccessException, NoSuchMethodException, SecurityException, DataBindException {
		MethodHandle constructorHandle;
		if (targetClass.isArray()) {
			constructorHandle = MethodHandles.arrayConstructor(targetClass);
		} else if (Collection.class.isAssignableFrom(targetClass)) {

			// If a field uses a non-specific collection type such as List there's no way
			// in knowing the implementation that is expected. This could be fixed with an annotation.
			// For now, use a default concrete type.
			if (collectionInterfaces.containsKey(targetClass)) {
				Class<?> implementationClass = collectionInterfaces.get(targetClass);

				constructorHandle = MethodHandles.lookup()
						.unreflectConstructor(implementationClass.getConstructor(int.class));
			} else {

				// There are some collection types that do not have a constructor that takes an int as a parameter.
				// These are things like TreeSet and custom Collection implementations. This will
				// need to expanded in future to allow other user custom constructors to be supplied.
				constructorHandle = MethodHandles.lookup().unreflectConstructor(targetClass.getConstructor(int.class));
			}
		} else {
			throw new DataBindException("Not recognised array class");
		}

		return constructorHandle;
	}

	/**
	 * Returns a MethodHandle that given an array type (Java array or collection) returns the size as an
	 * int.
	 * 
	 * 
	 * 
	 * @return MethodHandle with signature size( array ):int
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws DataBindException
	 */
	public MethodHandle getSizeMethodHandle() throws NoSuchMethodException, IllegalAccessException, DataBindException {
		MethodHandle sizeHandle;

		if (targetClass.isArray()) {
			sizeHandle = MethodHandles.arrayLength(targetClass);
		} else if (Collection.class.isAssignableFrom(targetClass)) {
			sizeHandle = MethodHandles.lookup().findVirtual(targetClass, "size", MethodType.methodType(int.class));
		} else {
			throw new DataBindException("Not recognised array class");
		}

		return sizeHandle;
	}

	public static class IntIterator {
		public int pos;
	}

	/**
	 * Returns an iterator that can be used for the get/put method handles. The object the MethodHandle
	 * returns does not necessarily return an Iterator object. For Java array objects it returns an
	 * IntIterator. The returned value should be passed in as the second argument of the get/put
	 * MethodHandles.
	 * 
	 * @return MethodHandle iter( array ) iterObject;
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws DataBindException
	 */
	public MethodHandle getIteratorMethodHandle()
			throws IllegalAccessException, NoSuchMethodException, SecurityException, DataBindException {
		MethodHandle iteratorHandle;

		if (targetClass.isArray()) {
			// return constructor for IntIterator;

			// ():IntIterator -> return new IntIterator();
			MethodHandle newIntIterator = MethodHandles.lookup()
					.unreflectConstructor(IntIterator.class.getDeclaredConstructor());

			// (<array>):IntIterator
			iteratorHandle = MethodHandles.dropArguments(newIntIterator, 0, targetClass);

		} else if (Collection.class.isAssignableFrom(targetClass)) {
			iteratorHandle = MethodHandles.publicLookup().findVirtual(targetClass, "iterator",
					MethodType.methodType(Iterator.class));
		} else {
			throw new DataBindException("Not recognised array class");
		}

		return iteratorHandle;
	}

	/**
	 * Returns a MethodHandle used to access the individual objects from the array.
	 * 
	 * @return returns MethodHandle with signature get( array, iter ):value
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws DataBindException
	 */
	public MethodHandle getIteratorGetMethodHandle()
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, DataBindException {
		MethodHandle getHandle;

		if (targetClass.isArray()) {

			// This is equivalent to return array[iterator.pos++];

			// (IntIterator) -> iterator.pos
			VarHandle posHandle = MethodHandles.lookup().findVarHandle(IntIterator.class, "pos", int.class);

			// (IntIterator, int) -> iterator.pos+x
			MethodHandle posGetAndAdd = posHandle.toMethodHandle(AccessMode.GET_AND_ADD);

			// 1
			MethodHandle constOne = MethodHandles.constant(int.class, 1);

			// (IntIterator) -> iterator.pos+=1
			MethodHandle getAndInc = MethodHandles.foldArguments(posGetAndAdd, 1, constOne);

			// (<array>, x) -> array[ x ]
			MethodHandle arrayGetter = MethodHandles.arrayElementGetter(targetClass);

			// (<array>, IntIterator) -> array[ iterator.pos++ ]
			getHandle = MethodHandles.filterArguments(arrayGetter, 1, getAndInc);

		} else if (Collection.class.isAssignableFrom(targetClass)) {

			// equivalent to
			// if (iterator.hasNext())
			// return iterator.next();
			// else
			// return null;

			// (Iterator) -> iterator.hasNext
			MethodHandle hasNext = MethodHandles.publicLookup().findVirtual(Iterator.class, "hasNext",
					MethodType.methodType(boolean.class));

			// (Iterator) -> iterator.next
			MethodHandle next = MethodHandles.publicLookup().findVirtual(Iterator.class, "next",
					MethodType.methodType(Object.class));

			// ():null -> return null;
			MethodHandle noResult = MethodHandles.constant(Object.class, null);

			// (Iterator):null -> return null;
			MethodHandle returnNull = MethodHandles.dropArguments(noResult, 0, Iterator.class);

			// (Iterator) -> if (iterator.hasNext) return iterator.next() else return null.
			MethodHandle ifCheck = MethodHandles.guardWithTest(hasNext, next, returnNull);

			// (Collection, Iterator) -> if (iterator.hasNext) return iterator.next() else return null.
			getHandle = MethodHandles.dropArguments(ifCheck, 0, targetClass);

			// May need to spread iterator.
			// need to add and ignore additonal parameter of collection.
		} else {
			throw new DataBindException("Not recognised array class");
		}

		return getHandle;
	}

	/**
	 * 
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws DataBindException
	 */
	public MethodHandle getIteratorPutMethodHandle()
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, DataBindException {
		MethodHandle putHandle = null;

		if (targetClass.isArray()) {
			// This is equivalent to array[iterator.pos++] = value;

			// (IntIterator) -> iterator.pos
			VarHandle posHandle = MethodHandles.lookup().findVarHandle(IntIterator.class, "pos", int.class);

			// (IntIterator, int) -> iterator.pos+x
			MethodHandle posGetAndAdd = posHandle.toMethodHandle(AccessMode.GET_AND_ADD);

			// 1
			MethodHandle constOne = MethodHandles.constant(int.class, 1);

			// (IntIterator) -> iterator.pos+=1
			MethodHandle getAndInc = MethodHandles.foldArguments(posGetAndAdd, 1, constOne);

			// (<array>, x) -> array[ x ]
			MethodHandle arraySetter = MethodHandles.arrayElementSetter(targetClass);

			// (<array>, IntIterator, value ) -> array[ iterator.pos++ ] = value
			putHandle = MethodHandles.filterArguments(arraySetter, 1, getAndInc);

		} else if (Collection.class.isAssignableFrom(targetClass)) {

			// ( collection, value ):boolean -> collection.add( value )
			MethodHandle addHandle = MethodHandles.publicLookup().findVirtual(targetClass, "add",
					MethodType.methodType(boolean.class, Object.class));

			// ( collection, value ):boolean -> collection.add( value )
			putHandle = MethodHandles.dropArguments(addHandle, 1, Iterator.class);

		} else {
			throw new DataBindException("Not recognised array class");
		}
		return putHandle;
	}
}
