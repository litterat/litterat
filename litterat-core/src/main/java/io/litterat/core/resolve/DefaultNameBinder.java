package io.litterat.core.resolve;

import io.litterat.annotation.Namespace;
import io.litterat.bind.DataClass;
import io.litterat.core.TypeContext;
import io.litterat.core.TypeContextNameBinder;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Typename;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the default method of converting from a code first Class to a schema typename.
 * It uses the Class package and name by default, but can be overridden by using the Typename
 * or Namespace annotations.
 */
public class DefaultNameBinder implements TypeContextNameBinder {


	private final Map<String,Package> namespaceMap = new HashMap<>();

	@Override
	public Typename resolve(TypeContext context, DataClass dataClass) throws TypeException {
		Class<?> clss = dataClass.typeClass();

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

	@Override
	public Class<?> resolve(TypeContext context, Typename typename) throws TypeException {

		String namespace = typename.namespace();
		String name = typename.name();
		Class<?> result = null;

        try {
            result = Class.forName(namespace+"." + name);
        } catch (ClassNotFoundException e) {
            // ignore
        }

		Package pkg = namespaceMap.get(namespace);
		if (pkg != null) {
			namespace = pkg.getName();
			try {
				result = Class.forName(namespace+"." + name);
			} catch (ClassNotFoundException e) {
				// ignore
			}
		}

        return result;
	}

	@Override
	public void registerPackage(String namespace, Package pkg) {
		namespaceMap.put(namespace,pkg);
	}

}
