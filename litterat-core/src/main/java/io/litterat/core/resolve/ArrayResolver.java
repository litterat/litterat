package io.litterat.core.resolve;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClassArray;
import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Array;
import io.litterat.schema.meta.Typename;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ArrayResolver implements TypeResolver<Array, DataClassArray> {

    public Array resolveDefinition(TypeContext context, DataClassArray dataClass, Type parameterizedType)
            throws TypeException, DataBindException {

        try {

            Typename arrayDataTypename;

            Class<?> targetClass = dataClass.typeClass();

            // Find the type of the Array collection.
            if (targetClass.isArray()) {

                // Java arrays type is easily available via reflection.
                Class<?> arrayClass = targetClass.getComponentType();
                arrayDataTypename = context.getTypename(arrayClass, arrayClass);

            } else if (Collection.class.isAssignableFrom(targetClass)) {

                // We need the parameterizedType as type erasure means we can only get Collection type
                // from certain places.
                if (!(parameterizedType instanceof ParameterizedType)) {
                    throw new CodeAnalysisException("Collection must provide parameterized type information");
                }

                Type paramType = ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
                if (paramType instanceof Class) {
                    Class<?> arrayClass = (Class<?>) paramType;
                    arrayDataTypename = context.getTypename(arrayClass, arrayClass);
                } else if (paramType instanceof ParameterizedType) {
                    ParameterizedType arrayParamType = (ParameterizedType) paramType;
                    arrayDataTypename = context.getTypename((Class<?>) arrayParamType.getRawType(), arrayParamType);
                } else {
                    throw new CodeAnalysisException("Unrecognized parameterized type");
                }

            } else {
                throw new CodeAnalysisException("Not recognised array class");
            }

            return new Array(arrayDataTypename);

        } catch (SecurityException e) {
            throw new CodeAnalysisException("Failed to get array descriptor", e);
        }

    }
}
