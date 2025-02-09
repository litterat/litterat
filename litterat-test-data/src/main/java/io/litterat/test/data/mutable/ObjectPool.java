package io.litterat.test.data.mutable;

import io.litterat.annotation.DataCreator;

import java.util.ArrayList;
import java.util.List;

public class ObjectPool<T extends PooledObject> implements DataCreator<T> {

    private List<T> objects = new ArrayList<>();



    public synchronized T acquire() {
        T t = null;
        if (objects.isEmpty()) {
            t = getInstance();
        } else {
            t = objects.removeFirst();
        }
        return t;
    }

    public synchronized void release(T t) {
        // reset the object on release, so don't need to later.
        t.reset();
        objects.addLast(t);
    }

    @Override
    public T getInstance() {
        return null;
    }
}
