package io.litterat.core;

import java.lang.reflect.Type;

import io.litterat.bind.DataClass;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Typename;

/**
 * 
 * The class binder should return the correct DataClass for the targetClass/type. To do this, the
 * corresponding typename should be returned from the name binder and the data definition returned
 * from the type library. In a code first situation the definition should be registered using the
 * definition binder. In a schema first situation, the definition should already be present and the
 * class should match the definition provided.
 *
 */
public interface TypeContextClassBinder {

	DataClass resolve(TypeContext context, Typename typename, Class<?> targetClass, Type parameterizedType)
			throws TypeException;

}
