package io.litterat.pep.array;

public class IntArrayBridge {

	private static class IntIterator {
		int pos;
	}

	public IntIterator iterator(int[] array) {
		return new IntIterator();
	}

	public int size(int[] array) {
		return array.length;
	}

	public int get(IntIterator iterator, int[] array) {
		return array[iterator.pos++];
	}

	public void put(IntIterator iterator, int[] array, int value) {
		array[iterator.pos++] = value;
	}
}
