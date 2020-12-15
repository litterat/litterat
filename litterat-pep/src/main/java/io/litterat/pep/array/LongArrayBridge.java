package io.litterat.pep.array;

public class LongArrayBridge {

	private static class LongIterator {
		int pos;
	}

	LongIterator iterator(long[] array) {
		return new LongIterator();
	}

	int size(long[] array) {
		return array.length;
	}

	long get(LongIterator iterator, long[] array) {
		return array[iterator.pos++];
	}

	void put(LongIterator iterator, long[] array, long value) {
		array[iterator.pos++] = value;
	}
}
