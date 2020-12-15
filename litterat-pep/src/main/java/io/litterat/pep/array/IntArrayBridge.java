package io.litterat.pep.array;

public class IntArrayBridge {

	private static class IntIterator {
		int pos;
	}

	IntIterator iterator(int[] array) {
		return new IntIterator();
	}

	int size(int[] array) {
		return array.length;
	}

	int get(IntIterator iterator, int[] array) {
		return array[iterator.pos++];
	}

	void put(IntIterator iterator, int[] array, int value) {
		array[iterator.pos++] = value;
	}
}
