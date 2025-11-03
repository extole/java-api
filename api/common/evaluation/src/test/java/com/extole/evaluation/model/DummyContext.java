package com.extole.evaluation.model;

import com.extole.common.lang.LazyLoadingSupplier;

public class DummyContext {

    public static final LazyLoadingSupplier<DummyContext> DUMMY_CONTEXT_SUPPLIER =
        new LazyLoadingSupplier<>(() -> new DummyContext());

    public String getDummyText() {
        return "dummy text";
    }

}
