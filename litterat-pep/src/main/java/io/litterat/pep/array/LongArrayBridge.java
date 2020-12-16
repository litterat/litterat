package io.litterat.pep.array;

public class LongArrayBridge {

	private static class LongIterator {
		int pos;
	}

	public LongIterator iterator(long[] array) {
		return new LongIterator();
	}

	public int size(long[] array) {
		return array.length;
	}

	public long get(LongIterator iterator, long[] array) {
		return array[iterator.pos++];
	}

	public void put(LongIterator iterator, long[] array, long value) {
		array[iterator.pos++] = value;
	}
}
