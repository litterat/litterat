package io.litterat.pep.array;

public class ByteArrayBridge {

	private static class ByteIterator {
		int pos;
	}

	ByteIterator iterator(byte[] array) {
		return new ByteIterator();
	}

	int size(byte[] array) {
		return array.length;
	}

	byte get(ByteIterator iterator, byte[] array) {
		return array[iterator.pos++];
	}

	void put(ByteIterator iterator, byte[] array, byte value) {
		array[iterator.pos++] = value;
	}
}
