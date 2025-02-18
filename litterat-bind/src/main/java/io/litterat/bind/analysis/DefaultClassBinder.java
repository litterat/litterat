package io.litterat.bind.analysis;


import io.litterat.annotation.Atom;
import io.litterat.annotation.Record;
import io.litterat.annotation.ToData;
import io.litterat.annotation.Union;
import io.litterat.bind.DataBindContext;
import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;

public class DefaultClassBinder {


    private final NewFeatures newFeatures;

	// private final TypeContextNameBinder nameBinder;

	private final DefaultRecordBinder recordBinder;
	private final DefaultAtomBinder atomBinder;
	private final DefaultUnionBinder unionBinder;
	private final DefaultArrayBinder arrayBinder;


	public DefaultClassBinder() {

        newFeatures = new NewFeatures();
		recordBinder = new DefaultRecordBinder();
		atomBinder = new DefaultAtomBinder();
		unionBinder = new DefaultUnionBinder();
		arrayBinder = new DefaultArrayBinder();
	}

	public DataClass resolve(DataBindContext context,  Class<?> targetClass, Type parameterizedType)
			throws DataBindException {

		DataClass result = null;

		// If this is a schema first situation then this will return a definition and the job is to bind to
		// it. If it is a code first situation then we expect this to throw an exception, and we need to
		// first create the definition and then bind the class to that definition.

		if (isRecord(targetClass)) {
			result = recordBinder.resolveRecord(context, targetClass);
		} else if (isUnion(targetClass)) {
			result = unionBinder.resolveUnion(context, targetClass, parameterizedType);
		} else if (isAtom(targetClass)) {
			result = atomBinder.resolveAtom(context, targetClass);
		} else if (isArray(targetClass)) {
			result = arrayBinder.resolveArray(context, targetClass, parameterizedType);
		} else {
			throw new DataBindException(
					String.format("Unable to find a valid data conversion for class: %s", targetClass));
		}

		return result;
	}


	private boolean isUnion(Class<?> targetClass) {
		if (Collection.class.isAssignableFrom(targetClass)) {
			return false;
		}

		// If the targetClass is using Java 16 sealed interfaces then it provides
		// the union type.
		if (newFeatures.isSealed(targetClass)) {
			return true;
		}

		if (targetClass.isInterface()) {

			// Interface needs to be marked with @Data or Serializable.
			Union unionAnnotation = targetClass
					.getAnnotation(Union.class);
			if (unionAnnotation != null) {
				return true;
			}

		} else

		if (Modifier.isAbstract(targetClass.getModifiers())) {
			// Array classes are abstract and we don't want them.
			if (targetClass.isArray()) {
				// this is classed as an array, not a union.
				return false;
			}

			// Interface needs to be marked with @Data or Serializable.
			Union unionAnnotation = targetClass
					.getAnnotation(Union.class);
			if (unionAnnotation != null) {
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean isRecord(Class<?> targetClass) {

		// If it a Java record then no annotations are required.
		if (newFeatures.isRecord(targetClass)) {
			return true;
		}

		// if class has annotation this is a tuple.
		Record recordAnnotation = targetClass.getAnnotation(Record.class);
		if (recordAnnotation != null) {
			return true;
		}

		// Check for annotation on constructor.
		Constructor<?>[] constructors = targetClass.getConstructors();
		for (Constructor<?> constructor : constructors) {
			recordAnnotation = constructor.getAnnotation(Record.class);
			if (recordAnnotation != null) {
				return true;
			}
		}

		// This is to look at static methods
		Method[] methods = targetClass.getDeclaredMethods();
		for (Method method : methods) {

			recordAnnotation = method.getAnnotation(Record.class);
			if (Modifier.isStatic(method.getModifiers()) && recordAnnotation != null) {
				return true;
			}
		}

		// Class has implemented ToData so exports/imports a data class.
		return ToData.class.isAssignableFrom(targetClass);
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
		Atom atomAnnotation = targetClass
				.getAnnotation(Atom.class);
		if (atomAnnotation != null) {
			return true;
		}

		// Check for annotation on constructor.
		Constructor<?>[] constructors = targetClass.getConstructors();
		for (Constructor<?> constructor : constructors) {
			atomAnnotation = constructor.getAnnotation(Atom.class);
			if (atomAnnotation != null) {
				return true;
			}
		}

		// This is to look at static methods
		Method[] methods = targetClass.getDeclaredMethods();
		for (Method method : methods) {

			atomAnnotation = method.getAnnotation(Atom.class);
			if (Modifier.isStatic(method.getModifiers()) && atomAnnotation != null) {
				return true;
			}
		}

		return false;
	}

}
