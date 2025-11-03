package com.extole.common.lang;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonValue;

public class ThreadSafeLazyLoadedSupplier<T> implements Supplier<T> {
    private final AtomicReference<T> value = new AtomicReference<>();
    private final Lock lock = new ReentrantLock();
    private final Supplier<T> delegate;

    public ThreadSafeLazyLoadedSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        T result = value.get();
        if (result != null) {
            return result;
        }

        lock.lock();
        try {
            result = value.get();
            if (result == null) {
                result = delegate.get();
                value.set(result);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    @JsonValue // TODO ENG-7981 to force our ToString to use it
    @Override
    public String toString() {
        return value.toString();
    }

}
