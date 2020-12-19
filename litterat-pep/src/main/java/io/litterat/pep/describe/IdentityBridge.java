package io.litterat.pep.describe;

import io.litterat.pep.DataBridge;
import io.litterat.pep.PepException;

/**
 *
 * The identity bridge. Mainly here as a placeholder for the default bridge
 * in @Field annotation.
 */
@SuppressWarnings("rawtypes")
public class IdentityBridge implements DataBridge {

	@Override
	public Object toData(Object b) throws PepException {

		return b;
	}

	@Override
	public Object toObject(Object s) throws PepException {
		return s;
	}

}
