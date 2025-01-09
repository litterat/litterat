package io.litterat.core;

import java.lang.reflect.Type;

import io.litterat.core.meta.Typename;

/**
 * 
 * The name binder converts the target class/type to the type library typename.
 *
 */
public interface TypeContextNameBinder {

	Typename resolve(TypeContext context, Class<?> targetClass, Type parameterizedType) throws TypeException;
}
