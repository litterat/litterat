package io.litterat.schema.meta.atom;

import io.litterat.annotation.Typename;
import io.litterat.annotation.Record;
import io.litterat.schema.meta.Atom;

/**
 *
 * A definition of a real number. This could possible
 *
 * @formatter:off
 * TODO This could possible extends from a Number type.
 * TODO Common restrictions such as min/max/set required.
 * TODO Should be able to base one real definition on a previous definition.
 * @formatter:on
 *
 */
public class RealAtom extends Atom {

	public RealAtom(AtomAttribute[] attributes) {
		super(attributes);
	}

	@Record
	@Typename(namespace = "schema", name = "atom_real")
	public static class AtomReal extends AtomAttribute {}

	@Record
	@Typename(namespace = "schema", name = "atom_ieee756")
	public static class AtomIEEE756 extends AtomAttribute {}

}
