package com.extole.api.impl.batch.column;

import javax.annotation.Nullable;

import com.extole.api.batch.column.BatchJobColumn;

public class BatchJobColumnImpl implements BatchJobColumn {

    private final ValidationPolicy validationPolicy;
    private final Type type;
    private final String prefix;

    public BatchJobColumnImpl(ValidationPolicy validationPolicy, Type type, @Nullable String prefix) {
        this.validationPolicy = validationPolicy;
        this.type = type;
        this.prefix = prefix;
    }

    @Override
    public ValidationPolicy getValidationPolicy() {
        return validationPolicy;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Nullable
    @Override
    public String getPrefix() {
        return prefix;
    }

}
