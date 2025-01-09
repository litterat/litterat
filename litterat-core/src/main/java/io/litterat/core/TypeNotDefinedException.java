package io.litterat.core;

import io.litterat.core.meta.Typename;

public class TypeNotDefinedException extends TypeException {

	public TypeNotDefinedException(Typename typename) {
		super(String.format("Typename '%s' not defined", typename));
	}
}
