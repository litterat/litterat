package io.litterat.schema;

import io.litterat.schema.meta.Typename;

public class TypeNotDefinedException extends TypeException {

	public TypeNotDefinedException(Typename typename) {
		super(String.format("Typename '%s' not defined", typename));
	}
}
