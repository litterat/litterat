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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.Field;
import io.litterat.bind.Union;

public class ImmutableFinder implements ComponentFinder {

	@SuppressWarnings("unused")
	private final DataBindContext context;

	public ImmutableFinder(DataBindContext context) {
		this.context = context;
	}

	/**
	 * This attempts to match up the arguments of the given constructor with field accessors. It relies
	 * on both the accessors and constructor to be using simple set and get field with no changes to
	 * values.
	 */
	@Override
	public void findComponents(Class<?> clss, Constructor<?> constructor, List<ComponentInfo> fields)
			throws DataBindException {

		try {
			Lookup lookup = MethodHandles.publicLookup();

			ClassReader cr = new ClassReader(clss.getName());
			ClassNode classNode = new ClassNode();
			cr.accept(classNode, 0);

			List<ComponentInfo> immutableFields = new ArrayList<>(constructor.getParameterCount());

			// First try and identify the constructor arguments.
			// Perform node instruction inspection to match constructor arguments with
			// accessors.
			for (MethodNode method : classNode.methods) {

				Type methodType = Type.getType(method.desc);
				String constructorDescriptor = Type.getConstructorDescriptor(constructor);

				// Find the MethodNode that matches the passed in constructor.
				if (method.name.equals("<init>") && method.desc.equals(constructorDescriptor)) {
					identifyArguments(constructor, immutableFields, method, methodType);
					break;
				}
			}

			// if byte code analysis failed or if user supplies @Field annotations.
			Parameter[] params = constructor.getParameters();
			for (int x = 0; x < params.length; x++) {
				Field field = params[x].getAnnotation(Field.class);
				if (field != null) {
					// field has been annotated so check for matching field.
					final int paramIndex = x;
					ComponentInfo component = immutableFields.stream()
							.filter(e -> e.getConstructorArgument() == paramIndex).findFirst().orElse(null);
					if (component != null) {
						component.setField(field);
					} else {
						// Add the parameter.
						component = new ComponentInfo(field.name(), params[x].getType());
						component.setConstructorArgument(x);
						component.setField(field);

						immutableFields.add(component);
					}
				}

				// Look to see if this is a union field.
				Union union = params[x].getAnnotation(Union.class);
				if (union != null) {
					// union has been annotated so check for matching field.
					final int paramIndex = x;
					ComponentInfo component = immutableFields.stream()
							.filter(e -> e.getConstructorArgument() == paramIndex).findFirst().orElse(null);
					if (component != null) {
						component.setUnion(union);
					} else {
						// Add the parameter.
						component = new ComponentInfo(field.name(), params[x].getType());
						component.setConstructorArgument(x);
						component.setUnion(union);

						immutableFields.add(component);
					}
				}
			}

			// Find the matching accessor methods. Must have already found fields in constructor.
			examineAccessorMethods(clss, immutableFields, classNode);
			examineAccessorAnnotations(clss, immutableFields, lookup);

			Class<?> superClass = clss;
			while ((superClass = superClass.getSuperclass()) != null) {
				if (superClass == Object.class) {
					continue;
				}
				ClassReader superClassReader = new ClassReader(superClass.getName());
				ClassNode superClassNode = new ClassNode();
				superClassReader.accept(superClassNode, 0);

				examineAccessorMethods(superClass, immutableFields, superClassNode);
				examineAccessorAnnotations(superClass, immutableFields, lookup);
			}

			// Fail if we didn't find the right number of parameters.
			if (immutableFields.size() != constructor.getParameterCount()) {
				throw new DataBindException(String.format(
						"Failed to match immutable fields for class: %s. Add @Field annotations to assist.", clss));
			}

			// Check all params have valid information.
			for (ComponentInfo component : immutableFields) {
				if (component.getReadMethod() == null) {
					throw new DataBindException(String.format(
							"Failed to match immutable field accessor for class: %s. Add @Field annotations to assist field '%s'",
							clss, component.getName()));
				}
			}

			// Add the fields to the list for use.
			fields.addAll(immutableFields);

		} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException e) {
			throw new DataBindException("Failed to access class", e);
		}
	}

	public static Class<?>[] classesFromTypes(Type[] types) throws ClassNotFoundException {
		int len = types.length;
		Class<?>[] argumentTypes = new Class[len];
		for (int i = 0; i < types.length; ++i) {
			argumentTypes[i] = Class.forName(types[i].getClassName());
		}
		return argumentTypes;
	}

	private void examineAccessorMethods(Class<?> clss, List<ComponentInfo> immutableFields, ClassNode classNode)
			throws NoSuchMethodException, IllegalAccessException, SecurityException, DataBindException {
		// Perform node instruction inspection to match constructor arguments with
		// accessors.
		for (MethodNode methodNode : classNode.methods) {

			Type methodType = Type.getType(methodNode.desc);

			// Only interested in accessors.
			if (methodType.getArgumentTypes().length != 0) {
				continue;
			}

			// Returns the field name that this accessor is using if it is a simple accessor
			// type.
			examineAccessor(clss, immutableFields, methodNode);
		}
	}

	private void examineAccessorAnnotations(Class<?> clss, List<ComponentInfo> immutableFields, Lookup lookup)
			throws IllegalAccessException, DataBindException {
		// Possibly failed to find accessor through invariant byte code analysis.
		// Fallback on @Field annotation or method name.
		for (Method method : clss.getDeclaredMethods()) {

			// Only interested in accessors.
			if (method.getParameterCount() > 0) {
				continue;
			}

			// field has been annotated so check for matching field.
			final String name = method.getName();
			ComponentInfo component = immutableFields.stream().filter(e -> e.getName().equals(name)).findFirst()
					.orElse(null);
			if (component != null && component.getReadMethod() == null) {
				Field field = method.getAnnotation(Field.class);
				if (field != null) {
					component.setReadMethod(lookup.unreflect(method));
					component.setField(field);
				}

				Union union = method.getAnnotation(Union.class);
				if (union != null) {
					component.setReadMethod(lookup.unreflect(method));
					component.setField(field);
				}

			}

		}
	}

	private void checkFieldAnnotation(ComponentInfo info, Class<?> clss, String fieldName) throws DataBindException {
		// Capture field annotation from field if present.
		try {
			java.lang.reflect.Field clssField = clss.getDeclaredField(fieldName);
			Field field = clssField.getAnnotation(Field.class);
			if (field != null) {
				info.setField(field);
			}

			Union union = clssField.getAnnotation(Union.class);
			if (union != null) {
				info.setUnion(union);
			}

		} catch (NoSuchFieldException | SecurityException e1) {
			// don't expect an exception here.
			throw new RuntimeException("unexepected exception", e1);
		}

	}

	/**
	 * This matches up a constructor arguments with the relevant fields for a class. It requires that
	 * each argument is not mutated before being assigned to the field. It relies on the fact that the
	 * instructions required to assign a value to a field uses the following operations:
	 * 
	 * <pre>
	 * ALOAD 0 // Load this object reference. 
	 * ILOAD x // Load the value for the parameter. 
	 * PUTFIELD y // Assign the value x to the field y on this object.
	 * </pre>
	 * 
	 * In the case of a class that extends another class it will call the super class. This is a little
	 * more complex to work out. The constructor of the super class needs to be analysed and matched
	 * with the parameters it is called with.
	 *
	 * @param fieldMap
	 * @param method
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws DataBindException
	 */
	private int identifyArguments(Constructor<?> constructor, List<ComponentInfo> fields, MethodNode method,
			Type methodType) throws IOException, NoSuchMethodException, SecurityException, DataBindException {
		boolean foundLoadThis = false;
		boolean foundLoadArg = false;

		int argsFound = 0;

		// Find param load instruction indexes.
		Map<Integer, Integer> loadIndexToParamMap = new HashMap<>();
		Class<?>[] paramClasses = constructor.getParameterTypes();
		int paramCounter = 0;
		for (int x = 0; x < paramClasses.length; x++) {
			// index is 1 based. increment first.
			paramCounter++;

			loadIndexToParamMap.put(paramCounter, x);

			// primitive longs and doubles take up two slots and move all subsequent params out by one.
			if (paramClasses[x] == long.class || paramClasses[x] == double.class) {
				paramCounter++;
			}

		}

		int maxVarLoadInsn = paramCounter;

		List<Integer> args = new ArrayList<>();

		ListIterator<AbstractInsnNode> it = method.instructions.iterator();
		while (it.hasNext()) {
			AbstractInsnNode insn = it.next();

			switch (insn.getOpcode()) {
			case Opcodes.ALOAD:
				VarInsnNode varInsn = (VarInsnNode) insn;
				if (!foundLoadThis) {
					if (varInsn.var == 0) {
						foundLoadThis = true;
					}
					break;
				}
			case Opcodes.ILOAD:
			case Opcodes.LLOAD:
			case Opcodes.DLOAD:
			case Opcodes.FLOAD:
				VarInsnNode varLoadInsn = (VarInsnNode) insn;
				if (insn.getOpcode() == Opcodes.DLOAD || insn.getOpcode() == Opcodes.LLOAD) {
					// doubles and longs take up two slots so need to increment max.
					maxVarLoadInsn++;
				}

				// Check if this is being loaded from a parameter variable.
				if (foundLoadThis & varLoadInsn.var > 0 && varLoadInsn.var <= maxVarLoadInsn) {
					foundLoadArg = true;
					// map back to the correct argument index.
					args.add(loadIndexToParamMap.get(varLoadInsn.var));
				}
				break;
			case Opcodes.PUTFIELD:
				FieldInsnNode putFieldInsn = (FieldInsnNode) insn;
				if (foundLoadThis && foundLoadArg && args.size() == 1) {

					// Invariance identified, so capture the argument number.
					int arg = args.get(0);
					Parameter param = constructor.getParameters()[arg];
					Class<?> fieldClass = param.getType();
					ComponentInfo component = new ComponentInfo(putFieldInsn.name, fieldClass);
					component.setConstructorArgument(arg);

					java.lang.reflect.Type paramType = param.getParameterizedType();
					if (paramType instanceof ParameterizedType) {
						component.setParamType((ParameterizedType) paramType);
					}

					// Check if either the parameter or field has the @Field parameter.
					checkFieldAnnotation(component, constructor.getDeclaringClass(), putFieldInsn.name);

					fields.add(component);
					argsFound++;

				}
				foundLoadThis = false;
				foundLoadArg = false;
				args.clear();
				break;

			case Opcodes.INVOKESPECIAL:
				MethodInsnNode superInvoke = (MethodInsnNode) insn;
				if (foundLoadThis && foundLoadArg && args.size() > 0 && superInvoke.name.equals("<init>")) {
					List<ComponentInfo> superFields = new ArrayList<>();

					// Get the super class and read the byte code.
					Class<?> superClass = constructor.getDeclaringClass().getSuperclass();
					ClassReader cr = new ClassReader(superClass.getName());
					ClassNode classNode = new ClassNode();
					cr.accept(classNode, 0);

					// Find the right constructor that matches the calling parameters.
					Class<?>[] superInitArgs = new Class<?>[args.size()];
					for (int x = 0; x < args.size(); x++) {
						int arg = args.get(x);
						superInitArgs[x] = constructor.getParameters()[arg].getType();
					}

					Constructor<?> superConstructor = superClass.getConstructor(superInitArgs);

					// Find the byte code for the corresponding initialiser method in the super class.
					int superArgsFound = 0;
					for (MethodNode superMethod : classNode.methods) {

						Type superMethodType = Type.getType(superMethod.desc);
						String constructorDescriptor = Type.getConstructorDescriptor(superConstructor);

						// Find the MethodNode that matches the passed in constructor.
						if (superMethod.name.equals("<init>") && superMethod.desc.equals(constructorDescriptor)) {
							superArgsFound = identifyArguments(superConstructor, superFields, superMethod,
									superMethodType);
							break;

						}
					}

					if (superArgsFound != args.size()) {
						throw new DataBindException(String.format(
								"Failed to match super fields for class: %s. Add @Field annotations to assist. %s",
								superClass.getName()));
					}

					// add found fields to the list.
					fields.addAll(superFields);

				}
				// fall through to reset.
			default:
				foundLoadThis = false;
				foundLoadArg = false;
				args.clear();
			}

		}

		return argsFound;

	}

	/**
	 * Look through the instructions looking for ALOAD, GETFIELD, RETURN combination. The fieldName used
	 * in the GETFIELD instruction must have previously been used and found in the constructor.
	 *
	 * @param clss
	 * @param fieldMap
	 * @param method
	 * @return field name
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws DataBindException
	 */
	private String examineAccessor(Class<?> clss, List<ComponentInfo> fields, MethodNode method)
			throws NoSuchMethodException, IllegalAccessException, SecurityException, DataBindException {
		boolean foundLoadThis = false;
		String lastField = null;

		ListIterator<AbstractInsnNode> it = method.instructions.iterator();
		while (it.hasNext()) {

			AbstractInsnNode insn = it.next();

			switch (insn.getOpcode()) {
			case Opcodes.ALOAD:
				VarInsnNode varInsn = (VarInsnNode) insn;
				if (varInsn.var == 0) {
					foundLoadThis = true;
				}
				break;
			case Opcodes.GETFIELD:
				FieldInsnNode fieldInsn = (FieldInsnNode) insn;
				if (foundLoadThis) {
					lastField = fieldInsn.name;
				}
				break;
			case Opcodes.IRETURN:
			case Opcodes.ARETURN:
			case Opcodes.LRETURN:
			case Opcodes.DRETURN:
			case Opcodes.RETURN:
			case Opcodes.FRETURN:
				if (foundLoadThis && lastField != null) {

					final String fieldName = lastField;
					ComponentInfo info = fields.stream().filter(e -> e.getName().equals(fieldName)).findFirst()
							.orElse(null);
					if (info != null) {
						Method accessorMethod = clss.getDeclaredMethod(method.name);

						Field field = accessorMethod.getAnnotation(Field.class);
						if (field != null) {
							info.setField(field);
						}

						Union union = accessorMethod.getAnnotation(Union.class);
						if (union != null) {
							info.setUnion(union);
						}

						info.setReadMethod(MethodHandles.publicLookup().unreflect(accessorMethod));
					}

					// accessor matches, break;
					break;
				}
			case -1:
				// ignore these opcodes.
				break;
			default:
				foundLoadThis = false;
				lastField = null;
			}
		}

		return lastField;
	}

}
