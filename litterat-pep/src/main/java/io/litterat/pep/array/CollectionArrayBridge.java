package io.litterat.pep.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class CollectionArrayBridge {

	Iterator iterator(Collection list) {
		return list.iterator();
	}

	int size(Collection list) {
		return list.size();
	}

	Object get(Iterator iterator, Collection list) {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	void add(Iterator iterator, Collection list, Object value) {
		list.add(value);
	}

	Collection constructor(int length) {
		return new ArrayList(length); // This is specific to the collection type.
	}

}
