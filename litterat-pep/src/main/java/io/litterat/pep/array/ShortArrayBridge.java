package io.litterat.pep.array;

public class ShortArrayBridge {

	private static class ShortIterator {
		int pos;
	}

	ShortIterator iterator(short[] array) {
		return new ShortIterator();
	}

	int size(short[] array) {
		return array.length;
	}

	short get(ShortIterator iterator, short[] array) {
		return array[iterator.pos++];
	}

	void put(ShortIterator iterator, short[] array, short value) {
		array[iterator.pos++] = value;
	}
}
