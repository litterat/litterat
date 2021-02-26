package io.litterat.bind.array;

public class FloatArrayBridge {

	private static class FloatIterator {
		int pos;
	}

	public FloatIterator iterator(float[] array) {
		return new FloatIterator();
	}

	public int size(float[] array) {
		return array.length;
	}

	public float get(FloatIterator iterator, float[] array) {
		return array[iterator.pos++];
	}

	public void put(FloatIterator iterator, float[] array, float value) {
		array[iterator.pos++] = value;
	}
}
