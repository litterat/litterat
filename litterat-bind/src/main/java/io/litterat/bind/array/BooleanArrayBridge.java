package io.litterat.bind.array;

public class BooleanArrayBridge {

	private static class BooleanIterator {
		int pos;
	}

	public BooleanIterator iterator(boolean[] array) {
		return new BooleanIterator();
	}

	public int size(boolean[] array) {
		return array.length;
	}

	public boolean get(BooleanIterator iterator, boolean[] array) {
		return array[iterator.pos++];
	}

	public void put(BooleanIterator iterator, boolean[] array, boolean value) {
		array[iterator.pos++] = value;
	}
}
