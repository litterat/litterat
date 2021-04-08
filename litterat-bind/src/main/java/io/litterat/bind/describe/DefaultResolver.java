/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
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
package io.litterat.bind.describe;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

import io.litterat.bind.Atom;
import io.litterat.bind.Data;
import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindContextResolver;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataBridge;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.bind.DataClassUnion;
import io.litterat.bind.DataOrder;
import io.litterat.bind.Field;
import io.litterat.bind.ToData;
import io.litterat.bind.Union;

public class DefaultResolver implements DataBindContextResolver {

	private static final String TODATA_METHOD = "toData";
	private static final String TOOBJECT_METHOD = "toObject";

	private final boolean allowSerializable;

	private final boolean allowAny;

	public DefaultResolver(boolean allowSerializable, boolean allowAny) {
		this.allowSerializable = allowSerializable;
		this.allowAny = allowAny;
	}

	@Override
	public DataClass resolve(DataBindContext context, Class<?> targetClass, Type parameterizedType)
			throws DataBindException {
		DataClass descriptor = null;

		if (isUnion(targetClass)) {
			descriptor = resolveUnion(context, targetClass);
		} else if (isArray(targetClass)) {
			descriptor = resolveArray(context, targetClass, parameterizedType);
		} else if (isAtom(targetClass)) {
			descriptor = resolveAtom(context, targetClass);
		} else if (isRecord(targetClass)) {
			descriptor = resolveRecord(context, targetClass);
		} else {
			throw new DataBindException(
					String.format("Unable to find a valid data conversion for class: %s", targetClass));
		}
		return descriptor;
	}

	private boolean isUnion(Class<?> targetClass) {
		if (targetClass.isInterface() && !Collection.class.isAssignableFrom(targetClass)) {
			return true;
		}

		// Array classes are abstract and we don't want them.
		if (Modifier.isAbstract(targetClass.getModifiers())) {

			if (targetClass.isArray()) {
				// this is classed as an array, not a union.
				return false;
			} else if (Collection.class.isAssignableFrom(targetClass)) {
				// this is classed as an array, not an interface.
				return false;
			}

			return true;
		} else if (targetClass.isInterface()) {

			// Interface needs to be marked with @Data or Serializable.
			Data pepData = targetClass.getAnnotation(Data.class);
			if (pepData != null) {
				return true;
			}

			if (allowSerializable) {
				if (Serializable.class.isAssignableFrom(targetClass)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isRecord(Class<?> targetClass) {

		// if class has annotation this is a tuple.
		Data pepData = targetClass.getAnnotation(Data.class);
		if (pepData != null) {
			return true;
		}

		// Check for annotation on constructor.
		Constructor<?>[] constructors = targetClass.getConstructors();
		for (Constructor<?> constructor : constructors) {
			pepData = constructor.getAnnotation(Data.class);
			if (pepData != null) {
				return true;
			}
		}

		// This is to look at static methods
		Method[] methods = targetClass.getDeclaredMethods();
		for (Method method : methods) {

			pepData = method.getAnnotation(Data.class);
			if (Modifier.isStatic(method.getModifiers()) && pepData != null) {
				return true;
			}
		}

		// Class has implemented ToData so exports/imports a data class.
		if (ToData.class.isAssignableFrom(targetClass)) {
			return true;
		}

		// We can try and see if we can serialize the class. Results will vary.
		if (allowSerializable) {
			if (Serializable.class.isAssignableFrom(targetClass)) {
				return true;
			}
		}

		// Sure, we can try to serialize anything. but ¯\_("/)_/¯
		if (allowAny) {
			return true;
		}

		return false;
	}

	private boolean isArray(Class<?> targetClass) {

		if (targetClass.isArray() || Collection.class.isAssignableFrom(targetClass)) {
			return true;
		}

		return false;
	}

	private boolean isAtom(Class<?> targetClass) {

		if (targetClass.isPrimitive()) {
			return true;
		}

		// Check for class annoation
		Atom pepAtom = targetClass.getAnnotation(Atom.class);
		if (pepAtom != null) {
			return true;
		}

		// Check for annotation on constructor.
		Constructor<?>[] constructors = targetClass.getConstructors();
		for (Constructor<?> constructor : constructors) {
			pepAtom = constructor.getAnnotation(Atom.class);
			if (pepAtom != null) {
				return true;
			}
		}

		// This is to look at static methods
		Method[] methods = targetClass.getDeclaredMethods();
		for (Method method : methods) {

			pepAtom = method.getAnnotation(Atom.class);
			if (Modifier.isStatic(method.getModifiers()) && pepAtom != null) {
				return true;
			}
		}

		// Allow enums to be serialized.
		if (allowSerializable || allowAny) {
			if (targetClass.isEnum()) {
				return true;
			}
		}

		return false;
	}

	private DataClassUnion resolveUnion(DataBindContext context, Class<?> targetClass) throws DataBindException {

		// TODO It could be useful to map other classes to this data class.
		return new DataClassUnion(targetClass);
	}

	// TODO If the targetClass is a Collection it isn't possible get the generic
	// parameter as the class will erazed.
	// The resolution should use a Type which can get the information from the field
	// or parameter and pass that through.
	private DataClassArray resolveArray(DataBindContext context, Class<?> targetClass, Type parameterizedType)
			throws DataBindException {
		DataClassArray descriptor = null;

		try {

			DataClass arrayDataClass;

			// Find the type of the Array collection.
			if (targetClass.isArray()) {

				// Java arrays type is easily available via reflection.
				arrayDataClass = context.getDescriptor(targetClass.getComponentType());

			} else if (Collection.class.isAssignableFrom(targetClass)) {

				// We need the parameterizedType as type erasure means we can only get Collection type
				// from certain places.
				if (!(parameterizedType instanceof ParameterizedType)) {
					throw new DataBindException("Collection must provide parameterized type information");
				}

				Type paramType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
				if (paramType instanceof Class) {
					arrayDataClass = context.getDescriptor((Class<?>) paramType);
				} else if (paramType instanceof ParameterizedType) {
					ParameterizedType arrayParamType = (ParameterizedType) paramType;
					arrayDataClass = context.getDescriptor((Class<?>) arrayParamType.getRawType(), arrayParamType);
				} else {
					throw new DataBindException("Unrecognized parameterized type");
				}

			} else {
				throw new DataBindException("Not recognised array class");
			}

			ArrayAccessBridge arrayBridge = new ArrayAccessBridge(targetClass, arrayDataClass);

			descriptor = new DataClassArray(targetClass, arrayDataClass, arrayBridge.constructor(),
					arrayBridge.getSizeMethodHandle(), arrayBridge.getIteratorMethodHandle(),
					arrayBridge.getIteratorGetMethodHandle(), arrayBridge.getIteratorPutMethodHandle());

		} catch (IllegalAccessException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
			throw new DataBindException("Failed to get array descriptor", e);
		}

		return descriptor;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Set<Class> WRAPPER_TYPES = new HashSet(Arrays.asList(Boolean.class, Character.class,
			Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Void.class));

	private boolean isPrimitive(Class<?> targetClass) {
		if (targetClass.isPrimitive() || WRAPPER_TYPES.contains(targetClass)) {
			return true;
		}
		return false;
	}

	private DataClass resolveAtom(DataBindContext context, Class<?> targetClass) throws DataBindException {
		DataClass descriptor = null;

		try {
			if (targetClass.isPrimitive()) {
				// primitives should already be registered, but just incase.
				MethodHandle identity = MethodHandles.identity(targetClass);
				descriptor = new DataClassRecord(targetClass, targetClass, identity, identity, identity,
						new DataClassField[0]);
			}

			// Check for annotation on constructor.
			Constructor<?>[] constructors = targetClass.getConstructors();
			for (Constructor<?> constructor : constructors) {
				Atom pepAtom = constructor.getAnnotation(Atom.class);
				if (pepAtom != null) {
					Parameter[] params = constructor.getParameters();
					if (params.length != 1 || !isPrimitive(params[0].getType())) {
						throw new DataBindException(
								String.format("Atom must have single primitive argument", targetClass));
					}

					Class<?> dataClass = params[0].getType();
					MethodHandle identity = MethodHandles.identity(dataClass);

					MethodHandle toObject = MethodHandles.lookup().unreflectConstructor(constructor);

					// TODO Should do additional checks. Is return type same as constructor type.
					// Also should check for ToData interface implementation or @PepAtom on specific
					// method as could be different
					// ways to say which method is toData.
					Method toDataMethod = targetClass.getDeclaredMethod(TODATA_METHOD);

					MethodHandle toData = MethodHandles.lookup().unreflect(toDataMethod);

					descriptor = new DataClassRecord(targetClass, dataClass, identity, toData, toObject,
							new DataClassField[0]);
					break;
				}
			}

			// This is to look at static methods
			Method[] methods = targetClass.getDeclaredMethods();
			for (Method method : methods) {

				Atom pepAtom = method.getAnnotation(Atom.class);
				if (Modifier.isStatic(method.getModifiers()) && pepAtom != null) {
					Parameter[] params = method.getParameters();
					if (params.length != 1 || !isPrimitive(params[0].getType())) {
						throw new DataBindException("Atom static method must have a single primitive value");
					}

					MethodHandle toObject = MethodHandles.publicLookup().unreflect(method);

					Class<?> param = params[0].getType();

					MethodHandle toData = null;
					// Requires an accessor with the same type.
					for (Method accessorMethod : methods) {

						Atom accessorAtom = accessorMethod.getAnnotation(Atom.class);
						if (accessorAtom != null && !Modifier.isStatic(accessorMethod.getModifiers())) {
							if (accessorMethod.getReturnType() != param) {
								throw new DataBindException(
										"Atom accessor method must have a single primitive value as same type as static constructor");
							}
							toData = MethodHandles.publicLookup().unreflect(accessorMethod);
							break;
						}

					}

					if (toData == null) {
						throw new DataBindException("Atom accessor @Atom annotation not found");
					}

					descriptor = new DataClassAtom(targetClass, param, toData, toObject);

				}
			}

			// Allow enums to be serialized to their String value if using default
			// serialization.
			Atom enumAtom = targetClass.getAnnotation(Atom.class);
			if (targetClass.isEnum() && (allowSerializable || allowAny || enumAtom != null)) {

				EnumBridge bridge = new EnumBridge(targetClass);

				MethodHandle toObject = MethodHandles.lookup()
						.findVirtual(EnumBridge.class, TOOBJECT_METHOD, MethodType.methodType(Enum.class, String.class))
						.bindTo(bridge).asType(MethodType.methodType(targetClass, String.class));
				MethodHandle toData = MethodHandles.lookup()
						.findVirtual(EnumBridge.class, TODATA_METHOD, MethodType.methodType(String.class, Enum.class))
						.bindTo(bridge).asType(MethodType.methodType(String.class, targetClass));

				descriptor = new DataClassAtom(targetClass, String.class, toData, toObject);

			}
		} catch (SecurityException | IllegalAccessException | NoSuchMethodException | DataBindException e) {
			throw new DataBindException("Failed to get atom descriptor", e);
		}

		return descriptor;
	}

	private DataClassRecord resolveRecord(DataBindContext context, Class<?> targetClass) throws DataBindException {
		DataClassRecord descriptor = null;

		try {
			// If the class if exporting/importing a dataclass.
			if (ToData.class.isAssignableFrom(targetClass)) {

				for (Type genericInterface : targetClass.getGenericInterfaces()) {

					if (genericInterface instanceof ParameterizedType
							&& ((ParameterizedType) genericInterface).getRawType() == ToData.class) {

						// ToData has one parameter so this should be safe.
						Class<?> dataType = (Class<?>) ((ParameterizedType) genericInterface)
								.getActualTypeArguments()[0];

						// Recursively get hold of the data class descriptor.
						// Might need to revisit this. Expecting a record and while throw if it isn't. :(
						DataClassRecord tupleData = (DataClassRecord) context.getDescriptor(dataType);

						// We require that a constructor be present that takes the data class as input.
						MethodHandle toObject = MethodHandles.publicLookup()
								.unreflectConstructor(targetClass.getConstructor(dataType));

						// The class implements ToData so get the method.
						MethodHandle toData = MethodHandles.publicLookup()
								.unreflect(targetClass.getMethod(TODATA_METHOD));

						// The constructor and data components are copied from the data class.
						descriptor = new DataClassRecord(targetClass, dataType, tupleData.constructor(), toData,
								toObject, tupleData.fields());
						break;
					}

					// Should be unreachable code.
					throw new DataBindException(String.format(
							"Failed to get data descriptor. Failed to find constructor for %s", targetClass.getName()));
				}

			} else {

				// This is a data class so need to decide what is/isn't data.
				List<ComponentInfo> components = new ArrayList<>();

				// get the constructor.
				Constructor<?> ctor = getConstructor(targetClass);

				// Use the Immutable finder to discover any immutable fields for the class.
				ImmutableFinder describer = new ImmutableFinder(context);
				describer.findComponents(targetClass, ctor, components);

				// Use the getter/setter finder to discover any fields with matching getters and
				// setters.
				GetSetFinder getSetFinder = new GetSetFinder();
				getSetFinder.findComponents(targetClass, ctor, components);

				// This is a data class so toObject/toData is identity functions.
				MethodHandle toObject = MethodHandles.identity(targetClass);
				MethodHandle toData = MethodHandles.identity(targetClass);

				// Modify the order of fields if order annotation supplied.
				DataOrder dataOrder = targetClass.getAnnotation(DataOrder.class);
				if (dataOrder != null) {
					reorderFields(dataOrder, components);
				}

				// Prepare the field descriptors.
				DataClassField[] dataComponents = new DataClassField[components.size()];
				for (int x = 0; x < components.size(); x++) {
					ComponentInfo info = components.get(x);

					MethodHandle accessor = info.getReadMethod();

					MethodHandle setter = null;
					if (info.getWriteMethod() != null) {
						setter = info.getWriteMethod();
					}

					DataClassField component;

					// Will probably need a more generic way of handling paramterized types.
					Field fieldAnnotation = info.getField();

					boolean isRequired = false;
					String fieldName = info.getName();
					if (fieldAnnotation != null) {
						isRequired = fieldAnnotation.required();
						if (fieldAnnotation.name() != null && !fieldAnnotation.name().strip().equals("")) {
							fieldName = fieldAnnotation.name();
						}
					}

					if (fieldAnnotation != null && fieldAnnotation.bridge() != null
							&& fieldAnnotation.bridge() != IdentityBridge.class) {

						@SuppressWarnings("rawtypes")
						Class<? extends DataBridge> bridgeClass = fieldAnnotation.bridge();

						ParameterizedType bridgeTypes = (ParameterizedType) bridgeClass.getGenericInterfaces()[0];

						// ToData has one parameter so this should be safe.
						Type bridgeDataType = bridgeTypes.getActualTypeArguments()[0];
						Type bridgeObjectType = bridgeTypes.getActualTypeArguments()[1];

						Class<?> bridgeDataClass;
						Class<?> bridgeObjectClass;

						if (bridgeDataType instanceof Class) {
							bridgeDataClass = (Class<?>) bridgeDataType;
						} else if (bridgeDataType instanceof ParameterizedType) {
							bridgeDataClass = (Class<?>) ((ParameterizedType) bridgeDataType).getRawType();
						} else {
							throw new DataBindException("Unrecognised Type");
						}

						if (bridgeObjectType instanceof Class) {
							bridgeObjectClass = (Class<?>) bridgeObjectType;
						} else if (bridgeObjectType instanceof ParameterizedType) {
							bridgeObjectClass = (Class<?>) ((ParameterizedType) bridgeObjectType).getRawType();
						} else {
							throw new DataBindException("Unrecognised Type");
						}

						// Recursively get hold of the data class descriptor.
						DataClass tupleData = context.getDescriptor(bridgeDataClass, bridgeDataType);

						if (!bridgeObjectClass.isAssignableFrom(info.getType())) {
							throw new DataBindException("Bridge object type not assignable from field type");
						}

						// TODO Currently assume Bridge classes have no state. This might need to change
						// if a bridge uses IOC framework.

						@SuppressWarnings("rawtypes")
						DataBridge bridge;
						try {
							bridge = bridgeClass.getConstructor().newInstance();
						} catch (InstantiationException | IllegalArgumentException | InvocationTargetException e) {
							throw new DataBindException("Failed to instantiate bridge", e);
						}

						// bridge.toData(dataType):type
						MethodHandle bridgeToData = MethodHandles.publicLookup().findVirtual(bridgeClass, TODATA_METHOD,
								MethodType.methodType(bridgeDataClass, bridgeObjectClass)).bindTo(bridge);

						accessor = accessor.asType(accessor.type().changeReturnType(bridgeObjectClass));

						// bridge.toData( targetClass.getOptional() ): optionalType
						accessor = MethodHandles.collectArguments(bridgeToData, 0, accessor);

						// bridge.toObject(type):dataType
						MethodHandle bridgeToObject = MethodHandles.publicLookup().findVirtual(bridgeClass,
								TOOBJECT_METHOD, MethodType.methodType(bridgeObjectClass, bridgeDataClass))
								.bindTo(bridge);

						if (setter != null) {
							setter = MethodHandles.collectArguments(setter, 1, bridgeToObject)
									.asType(MethodType.methodType(info.getType(), bridgeDataClass));
						}

						// Use Objects.nonNull(accessor()) to check if value is present.
						// Probably not hugely efficient.
						MethodHandle isPresent = MethodHandles.publicLookup()
								.findStatic(Objects.class, "nonNull",
										MethodType.methodType(boolean.class, Object.class))
								.asType(MethodType.methodType(boolean.class, bridgeDataClass));

						isPresent = MethodHandles.filterArguments(isPresent, 0, accessor);

						component = new DataClassField(x, fieldName, bridgeDataClass, tupleData, isRequired, isPresent,
								accessor, setter);
					} else if (info.getType() == Optional.class && info.getParamType() != null) {

						Class<?> optionalType = (Class<?>) info.getParamType().getActualTypeArguments()[0];

						DataClass dataClass = context.getDescriptor(optionalType);

						Lookup lookup = MethodHandles.publicLookup();

						// Optional.ofNullable(value);
						MethodHandle optionalToObject = lookup.findStatic(Optional.class, "ofNullable",
								MethodType.methodType(Optional.class, Object.class));

						// (value, elseValue ) -> value.orElse(elseValue);
						MethodHandle optionalToData = lookup.findVirtual(Optional.class, "get",
								MethodType.methodType(Object.class));

						// bridge.toData( targetClass.getOptional() ): optionalType
						MethodHandle optionalObject = MethodHandles.collectArguments(optionalToData, 0, accessor)
								.asType(MethodType.methodType(optionalType, targetClass));

						if (setter != null) {
							setter = MethodHandles.collectArguments(setter, 1, optionalToObject)
									.asType(MethodType.methodType(optionalType, Optional.class));
						}

						// (optional) -> optional.isPresent();
						MethodHandle isPresent = lookup.findVirtual(Optional.class, "isPresent",
								MethodType.methodType(boolean.class));

						isPresent = MethodHandles.collectArguments(isPresent, 0, accessor)
								.asType(MethodType.methodType(boolean.class, targetClass));

						// By definition optional is not required.
						isRequired = false;

						// TODO the constructor parameter will need to pass through the bridge.
						component = new DataClassField(x, fieldName, optionalType, dataClass, isRequired, isPresent,
								optionalObject, setter);

					} else if (info.getType() == OptionalInt.class
							|| info.getType() == OptionalLong.class | info.getType() == OptionalDouble.class) {

						// This will be one of OptionalInt, OptionalLong or OptionalDouble.
						Class<?> optionalClass = info.getType();

						// work out which Optional class this is by getting return type of orElseThrow.
						Class<?> optionalType = optionalClass.getDeclaredMethod("orElseThrow").getReturnType();
						Class<?> optionalNullableType = null;
						if (optionalType == int.class) {
							optionalNullableType = Integer.class;
						} else if (optionalType == long.class) {
							optionalNullableType = Long.class;
						} else if (optionalType == double.class) {
							optionalNullableType = Double.class;
						}

						// Get data class for int, long or double.
						DataClass dataClass = context.getDescriptor(optionalNullableType);

						Lookup lookup = MethodHandles.publicLookup();

						// Construct the correct getAs method name.
						String getAsMethod = "getAs" + optionalType.getSimpleName().substring(0, 1).toUpperCase()
								+ optionalType.getSimpleName().substring(1);

						// (optionalx) -> optionalx.get();
						MethodHandle optionalToData = lookup.findVirtual(optionalClass, getAsMethod,
								MethodType.methodType(optionalType));

						// bridge.toData( targetClass.getOptional() ): optionalType
						MethodHandle fieldAccessor = MethodHandles.collectArguments(optionalToData, 0, accessor)
								.asType(MethodType.methodType(optionalType, targetClass));

						// Integer.valueOf( int );
						MethodHandle wrapInteger = lookup.findStatic(optionalNullableType, "valueOf",
								MethodType.methodType(optionalNullableType, optionalType));

						fieldAccessor = MethodHandles.filterArguments(wrapInteger, 0, fieldAccessor);

						if (setter != null) {

							// (value):boolean -> Objects.nonNull(value)
							MethodHandle checkNonNull = MethodHandles.publicLookup()
									.findStatic(Objects.class, "nonNull",
											MethodType.methodType(boolean.class, Object.class))
									.asType(MethodType.methodType(boolean.class, optionalNullableType));

							// (int):OptionalInt -> OptionalInt.of(int);
							MethodHandle optionalIntOf = MethodHandles.publicLookup().findStatic(optionalClass, "of",
									MethodType.methodType(optionalClass, optionalType));

							// (Integer):int -> Integer.intValue()
							MethodHandle integerIntValue = MethodHandles.publicLookup().findVirtual(
									optionalNullableType, optionalType.getSimpleName() + "Value",
									MethodType.methodType(optionalType));

							// (Integer):OptionalInt -> OptionalInt.of( Integer.intValue(i) )
							optionalIntOf = MethodHandles.collectArguments(optionalIntOf, 0, integerIntValue);

							// ():OptionalInt -> OptionalInt.empty()
							MethodHandle optionalIntEmpty = MethodHandles.publicLookup().findStatic(optionalClass,
									"empty", MethodType.methodType(optionalClass));

							// (Object[]):OptionalInt -> OptionalInt.empty()
							optionalIntEmpty = MethodHandles.dropArguments(optionalIntEmpty, 0, optionalNullableType);

							// (values[]):optionalType -> if (Object.nonNull(Integer)) { return OptionalInt.of(
							// Integer.intValue(i) ) } else { return OptionalInt.empty() }
							setter = MethodHandles.guardWithTest(checkNonNull, optionalIntOf, optionalIntEmpty);

						}

						// Optional values by definition are not required.
						isRequired = false;

						// (optional) -> optional.isPresent();
						MethodHandle isPresent = lookup.findVirtual(optionalClass, "isPresent",
								MethodType.methodType(boolean.class));

						isPresent = MethodHandles.collectArguments(isPresent, 0, accessor)
								.asType(MethodType.methodType(boolean.class, targetClass));

						component = new DataClassField(x, fieldName, optionalNullableType, dataClass, isRequired,
								isPresent, fieldAccessor, setter);

					} else {

						Union union = info.getUnion();
						if (union != null) {

							Class<?> unionClass = info.getType();

							if (union.value() == null || union.value().length == 0) {
								throw new DataBindException("Union annotation must have one or more classes");
							}

							DataClass[] unionTypes = new DataClass[union.value().length];
							for (int z = 0; z < union.value().length; z++) {
								Class<?> clss = union.value()[z];
								if (!unionClass.isAssignableFrom(clss)) {
									throw new DataBindException("Union types not assignable from class type");
								}

								unionTypes[z] = context.getDescriptor(clss);
								if (unionTypes[z] instanceof DataClassUnion
										|| unionTypes[z] instanceof DataClassArray) {
									// Embedded unions will require more work to decide on how valid other unions or
									// arrays would be. Unions with unknown type sets is problematic. If another union
									// member set is known it might be ok to add all children to the parent. Something
									// for future.
									throw new DataBindException(
											"Embedded union types can not include other unions or arrays");
								}
							}

							DataClassUnion dataUnion = new DataClassUnion(unionClass, unionTypes);

							MethodHandle isPresent = MethodHandles.publicLookup()
									.findStatic(Objects.class, "nonNull",
											MethodType.methodType(boolean.class, Object.class))
									.asType(MethodType.methodType(boolean.class, info.getType()));

							isPresent = MethodHandles.filterArguments(isPresent, 0, accessor);

							component = new DataClassField(x, fieldName, unionClass, dataUnion, isRequired, isPresent,
									accessor, setter);
						} else {

							DataClass dataClass = context.getDescriptor(info.getType(),
									(info.getParamType() != null ? info.getParamType() : info.getType()));

							if (dataClass.typeClass().isPrimitive()) {
								isRequired = true;
							}

							// Make isPresent return true for primitives/isRequired or call Objects.nonNull(value)
							MethodHandle isPresent = null;
							if (info.getType().isPrimitive()) {
								isPresent = MethodHandles.constant(boolean.class, true);

								isPresent = MethodHandles.dropArguments(isPresent, 0, targetClass);
							} else {
								isPresent = MethodHandles.publicLookup()
										.findStatic(Objects.class, "nonNull",
												MethodType.methodType(boolean.class, Object.class))
										.asType(MethodType.methodType(boolean.class, info.getType()));

								isPresent = MethodHandles.filterArguments(isPresent, 0, accessor);
							}

							component = new DataClassField(x, fieldName, info.getType(), dataClass, isRequired,
									isPresent, accessor, setter);
						}
					}
					dataComponents[x] = component;
				}

				// get the correct data constructor method handle. Either a constructor or a
				// static method tagged with @Data.
				MethodHandle dataConstructor = getDataConstructor(targetClass);

				// Build a MethodHandle that creates object and also calls setters with order of
				// fields as defined in components.
				MethodHandle constructor = createTupleConstructor(targetClass, components, dataConstructor);

				// See if there's an empty constructor available.
				MethodHandle creator = null;
				try {
					Constructor<?> creatorCtor = targetClass.getConstructor();
					creator = MethodHandles.publicLookup().unreflectConstructor(creatorCtor);
				} catch (NoSuchMethodException e) {
					// ignore.
				}

				descriptor = new DataClassRecord(targetClass, targetClass, creator, constructor, toData, toObject,
						dataComponents);
			}

			// At this point descriptor is not null but hasn't been added to context.
			// Loop through know interfaces and if we find a union type add this descriptor to it.
			Class<?>[] interfaces = targetClass.getInterfaces();
			for (Class<?> targetInterface : interfaces) {
				try {
					DataClassUnion targetUnion = (DataClassUnion) context.getDescriptor(targetInterface);

					targetUnion.addMemberType(descriptor);
				} catch (Throwable t) {
					// ignore.
				}
			}

			// Same applies to super classes that are abstract.
			Class<?> superClass = targetClass;
			while ((superClass = superClass.getSuperclass()) != null) {

				// An abstract type is a union type.
				if (Modifier.isAbstract(superClass.getModifiers())) {
					try {
						DataClassUnion targetUnion = (DataClassUnion) context.getDescriptor(superClass);

						targetUnion.addMemberType(descriptor);
					} catch (Throwable t) {
						// ignore.
					}
				}
			}

		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			throw new DataBindException(String.format("Failed to get data descriptor for %s", targetClass.getName()),
					e);
		}

		return descriptor;
	}

	private void reorderFields(DataOrder dataOrder, List<ComponentInfo> components) throws DataBindException {

		// Check lengths are ok.
		String[] fieldOrder = dataOrder.value();
		if (fieldOrder == null || fieldOrder.length != components.size()) {
			throw new DataBindException("DataOrder annotation has different field count to components found in data");
		}

		// build map of components to select from.
		Map<String, ComponentInfo> componentMap = components.stream()
				.collect(Collectors.toMap(e -> e.getName(), e -> e));

		// check all fields are present.
		List<String> fieldOrderList = List.of(dataOrder.value());
		for (String field : fieldOrderList) {
			if (!componentMap.containsKey(field)) {
				throw new DataBindException(
						String.format("DataOrder annotation had no corresponding field '%s'", field));
			}
		}

		// sort the list.
		components.sort(Comparator.comparing((ComponentInfo v) -> {
			return fieldOrderList.indexOf(v.getName());
		}));

	}

	/**
	 * Returns the Constructor for the dataClass. This may be different to what is used in creating the
	 * dataClass as a static constructor might be used instead. getDataConstructor is used to retrieve
	 * the MethodHandle for the correct data constructor.
	 *
	 * @param dataClass
	 * @return
	 * @throws DataBindException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Constructor<?> getConstructor(Class<?> dataClass)
			throws DataBindException, NoSuchMethodException, SecurityException {
		Constructor<?>[] constructors = dataClass.getConstructors();

		// only one custructor. this must be it.
		if (constructors.length == 1) {
			return constructors[0];
		}

		// Does it have an annotation?
		for (Constructor<?> constructor : constructors) {
			Data dataAnnotation = constructor.getAnnotation(Data.class);
			if (dataAnnotation != null) {
				return constructor;
			}
		}

		// look for a static constructor.
		Method[] methods = dataClass.getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				Data dataAnnotation = method.getAnnotation(Data.class);
				if (dataAnnotation != null) {
					// There must be a matching constructor that matches the parameters of the
					// static constructor.
					return dataClass.getConstructor(method.getParameterTypes());
				}
			}
		}

		throw new DataBindException("Could not find constructor: " + dataClass);

	}

	private MethodHandle getDataConstructor(Class<?> dataClass)
			throws DataBindException, NoSuchMethodException, SecurityException, IllegalAccessException {
		Constructor<?>[] constructors = dataClass.getConstructors();

		// only one custructor. this must be it.
		if (constructors.length == 1) {
			return MethodHandles.publicLookup().unreflectConstructor(constructors[0]);
		}

		// Does it have an annotation?
		for (Constructor<?> constructor : constructors) {
			Data dataAnnotation = constructor.getAnnotation(Data.class);
			if (dataAnnotation != null) {
				return MethodHandles.publicLookup().unreflectConstructor(constructor);
			}
		}

		// look for a static constructor.
		Method[] methods = dataClass.getMethods();
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers())) {
				Data dataAnnotation = method.getAnnotation(Data.class);
				if (dataAnnotation != null) {
					return MethodHandles.publicLookup().unreflect(method);
				}
			}
		}

		throw new DataBindException("Could not find constructor");

	}

	/**
	 * Creates a single MethodHandle that both constructs an object and sets any setters using a single
	 * Object[] as input. Fields are passed in through the real constructor or through the setters if
	 * present.
	 *
	 * @param fields
	 * @param dataConstructor
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws DataBindException
	 */
	private MethodHandle createTupleConstructor(Class<?> dataClass, List<ComponentInfo> fields,
			MethodHandle dataConstructor) throws IllegalAccessException, NoSuchMethodException, DataBindException {

		// Class<?>[] params = new Class[fields.size()];
		// for (int x = 0; x < fields.size(); x++) {
		// params[x] = fields.get(x).getType();
		// }
		//
		// MethodHandle signature = MethodHandles.empty(MethodType.methodType(dataClass,
		// params));

		// (Object[]):serialClass -> ctor(Object[])
		MethodHandle create = createEmbedConstructor(dataClass, dataConstructor, fields);

		// (serialClass, Object[]) -> serialClass.setValues(Object[x]);
		MethodHandle setters = createEmbedSetters(create, dataClass, fields);

		// (Object[], Object[]) -> ctor(Object[]).setvalues(Object[])
		MethodHandle createAndSet = MethodHandles.collectArguments(setters, 0, create);

		// (Object[]):dataObject
		int[] permuteInput = new int[2];
		MethodHandle result = MethodHandles.permuteArguments(createAndSet,
				MethodType.methodType(dataClass, Object[].class), permuteInput);

		return result;
	}

	/**
	 * Builds a constructor that takes Object[] as constructor arguments and return an object instance.
	 * Passes relevant fields into the data class constructor.
	 *
	 * @param objectConstructor
	 * @param fields
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws DataBindException
	 */
	private MethodHandle createEmbedConstructor(Class<?> dataClass, MethodHandle dataConstructor,
			List<ComponentInfo> fields) throws NoSuchMethodException, IllegalAccessException, DataBindException {
		MethodHandle result = dataConstructor;

		for (int x = 0; x < fields.size(); x++) {
			ComponentInfo field = fields.get(x);

			if (field.getConstructorArgument() >= 0) {

				int arg = field.getConstructorArgument();
				int inputIndex = x;

				// (values[],int) -> values[int]
				MethodHandle arrayGetter = MethodHandles.arrayElementGetter(Object[].class);

				// () -> inputIndex
				MethodHandle index = MethodHandles.constant(int.class, inputIndex);

				// (values[]) -> values[inputIndex] and maybe additional modifications to the value.
				MethodHandle arrayIndexGetter = dataArrayToObject(field, arrayGetter, index);

				// ()-> constructor( ..., values[inputIndex] , ... )
				result = MethodHandles.collectArguments(result, arg, arrayIndexGetter);
			}
		}

		// spread the arguments so ctor(Object[],Object[]...) becomes ctor(Object[])
		int paramCount = dataConstructor.type().parameterCount();
		if (paramCount > 0) {
			int[] permuteInput = new int[paramCount];
			result = MethodHandles.permuteArguments(result, MethodType.methodType(dataClass, Object[].class),
					permuteInput);
		} else {

			result = MethodHandles.dropArguments(result, 0, Object[].class);
		}
		return result;
	}

	/**
	 * Creates a MethodHandle that takes an Object[] as input and calls any field setters.
	 *
	 * @param fields
	 * @return
	 * @throws IllegalAccessException
	 * @throws DataBindException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private MethodHandle createEmbedSetters(MethodHandle ctorSignature, Class<?> dataClass, List<ComponentInfo> fields)
			throws IllegalAccessException, NoSuchMethodException, SecurityException, DataBindException {

		// (dataClass,Object[]):dataClass -> return object;
		MethodHandle identity = MethodHandles.identity(dataClass);

		// (dataClass, Object[]):dataClass -> return dataClass;
		MethodHandle result = MethodHandles.dropArguments(identity, 1, Object[].class);

		for (int x = 0; x < fields.size(); x++) {

			ComponentInfo field = fields.get(x);
			int inputIndex = x;

			if (field.getWriteMethod() != null && field.getConstructorArgument() < 0) {

				// (obj, value):void -> obj.setField( value );
				MethodHandle fieldSetter = field.getWriteMethod();

				// (value[],x):Object -> value[x]
				MethodHandle arrayGetter = MethodHandles.arrayElementGetter(Object[].class);

				// ():int -> inputIndex
				MethodHandle index = MethodHandles.constant(int.class, inputIndex);

				// (values[]) -> values[inputIndex] and maybe additional modifications to the value.
				MethodHandle arrayIndexGetter = dataArrayToObject(field, arrayGetter, index);

				// (obj, value[]):void -> obj.setField( value[inputIndex] );
				MethodHandle arrayFieldSetter = MethodHandles.collectArguments(fieldSetter, 1, arrayIndexGetter);

				// add to list of setters.
				result = MethodHandles.foldArguments(result, arrayFieldSetter);
			}
		}

		return result;
	}

	// The constructor uses an Object[] as the carrier to either call a constructor and/or call the
	// required setters of an object. Some field types such as bridges, Optional, OptionalInt requires
	// special handling to prepare the value prior to calling the constructor or setter. This makes the
	// required adaptations.
	private MethodHandle dataArrayToObject(ComponentInfo field, MethodHandle arrayGetter, MethodHandle index)
			throws DataBindException, IllegalAccessException, NoSuchMethodException, SecurityException {

		MethodHandle arrayIndexGetter;

		Field fieldAnnotation = field.getField();
		if (fieldAnnotation != null && fieldAnnotation.bridge() != null
				&& fieldAnnotation.bridge() != IdentityBridge.class) {

			@SuppressWarnings("rawtypes")
			Class<? extends DataBridge> bridgeClass = fieldAnnotation.bridge();

			ParameterizedType bridgeTypes = (ParameterizedType) bridgeClass.getGenericInterfaces()[0];

			// ToData has one parameter so this should be safe.
			Type bridgeDataType = bridgeTypes.getActualTypeArguments()[0];
			Type bridgeObjectType = bridgeTypes.getActualTypeArguments()[1];

			Class<?> bridgeDataClass;
			Class<?> bridgeObjectClass;

			if (bridgeDataType instanceof Class) {
				bridgeDataClass = (Class<?>) bridgeDataType;
			} else if (bridgeDataType instanceof ParameterizedType) {
				bridgeDataClass = (Class<?>) ((ParameterizedType) bridgeDataType).getRawType();
			} else {
				throw new DataBindException("Unrecognised Type");
			}

			if (bridgeObjectType instanceof Class) {
				bridgeObjectClass = (Class<?>) bridgeObjectType;
			} else if (bridgeObjectType instanceof ParameterizedType) {
				bridgeObjectClass = (Class<?>) ((ParameterizedType) bridgeObjectType).getRawType();
			} else {
				throw new DataBindException("Unrecognised Type");
			}

			@SuppressWarnings("rawtypes")
			DataBridge bridge;
			try {
				bridge = bridgeClass.getConstructor().newInstance();
			} catch (InstantiationException | IllegalArgumentException | InvocationTargetException e) {
				throw new DataBindException("Failed to instantiate bridge", e);
			}

			// (values[]) -> values[inputIndex]:optionalType
			arrayIndexGetter = MethodHandles.collectArguments(arrayGetter, 1, index)
					.asType(MethodType.methodType(bridgeDataClass, Object[].class));

			// bridge.toObject(type):dataType
			MethodHandle bridgeToObject = MethodHandles.publicLookup().findVirtual(bridgeClass, TOOBJECT_METHOD,
					MethodType.methodType(bridgeObjectClass, bridgeDataClass)).bindTo(bridge);

			bridgeToObject = bridgeToObject.asType(bridgeToObject.type().changeReturnType(field.getType()));

			// (values[]):bridgeType -> bridge.toObject( (bridgeData) values[inputIndex] ):bridgeObject
			arrayIndexGetter = MethodHandles.collectArguments(bridgeToObject, 0, arrayIndexGetter);

		} else if (field.getType() == Optional.class && field.getParamType() != null) {

			Class<?> optionalType = (Class<?>) field.getParamType().getActualTypeArguments()[0];

			// (values[]) -> values[inputIndex]:optionalType
			arrayIndexGetter = MethodHandles.collectArguments(arrayGetter, 1, index)
					.asType(MethodType.methodType(optionalType, Object[].class));

			// (value) -> Optional.ofNullable(value);
			MethodHandle optionalToObject = MethodHandles.publicLookup()
					.findStatic(Optional.class, "ofNullable", MethodType.methodType(Optional.class, Object.class))
					.asType(MethodType.methodType(Optional.class, optionalType));

			// (values[]):Optional<fieldType> -> bridge.toObject( values[inputIndex] ):Optional
			arrayIndexGetter = MethodHandles.collectArguments(optionalToObject, 0, arrayIndexGetter);
			// .asType(MethodType.methodType(field.getType(), Object[].class));
		} else if (field.getType() == OptionalInt.class
				|| field.getType() == OptionalLong.class | field.getType() == OptionalDouble.class) {

			// This is one of those ugly edge cases that is not supported by MethodHandles. OptionalInt type
			// optional primitives need to be unboxed from Integer for the constructor.
			// This requires a MethodHandle that looks as follows:
			// if (Object.nonNull(Integer)) { return OptionalInt.of( Integer.intValue(i) ) } else { return
			// OptionalInt.empty() }

			// This will be one of OptionalInt, OptionalLong or OptionalDouble.
			Class<?> optionalClass = field.getType();

			// work out which Optional class this is by getting return type of orElseThrow.
			Class<?> optionalType = optionalClass.getDeclaredMethod("orElseThrow").getReturnType();

			Class<?> optionalNullableType = null;
			if (optionalType == int.class) {
				optionalNullableType = Integer.class;
			} else if (optionalType == long.class) {
				optionalNullableType = Long.class;
			} else if (optionalType == double.class) {
				optionalNullableType = Double.class;
			}

			// Get data class for int, long or double.
			// DataClass optionalDataClass = context.getDescriptor(optionalType);

			// (values[]):optionalType -> values[inputIndex]:optionalType
			MethodHandle arrayGet = MethodHandles.collectArguments(arrayGetter, 1, index)
					.asType(MethodType.methodType(optionalNullableType, Object[].class));

			// (value):boolean -> Objects.nonNull(value)
			MethodHandle checkNonNull = MethodHandles.publicLookup()
					.findStatic(Objects.class, "nonNull", MethodType.methodType(boolean.class, Object.class))
					.asType(MethodType.methodType(boolean.class, optionalNullableType));

			// (values[]):boolean -> Objects.nonNull(values[index])
			checkNonNull = MethodHandles.collectArguments(checkNonNull, 0, arrayGet);

			// (int):OptionalInt -> OptionalInt.of(int);
			MethodHandle optionalIntOf = MethodHandles.publicLookup().findStatic(optionalClass, "of",
					MethodType.methodType(optionalClass, optionalType));

			// (Integer):int -> Integer.intValue()
			MethodHandle integerIntValue = MethodHandles.publicLookup().findVirtual(optionalNullableType,
					optionalType.getSimpleName() + "Value", MethodType.methodType(optionalType));

			// (Integer):OptionalInt -> OptionalInt.of( Integer.intValue(i) )
			optionalIntOf = MethodHandles.collectArguments(optionalIntOf, 0, integerIntValue);

			// (values[]):OptionalInt -> OptionalInt.of( Integer.intValue(values[inputIndex]) )
			optionalIntOf = MethodHandles.collectArguments(optionalIntOf, 0, arrayGet);

			// ():OptionalInt -> OptionalInt.empty()
			MethodHandle optionalIntEmpty = MethodHandles.publicLookup().findStatic(optionalClass, "empty",
					MethodType.methodType(optionalClass));

			// (Object[]):OptionalInt -> OptionalInt.empty()
			optionalIntEmpty = MethodHandles.dropArguments(optionalIntEmpty, 0, Object[].class);

			// (values[]):optionalType -> if (Object.nonNull(Integer)) { return OptionalInt.of(
			// Integer.intValue(i) ) } else { return OptionalInt.empty() }
			arrayIndexGetter = MethodHandles.guardWithTest(checkNonNull, optionalIntOf, optionalIntEmpty);

		} else {
			// (values[]):fieldType -> values[inputIndex]
			arrayIndexGetter = MethodHandles.collectArguments(arrayGetter, 1, index)
					.asType(MethodType.methodType(field.getType(), Object[].class));
		}

		return arrayIndexGetter;

	}
}