package io.litterat.pep.array;

public class ObjectArrayBridge {

	private static class ObjectIterator {
		int pos;
	}

	ObjectIterator iterator(Object[] array) {
		return new ObjectIterator();
	}

	int size(Object[] array) {
		return array.length;
	}

	Object get(ObjectIterator iterator, Object[] array) {
		return array[iterator.pos++];
	}

	void put(ObjectIterator iterator, Object[] array, Object value) {
		array[iterator.pos++] = value;
	}
}
