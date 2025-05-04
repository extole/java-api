package com.extole.api.impl.batch.column;

import javax.annotation.Nullable;

import com.extole.api.batch.column.FullNameMatchBatchJobColumn;
import com.extole.common.lang.ToString;

public class FullNameMatchBatchJobColumnImpl extends BatchJobColumnImpl implements FullNameMatchBatchJobColumn {

    private final String name;

    public FullNameMatchBatchJobColumnImpl(ValidationPolicy validationPolicy, Type type, @Nullable String prefix,
        String name) {
        super(validationPolicy, type, prefix);
        this.name = name;
    }

    public FullNameMatchBatchJobColumnImpl(
        com.extole.reporting.entity.batch.column.FullNameMatchBatchJobColumn column) {
        super(ValidationPolicy.valueOf(column.getValidationPolicy().name()), Type.valueOf(column.getType().name()),
            column.getPrefix().orElse(null));
        this.name = column.getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
