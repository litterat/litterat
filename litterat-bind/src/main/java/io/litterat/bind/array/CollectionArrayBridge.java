package io.litterat.bind.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class CollectionArrayBridge {

	public Iterator iterator(Collection list) {
		return list.iterator();
	}

	public int size(Collection list) {
		return list.size();
	}

	public Object get(Iterator iterator, Collection list) {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void put(Iterator iterator, Collection list, Object value) {
		list.add(value);
	}

	public Collection constructor(int length) {
		return new ArrayList(length); // This is specific to the collection type.
	}

}
