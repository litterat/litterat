package io.litterat.schema.meta.atom;

import io.litterat.bind.Record;
import io.litterat.schema.meta.Atom;
import io.litterat.schema.annotation.SchemaType;

public class IntegerAtom extends Atom {

	public IntegerAtom(AtomAttribute[] attributes) {
		super(attributes);
	}

	public IntegerAtom() {
		super(new AtomAttribute[] { new AtomInteger() });
	}

	@Record
	@SchemaType(namespace = "schema", name = "atom_integer")
	public static class AtomInteger extends AtomAttribute {}

	@Record
	@SchemaType(namespace = "schema", name = "atom_unsigned")
	public static class AtomUnsigned extends AtomAttribute {}

	@Record
	@SchemaType(namespace = "schema", name = "atom_signed")
	public static class AtomSigned extends AtomAttribute {}

	@Record
	@SchemaType(namespace = "schema", name = "atom_big_endian")
	public static class AtomBigEndian extends AtomAttribute {}

	@Record
	@SchemaType(namespace = "schema", name = "atom_little_endian")
	public static class AtomLittleEndian extends AtomAttribute {}

}
