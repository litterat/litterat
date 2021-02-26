package io.litterat.test.bind.data;

import io.litterat.bind.Data;

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
