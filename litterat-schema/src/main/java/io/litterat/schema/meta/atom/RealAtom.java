package io.litterat.schema.meta.atom;

import io.litterat.bind.Record;
import io.litterat.schema.meta.Atom;
import io.litterat.schema.annotation.SchemaType;

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
	@SchemaType(namespace = "schema", name = "atom_real")
	public static class AtomReal extends AtomAttribute {}

	@Record
	@SchemaType(namespace = "schema", name = "atom_ieee756")
	public static class AtomIEEE756 extends AtomAttribute {}

}
