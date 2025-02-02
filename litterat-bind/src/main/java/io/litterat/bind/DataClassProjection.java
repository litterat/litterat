package io.litterat.bind;

import java.lang.invoke.MethodHandle;

/**
 * In situations where a Record class has a projected class, it is useful to have a way of looking up
 * the dataClass based on the systemClass. This entry is added when a DataClassRecord's dataClass differs
 * from the targetClass. The targetType is the projected data class, and the systemClass is the Java
 * instance class.
 */
public class DataClassProjection extends DataClass {

    private final Class<?> dataClass;

    // Method handle to convert object to data object.
    // Converts from typeClass -> dataClasss.
    private final MethodHandle toData;

    // Method handle to convert data object to target object.
    // Converts from dataClass -> typeClass.
    private final MethodHandle toObject;

    public DataClassProjection(Class<?> targetType, Class<?> dataClass, MethodHandle toData, MethodHandle toObject) {
        super(targetType);

        this.dataClass = dataClass;
        this.toData = toData;
        this.toObject = toObject;
    }

    public Class<?> dataClass() {
        return dataClass;
    }

    public MethodHandle toData() {
        return toData;
    }

    public MethodHandle toObject() {
        return toObject;
    }
}
