package io.litterat.bind.array;

public class PrimitiveBridges {

	public static Object getPrimitiveArrayBridge(Class<?> clss) {

		Object bridge = null;

		if (!clss.isPrimitive()) {
			bridge = new ObjectArrayBridge();
		} else if (clss == byte.class) {
			bridge = new ByteArrayBridge();
		} else if (clss == char.class) {
			bridge = new CharArrayBridge();
		} else if (clss == short.class) {
			bridge = new ShortArrayBridge();
		} else if (clss == int.class) {
			bridge = new IntArrayBridge();
		} else if (clss == long.class) {
			bridge = new LongArrayBridge();
		} else if (clss == float.class) {
			bridge = new FloatArrayBridge();
		} else if (clss == double.class) {
			bridge = new DoubleArrayBridge();
		} else if (clss == boolean.class) {
			bridge = new BooleanArrayBridge();
		} else {
			throw new IllegalArgumentException("Not recognised primitive");
		}

		return bridge;
	}
}
