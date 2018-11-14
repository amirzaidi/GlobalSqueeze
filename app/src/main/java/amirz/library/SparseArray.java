package amirz.library;

import amirz.library.interfaces.Func;

public class SparseArray<T> extends android.util.SparseArray<T> {
    public T getOrAdd(int key, Func<T> getter) {
        T value = get(key);
        if (value == null) {
            value = getter.execute();
            put(key, value);
        }
        return value;
    }

    public synchronized T getOrAddSync(int key, Func<T> getter) {
        return getOrAdd(key, getter);
    }
}
