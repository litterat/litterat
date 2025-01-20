package io.litterat.schema.meta;

public final class Meta {

	// Core meta elements.
	public static final Typename DEFINITION = new Typename("meta", "definition");
	public static final Typename ELEMENT = new Typename("meta", "element");
	public static final Typename FIELD = new Typename("meta", "field");
	public static final Typename INTERFACE = new Typename("meta", "interface");
	public static final Typename METHOD = new Typename("meta", "method");
	public static final Typename NAMESPACE = new Typename("meta", "namespace");
	public static final Typename RECORD = new Typename("meta", "record");
	public static final Typename SEQUENCE = new Typename("meta", "sequence");
	public static final Typename SIGNATURE = new Typename("meta", "signature");
	public static final Typename TYPENAME = new Typename("meta", "type_name");
	public static final Typename ENTRY = new Typename("meta", "entry");
	public static final Typename UNION = new Typename("meta", "union");
	public static final Typename ARRAY = new Typename("meta", "array");

	// Strings as well as UTF8, UTF16, ASCII and other encodings need to better types.
	public static final Typename UTF16_CHAR = new Typename ("meta", "utf16");
	public static final Typename STRING = new Typename("meta", "string");

	// Boolean is a unique set type.
	public static final Typename BOOLEAN = new Typename("meta", "boolean");

	// Integers like the following need better definitions.
	public static final Typename INT8 = new Typename("meta", "int8");
	public static final Typename INT16 = new Typename("meta", "int16");
	public static final Typename INT32 = new Typename("meta", "int32");
	public static final Typename INT64 = new Typename("meta", "int64");
	public static final Typename UINT8 = new Typename("meta", "uint8");
	public static final Typename UINT16 = new Typename("meta", "uint16");
	public static final Typename UINT32 = new Typename("meta", "uint32");
	public static final Typename UINT64 = new Typename("meta", "uint64");

	// Reals like float and double are a subset of ieee 754.
	public static final Typename FLOAT = new Typename("meta", "float");
	public static final Typename DOUBLE = new Typename("meta", "double");

	// UUID can describe a number of UUID subtypes.
	public static final Typename UUID = new Typename("meta", "uuid");

	// void is
	public static final Typename VOID = new Typename("meta", "void");

	// Needs to be revisited. Dates are a whole category of types.
	public static final Typename DATE = new Typename("meta", "date");
}
