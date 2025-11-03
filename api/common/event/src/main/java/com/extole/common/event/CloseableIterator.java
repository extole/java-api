package com.extole.common.event;

import java.io.Closeable;
import java.util.Iterator;

public interface CloseableIterator<T> extends Iterator<T>,
    Closeable {
    void close();

    static <R> CloseableIterator<R> wrap(final Iterator<R> inner) {
        return new CloseableIterator<R>() {
            public void close() {
            }

            public boolean hasNext() {
                return inner.hasNext();
            }

            public R next() {
                return inner.next();
            }

            public void remove() {
                inner.remove();
            }
        };
    }
}
