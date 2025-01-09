package io.litterat.core;

public class TypeException extends Exception {

	public TypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TypeException(String message) {
		super(message);
	}

	public TypeException(Throwable cause) {
		super(cause);
	}

}
