package io.litterat.xpl.resolve;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import io.litterat.schema.TypeException;
import io.litterat.bind.DataClass;
import io.litterat.bind.DataClassAtom;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;

public final class ModelHelper {

	public static MethodHandle resolveFieldGetter(DataClassRecord dataClass, String fieldName) throws TypeException {

		for (DataClassField dataField : dataClass.fields()) {
			if (dataField.name().equalsIgnoreCase(fieldName)) {

				MethodHandle toData = null;
				// Pass the accessor through the toData handle to get the correct data type.
				// toData will be identity function if no change required.
				DataClass componentClass = dataField.dataClass();
				if (componentClass instanceof DataClassAtom) {
					DataClassAtom dataClassAtom = (DataClassAtom) componentClass;

					toData = MethodHandles.filterArguments(dataClassAtom.toData(), 0, dataField.accessor());

					if (!dataClassAtom.dataClass().isPrimitive()) {
						toData = checkIsPresent(dataClass, dataField, toData, dataClassAtom.dataClass());
					}

				} else if (componentClass instanceof DataClassRecord) {

					DataClassRecord dataClassRecord = (DataClassRecord) componentClass;

					// MethodHandle fieldToData = dataClassRecord.toData();

					// toData = MethodHandles.filterArguments(fieldToData, 0, dataField.accessor());

					toData = dataField.accessor();

					if (!dataClassRecord.typeClass().isPrimitive()) {
						toData = checkIsPresent(dataClass, dataField, toData, dataClassRecord.typeClass());
					}

				} else {
					toData = dataField.accessor();
				}

				return toData;

			}
		}

		throw new TypeException(
				String.format("Field '%s' not found in dataClass '%s'", fieldName, dataClass.typeClass().getName()));
	}

	public static MethodHandle resolveFieldSetter(DataClassRecord dataClass, String field) throws TypeException {
		for (DataClassField component : dataClass.fields()) {
			if (component.name().equalsIgnoreCase(field)) {

				MethodHandle setter = component.setter().orElseThrow(() -> new TypeException("No setter available"));

				// Pass the object through the toObject method handle to get the correct type
				// for the setter. toObject will be identity function if no change required.
				DataClass componentClass = component.dataClass();
				if (componentClass instanceof DataClassAtom) {
					MethodHandle toObject = ((DataClassAtom) componentClass).toObject();

					return MethodHandles.filterArguments(setter, 0, toObject);
				} else if (componentClass instanceof DataClassRecord) {
					// MethodHandle toObject = ((DataClassRecord) componentClass).toObject();

					// return MethodHandles.filterArguments(setter, 0, toObject);
					return setter;
				} else {
					return setter;
				}
			}
		}

		throw new TypeException("Field not found");
	}

	// If the value is primitive don't wrap it in guard with test. This is mainly because
	// MethodHandles.constant will throw a NPE for primitives. Also relates to a bigger issue
	// of design of XPL passing nulls around which will need to be refactored to remove.
	// TODO refactor so that design of interpreter doesn't need to pass around nulls.
	private static MethodHandle checkIsPresent(DataClassRecord dataClass, DataClassField dataField, MethodHandle toData,
			Class<?> fieldDataClass) {
		// Check for is a value is present prior to reading.
		MethodHandle isPresent = dataField.isPresent();

		MethodHandle returnNull = MethodHandles.constant(fieldDataClass, null);
		returnNull = MethodHandles.dropArguments(returnNull, 0, dataClass.typeClass());

		return MethodHandles.guardWithTest(isPresent, toData, returnNull);
	}

}
