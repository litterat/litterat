package io.litterat.core.resolve;

import java.lang.reflect.Type;

import io.litterat.annotation.Namespace;
import io.litterat.core.TypeContext;
import io.litterat.core.TypeContextNameBinder;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Typename;

/**
 * This is the default method of converting from a code first Class to a schema typename.
 * It uses the Class package and name by default, but can be overridden by using the Typename
 * or Namespace annotations.
 */
public class DefaultNameBinder implements TypeContextNameBinder {

	@Override
	public Typename resolve(TypeContext context, Class<?> clss, Type type) throws TypeException {
		String namespace = clss.getPackageName();
		String name = clss.getSimpleName();

		Package pckage = clss.getPackage();
		if (pckage != null) {
			Namespace namespaceAnnotation = pckage.getAnnotation(Namespace.class);
			if (namespaceAnnotation != null) {
				namespace = namespaceAnnotation.value();
			}
		}

		io.litterat.annotation.Typename nameAnnotation = clss
				.getAnnotation(io.litterat.annotation.Typename.class);
		if (nameAnnotation != null) {
			if (!nameAnnotation.namespace().isEmpty()) {
				namespace = nameAnnotation.namespace();
			}

			if (!nameAnnotation.name().isEmpty()) {
				name = nameAnnotation.name();
			}
		}

		if (clss.isArray()) {
			Typename arrayTypename = context.getTypename(clss.getComponentType());
			namespace = arrayTypename.namespace();
			name = arrayTypename.name() + "_array";
		}

		return new Typename(namespace, name);
	}

}
