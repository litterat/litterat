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
package io.litterat.pep;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import io.litterat.pep.describe.DefaultResolver;

public class PepContext {

	// Resolved class information
	private final ConcurrentHashMap<Class<?>, PepDataClass> descriptors = new ConcurrentHashMap<>();

	// Resolver
	private final PepContextResolver resolver;

	// default resolver
	private final PepContextResolver defaultResolver;

	public static class Builder {

		PepContextResolver resolver;

		boolean allowAny = false;

		boolean allowSerializable = false;

		public Builder() {
			this.resolver = null;
		}

		public Builder resolver(PepContextResolver resolver) {
			this.resolver = resolver;
			return this;
		}

		public Builder allowAny() {
			allowAny = true;
			return this;
		}

		public Builder allowSerializable() {
			allowSerializable = true;
			return this;
		}

		public PepContext build() {
			return new PepContext(this);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private PepContext(Builder builder) {

		this.defaultResolver = new DefaultResolver(builder.allowSerializable, builder.allowAny);

		if (builder.resolver == null) {
			this.resolver = defaultResolver;
		} else {
			this.resolver = builder.resolver;
		}

		try {
			registerAtom(Boolean.class);
			registerAtom(boolean.class);
			registerAtom(Character.class);
			registerAtom(char.class);
			registerAtom(Byte.class);
			registerAtom(byte.class);
			registerAtom(Short.class);
			registerAtom(short.class);
			registerAtom(Integer.class);
			registerAtom(int.class);
			registerAtom(Long.class);
			registerAtom(long.class);
			registerAtom(Float.class);
			registerAtom(float.class);
			registerAtom(Double.class);
			registerAtom(double.class);
			registerAtom(Void.class);
			registerAtom(String.class);
		} catch (PepException e) {
			throw new IllegalArgumentException();
		}

	}

	public PepContextResolver defaultResolver() {
		return defaultResolver;
	}

	public void registerAtom(Class<?> targetClass) throws PepException {
		register(targetClass, new PepDataClass(targetClass));
	}

	public PepDataClass getDescriptor(Class<?> targetClass) throws PepException {

		PepDataClass descriptor = descriptors.get(targetClass);
		if (descriptor == null) {
			descriptor = resolver.resolve(this, targetClass);
			if (descriptor == null) {
				throw new PepException(String.format("Unable to find suitable data descriptor for class: %s", targetClass.getName()));
			}
			register(targetClass, descriptor);
		}

		return descriptor;
	}

	private <T> void checkExists(Class<T> targetClass) throws PepException {
		if (descriptors.containsKey(targetClass)) {
			throw new PepException(String.format("Class already registered: %s", targetClass.getName()));
		}
	}

	public <T> void register(Class<T> targetClass, PepDataClass descriptor) throws PepException {
		checkExists(targetClass);

		descriptors.put(targetClass, descriptor);
	}

	public void registerAtom(Class<?> targetClass, ObjectDataBridge<?, ?> bridge) throws PepException {
		checkExists(targetClass);

		Class<?> bridgeClass = bridge.getClass();

		try {
			Method toDataMethod = bridgeClass.getMethod("toData", targetClass);
			Method toObjectMethod = bridgeClass.getMethod("toObject", toDataMethod.getReturnType());

			MethodHandle toData = MethodHandles.publicLookup().unreflect(toDataMethod).bindTo(bridge);
			MethodHandle toObject = MethodHandles.publicLookup().unreflect(toObjectMethod).bindTo(bridge);

			register(targetClass, new PepDataClass(targetClass, toDataMethod.getReturnType(), toData, toObject));
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | PepException e) {
			throw new PepException("Failed to register atom bridge", e);
		}

	}

}
