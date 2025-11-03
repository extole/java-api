package com.extole.common.log;

import java.util.function.Supplier;

public class LazyLogArgument {
    private final Supplier<String> supplier;

    public LazyLogArgument(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String toString() {
        return supplier.get();
    }
}
