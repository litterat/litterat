package io.litterat.bind.array;

public class DoubleArrayBridge {

	private static class DoubleIterator {
		int pos;
	}

	public DoubleIterator iterator(double[] array) {
		return new DoubleIterator();
	}

	public int size(double[] array) {
		return array.length;
	}

	public double get(DoubleIterator iterator, double[] array) {
		return array[iterator.pos++];
	}

	public void put(DoubleIterator iterator, double[] array, double value) {
		array[iterator.pos++] = value;
	}
}
