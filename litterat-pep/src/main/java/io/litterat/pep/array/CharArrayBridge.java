package io.litterat.pep.array;

public class CharArrayBridge {

	private static class CharIterator {
		int pos;
	}

	CharIterator iterator(char[] array) {
		return new CharIterator();
	}

	int size(char[] array) {
		return array.length;
	}

	char get(CharIterator iterator, char[] array) {
		return array[iterator.pos++];
	}

	void put(CharIterator iterator, char[] array, char value) {
		array[iterator.pos++] = value;
	}
}
