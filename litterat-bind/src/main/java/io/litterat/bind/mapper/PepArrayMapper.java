/*
 * Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.litterat.bind.mapper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassComponent;
import io.litterat.bind.DataBindException;

/**
 * Sample showing how to use the Pep library to convert an Object to/from
 * Object[]
 *
 * This is intentionally using MethodHandles throughout to demonstrate
 * pre-building method handles for each type. This is the method likely to be
 * used by serialization libraries to improve performance.
 *
 * TODO add try/catch/throw around conversions TODO deal with arrays TODO deal
 * with null values correctly
 *
 */
public class PepArrayMapper {

	private final DataBindContext context;

	private final Map<DataClassRecord, ArrayFunctions> functionCache;

	private static class ArrayFunctions {

		// Converts from Object[] to targetClass. Has signature: Object[] project( T
		// object );
		public final MethodHandle toArray;

		// constructs, calls setters and embeds. Has signature: T embed( Object[] values
		// ).
		public final MethodHandle toObject;

		public ArrayFunctions(MethodHandle toArray, MethodHandle toObject) {
			this.toArray = toArray;
			this.toObject = toObject;
		}
	}

	public PepArrayMapper(DataBindContext context) {
		this.context = context;
		this.functionCache = new HashMap<>();

	}

	private ArrayFunctions getFunctions(DataClassRecord dataClass) throws DataBindException {
		ArrayFunctions af = functionCache.get(dataClass);
		if (af == null) {

			MethodHandle toArray = createToDataFunction(dataClass);
			MethodHandle toObject = createToObjectFunction(dataClass);

			af = new ArrayFunctions(toArray, toObject);
			functionCache.put(dataClass, af);
		}
		return af;
	}

	/**
	 * Convenience function. Takes the target object of this descriptor and return
	 * an object array.
	 *
	 * @param o target object instance to project to object[]
	 * @return values from target object
	 * @throws Throwable any failure from the project function.
	 */
	public Object[] toArray(Object o) throws Throwable {
		return toArray(context.getDescriptor(o.getClass()), o);
	}

	public Object[] toArray(DataClassRecord clss, Object o) throws Throwable {
		Objects.requireNonNull(o);

		ArrayFunctions af = getFunctions(clss);

		// create and fill array with project instance.
		return (Object[]) af.toArray.invoke(o);
	}

	/**
	 * Convenience function. Takes an array of values based on the field types and
	 * returns the target object.
	 *
	 * @param values object values to embed into target object.
	 * @return recreated target object.
	 * @throws Throwable any failure from the embed function.
	 */
	public <T> T toObject(Class<T> clss, Object[] values) throws Throwable {
		return toObject(context.getDescriptor(clss), values);
	}

	public <T> T toObject(DataClassRecord clss, Object[] values) throws Throwable {
		Objects.requireNonNull(clss);
		Objects.requireNonNull(values);

		ArrayFunctions af = getFunctions(clss);

		// create the embedded object.
		return (T) af.toObject.invoke(values);
	}

	/**
	 * Creates the embed method handle. Will create the serial instance, call
	 * setters, and class the embed method handle to create the target object in a
	 * single call. This is equivalent to:
	 *
	 * // fields mapped as required from value array. T t = new EmbedClass(
	 * values[0], values[1], ... );
	 *
	 * // calls the embed function on the object. return toObject( t );
	 *
	 * @param objectConstructor
	 * @param fields
	 * @return a single MethodHandle to generate target object from Object[]
	 * @throws DataBindException
	 */
	private MethodHandle createToObjectFunction(DataClassRecord dataClass) throws DataBindException {

		// return MethodHandles.collectArguments(dataClass.toObject(), 0, create);

		MethodHandle result = null;
		if (dataClass.isAtom()) {

			// Use identity here because calling function wraps the toObject method.
			// identity( dataObject ):dataObject
			result = dataClass.toObject().asType(dataClass.toObject().type().changeParameterType(0, Object.class));

		} else if (dataClass.isData()) {

			result = dataClass.constructor();

			DataClassComponent[] fields = dataClass.dataComponents();
			for (int x = 0; x < dataClass.dataComponents().length; x++) {
				DataClassComponent field = fields[x];

				int inputIndex = x;

				// (values[],int) -> values[int]
				MethodHandle arrayGetter = MethodHandles.arrayElementGetter(Object[].class);

				// () -> inputIndex
				MethodHandle index = MethodHandles.constant(int.class, inputIndex);

				DataClassRecord fieldDataClass = field.dataClass();

				// (values[]) -> values[inputIndex]
				MethodHandle arrayIndexGetter = MethodHandles.collectArguments(arrayGetter, 1, index);
				// .asType(MethodType.methodType(fieldDataClass.dataClass(), Object[].class));

				// TODO Needs to be check for nulls before calling toObject or array to Object.

				// Pass the object through toObject if it isn't an atom.
				// (values[]) -> toObject(values[x])

				arrayIndexGetter = MethodHandles.collectArguments(createToObjectFunction(fieldDataClass), 0,
						arrayIndexGetter);

				// (values[],int,Object):void -> values[int] = Object;
				MethodHandle arraySetter = MethodHandles.arrayElementSetter(Object[].class);

				// (values[],Object):void -> values[x] = Object;
				MethodHandle arrayIndexSetter = MethodHandles.collectArguments(arraySetter, 1, index);

				// (values[],Object[]):void -> values[x] = toObject(values[x]);
				MethodHandle arrayValueSetter = MethodHandles.collectArguments(arrayIndexSetter, 1,
						arrayIndexGetter.asType(MethodType.methodType(Object.class, Object[].class)));

				int[] permuteInput = new int[2];
				MethodHandle combined = MethodHandles.permuteArguments(arrayValueSetter,
						MethodType.methodType(void.class, Object[].class), permuteInput);

				result = MethodHandles.foldArguments(result, combined);

			}

			// (Object[]) -> toObject( ctor(Object[]).setValues(Object[]) )
			result = MethodHandles.collectArguments(dataClass.toObject(), 0, result);

			result = result.asType(result.type().changeReturnType(dataClass.typeClass()));

		} else if (dataClass.isArray()) {

			// toObject( Object[] ):<array>
			try {
				DataClassArray dataArrayClass = (DataClassArray) dataClass;

				MethodHandle valueToData = createToObjectFunction(dataArrayClass.arrayDataClass());

				ArrayToObjectBridge bridge = new ArrayToObjectBridge(dataArrayClass, valueToData);

				result = MethodHandles.lookup().findVirtual(ArrayToObjectBridge.class, "toObject",
						MethodType.methodType(Object.class, Object[].class)).bindTo(bridge);

				result = result.asType(MethodType.methodType(dataArrayClass.typeClass(), Object.class));

			} catch (NoSuchMethodException | IllegalAccessException e) {
				throw new DataBindException("failed to build bridge for array", e);
			}
		} else if (dataClass.isBase()) {
			throw new DataBindException("not implemented");
		}

		return result;
	}

	/**
	 * create project takes a target object and returns an Object[] of values. Not
	 * yet complete. Haven't worked out how to re-use the Object[] in return value.
	 *
	 * // Project the instance to the embedded version. EmbeddedClass e =
	 * project.invoke(o)
	 *
	 * // Extract the values from the projected object. Object[] values = new
	 * Object[fields.length];
	 *
	 * // Call the various accessors to fill in the array and return values. return
	 * getter.invoke(e, values );
	 *
	 * @param fields
	 * @return
	 * @throws DataBindException
	 */
	private MethodHandle createToDataFunction(DataClassRecord dataClass) throws DataBindException {

		MethodHandle returnArray = null;

		if (dataClass.isAtom()) {
			returnArray = dataClass.toData();
		} else if (dataClass.isData()) {

			// (int):Object[] -> new Object[int]
			MethodHandle createArray = MethodHandles.arrayConstructor(Object[].class);

			// (int):length -> fields.length
			MethodHandle index = MethodHandles.constant(int.class, dataClass.dataComponents().length);

			// ():Object[] -> new Object[fields.length]
			MethodHandle arrayCreate = MethodHandles.collectArguments(createArray, 0, index);

			// (Object[],serialClass):void -> getters(Object[],serialClass)
			MethodHandle getters = createProjectGetters(dataClass);

			// (Object[],targetClass):void -> getters(Object[], project(targetClass))
			MethodHandle projectGetters = MethodHandles.collectArguments(getters, 1, dataClass.toData());

			// ():Object[] -> return new Object[fields.length];
			returnArray = MethodHandles.collectArguments(projectGetters, 0, arrayCreate);
		} else if (dataClass.isArray()) {
			try {

				DataClassArray arrayClass = (DataClassArray) dataClass;

				MethodHandle arrayToData = createToDataFunction(arrayClass.arrayDataClass());

				ObjectToArrayBridge bridge = new ObjectToArrayBridge(arrayClass, arrayToData);

				MethodHandle bridgeToData = MethodHandles.lookup().findVirtual(ObjectToArrayBridge.class, "toData",
						MethodType.methodType(Object[].class, Object.class)).bindTo(bridge);

				returnArray = bridgeToData.asType(MethodType.methodType(Object.class, dataClass.typeClass()));
				// fieldBox = MethodHandles.collectArguments(bridgeToData, 0, fieldBox);
			} catch (NoSuchMethodException | IllegalAccessException e) {
				throw new DataBindException("failed to build array bridge", e);
			}
		} else {
			throw new DataBindException("not implemented");
		}

		return returnArray;
	}

	/**
	 * Create the getters
	 *
	 * @param fields
	 * @return
	 * @throws DataBindException
	 */
	private MethodHandle createProjectGetters(DataClassRecord dataClass) throws DataBindException {

		// (object[]):object[] -> return object[];
		MethodHandle identity = MethodHandles.identity(Object[].class);

		// (Object[], embedClass):object[] -> return object[];
		MethodHandle result = MethodHandles.dropArguments(identity, 1, dataClass.dataClass());

		DataClassComponent[] fields = dataClass.dataComponents();
		for (int x = 0; x < fields.length; x++) {

			DataClassComponent field = fields[x];
			int outputIndex = x;

			// (value[],x, v) -> value[x] = v
			MethodHandle arraySetter = MethodHandles.arrayElementSetter(Object[].class);

			// () -> outputIndex
			MethodHandle index = MethodHandles.constant(int.class, outputIndex);

			// (value[],v) -> value[inputIndex] = v
			MethodHandle arrayIndexSetter = MethodHandles.collectArguments(arraySetter, 1, index);

			DataClassRecord fieldDataClass = field.dataClass();

			// (object) -> (Object) object.getter()
			MethodHandle fieldBox = field.accessor();

			// TODO needs to deal with null here.

			if (field.dataClass().typeClass() == dataClass.typeClass()) {
				throw new IllegalArgumentException("Recursive structures not yet supported for array mapper");
			}

			fieldBox = MethodHandles.collectArguments(createToDataFunction(fieldDataClass), 0, fieldBox)
					.asType(MethodType.methodType(Object.class, dataClass.dataClass()));

			// (value[],object) -> value[inputIndex] = object.getter()
			MethodHandle arrayValueSetter = MethodHandles.collectArguments(arrayIndexSetter, 1, fieldBox);

			// add to list of getters.
			result = MethodHandles.foldArguments(result, arrayValueSetter);

		}

		// (object[]):object[] -> ... callGetters(object[]); ... return object[];
		return result;
	}

	// The ArrayBridge is an easy way out of not writing loops using MethodHandles.
	// It can be done but is more difficult than above.
	private class ObjectToArrayBridge {

		private final DataClassArray arrayClass;
		private final MethodHandle dataToObject;

		public ObjectToArrayBridge(DataClassArray fieldDataClass, MethodHandle dataToObject) {
			this.arrayClass = fieldDataClass;

			this.dataToObject = MethodHandles.collectArguments(
					dataToObject
							.asType(dataToObject.type().changeParameterType(0, arrayClass.get().type().returnType())),
					0, arrayClass.get());
		}

		@SuppressWarnings("unused")
		public Object[] toData(Object v) throws DataBindException {
			try {

				Object arrayData = v;
				int length = (int) arrayClass.size().invoke(arrayData);
				Object[] outputArray = new Object[length];
				Object iterator = arrayClass.iterator().invoke(arrayData);

				DataClassRecord arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {
					outputArray[x] = dataToObject.invoke(iterator, arrayData);
				}

				return outputArray;

			} catch (Throwable e) {
				throw new DataBindException("Failed to convert arra", e);
			}
		}

	}

	private class ArrayToObjectBridge {

		private final DataClassArray arrayClass;
		private final MethodHandle arrayToObject;

		public ArrayToObjectBridge(DataClassArray arrayClass, MethodHandle arrayToObject) {
			this.arrayClass = arrayClass;
			this.arrayToObject = arrayToObject;

			// TODO revisit this. Difficult to align types correctly. Might need a explicit
			// cast.
			// this.arrayToObject = MethodHandles.collectArguments(arrayClass.put(), 2,
			// arrayToObject.asType(MethodType.methodType(Object.class,
			// arrayToObject.type().parameterType(0))));
		}

		@SuppressWarnings("unused")
		public Object toObject(Object[] s) throws DataBindException {
			try {
				Object[] inputArray = s;

				int length = inputArray.length;
				Object arrayData = arrayClass.constructor().invoke(length);
				Object iterator = arrayClass.iterator().invoke(arrayData);

				DataClassRecord arrayDataClass = arrayClass.arrayDataClass();

				for (int x = 0; x < length; x++) {

					Object v = arrayToObject.invoke(inputArray[x]);

					arrayClass.put().invoke(iterator, arrayData, v);
					// arrayToObject.invoke(iterator, arrayData, inputArray[x]);

				}

				return arrayData;
			} catch (Throwable e) {
				throw new DataBindException("Failed to convert arra", e);
			}
		}

	}
}
