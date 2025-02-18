package io.litterat.schema.meta.atom;

import io.litterat.annotation.Typename;
import io.litterat.annotation.Record;
import io.litterat.schema.meta.Atom;

public class IntegerAtom extends Atom {

	public IntegerAtom(AtomAttribute[] attributes) {
		super(attributes);
	}

	@Record
	@Typename(namespace = "schema", name = "atom_integer")
	public static class AtomInteger extends AtomAttribute {}

	@Record
	@Typename(namespace = "schema", name = "atom_unsigned")
	public static class AtomUnsigned extends AtomAttribute {}

	@Record
	@Typename(namespace = "schema", name = "atom_signed")
	public static class AtomSigned extends AtomAttribute {}

	@Record
	@Typename(namespace = "schema", name = "atom_big_endian")
	public static class AtomBigEndian extends AtomAttribute {}

	@Record
	@Typename(namespace = "schema", name = "atom_little_endian")
	public static class AtomLittleEndian extends AtomAttribute {}

}
