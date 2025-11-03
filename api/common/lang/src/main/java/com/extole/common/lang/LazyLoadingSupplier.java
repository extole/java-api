package com.extole.common.lang;

import java.util.function.Supplier;

public class LazyLoadingSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;

    public LazyLoadingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

}
