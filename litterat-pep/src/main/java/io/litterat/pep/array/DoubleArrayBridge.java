package io.litterat.pep.array;

public class DoubleArrayBridge {

	private static class DoubleIterator {
		int pos;
	}

	DoubleIterator iterator(double[] array) {
		return new DoubleIterator();
	}

	int size(double[] array) {
		return array.length;
	}

	double get(DoubleIterator iterator, double[] array) {
		return array[iterator.pos++];
	}

	void put(DoubleIterator iterator, double[] array, double value) {
		array[iterator.pos++] = value;
	}
}
