package io.litterat.bind.array;

public class ByteArrayBridge {

	private static class ByteIterator {
		int pos;
	}

	public ByteIterator iterator(byte[] array) {
		return new ByteIterator();
	}

	public int size(byte[] array) {
		return array.length;
	}

	public byte get(ByteIterator iterator, byte[] array) {
		return array[iterator.pos++];
	}

	public void put(ByteIterator iterator, byte[] array, byte value) {
		array[iterator.pos++] = value;
	}
}
