package io.litterat.bind.array;

public class ObjectArrayBridge {

	private static class ObjectIterator {
		int pos;
	}

	public ObjectIterator iterator(Object[] array) {
		return new ObjectIterator();
	}

	public int size(Object[] array) {
		return array.length;
	}

	public Object get(ObjectIterator iterator, Object[] array) {
		return array[iterator.pos++];
	}

	public void put(ObjectIterator iterator, Object[] array, Object value) {
		array[iterator.pos++] = value;
	}
}
