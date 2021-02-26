package io.litterat.bind.array;

public class CharArrayBridge {

	private static class CharIterator {
		int pos;
	}

	public CharIterator iterator(char[] array) {
		return new CharIterator();
	}

	public int size(char[] array) {
		return array.length;
	}

	public char get(CharIterator iterator, char[] array) {
		return array[iterator.pos++];
	}

	public void put(CharIterator iterator, char[] array, char value) {
		array[iterator.pos++] = value;
	}
}
