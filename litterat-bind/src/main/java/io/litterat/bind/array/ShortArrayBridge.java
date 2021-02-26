package io.litterat.bind.array;

public class ShortArrayBridge {

	private static class ShortIterator {
		int pos;
	}

	public ShortIterator iterator(short[] array) {
		return new ShortIterator();
	}

	public int size(short[] array) {
		return array.length;
	}

	public short get(ShortIterator iterator, short[] array) {
		return array[iterator.pos++];
	}

	public void put(ShortIterator iterator, short[] array, short value) {
		array[iterator.pos++] = value;
	}
}
