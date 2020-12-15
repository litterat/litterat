package io.litterat.pep.array;

public class FloatArrayBridge {

	private static class FloatIterator {
		int pos;
	}

	FloatIterator iterator(float[] array) {
		return new FloatIterator();
	}

	int size(float[] array) {
		return array.length;
	}

	float get(FloatIterator iterator, float[] array) {
		return array[iterator.pos++];
	}

	void put(FloatIterator iterator, float[] array, float value) {
		array[iterator.pos++] = value;
	}
}
