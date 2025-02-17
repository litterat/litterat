package io.litterat.core;

import io.litterat.bind.DataClass;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Typename;

/**
 * 
 * The name binder converts the target class/type to the type library typename.
 *
 */
public interface TypeContextNameBinder {

	/**
	 * Derive the schema Typename from the Java targetClass.
	 */
	Typename resolve(TypeContext context, DataClass dataClass) throws TypeException;

	/**
	 * Find a Java class given
	 * @param context
	 * @param typename
	 * @return
	 */
	Class<?> resolve(TypeContext context, Typename typename) throws TypeException;

	void registerPackage(String namespace, Package pkg);

}
