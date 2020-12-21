package io.litterat.test.pep.data;

import io.litterat.pep.Data;

public class PrimitiveArray {

	private final int[] intArray;

	@Data
	public PrimitiveArray(int[] intArray) {
		this.intArray = intArray;
	}

	public int[] intArray() {
		return intArray;
	}
}
