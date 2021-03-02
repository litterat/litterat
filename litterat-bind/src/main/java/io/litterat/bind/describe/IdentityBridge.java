package io.litterat.bind.describe;

import io.litterat.bind.DataBridge;
import io.litterat.bind.DataBindException;

/**
 *
 * The identity bridge. Mainly here as a placeholder for the default bridge
 * in @Field annotation.
 */
@SuppressWarnings("rawtypes")
public class IdentityBridge implements DataBridge {

	@Override
	public Object toData(Object b) throws DataBindException {

		return b;
	}

	@Override
	public Object toObject(Object s) throws DataBindException {
		return s;
	}

}
