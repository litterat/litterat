package io.litterat.pep.array;

public class BooleanArrayBridge {

	private static class BooleanIterator {
		int pos;
	}

	BooleanIterator iterator(boolean[] array) {
		return new BooleanIterator();
	}

	int size(boolean[] array) {
		return array.length;
	}

	boolean get(BooleanIterator iterator, boolean[] array) {
		return array[iterator.pos++];
	}

	void put(BooleanIterator iterator, boolean[] array, boolean value) {
		array[iterator.pos++] = value;
	}
}
