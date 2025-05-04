package com.extole.api.impl.batch.column;

import javax.annotation.Nullable;

import com.extole.api.batch.column.PatternNameMatchBatchJobColumn;
import com.extole.common.lang.ToString;

public class PatternNameMatchBatchJobColumnImpl extends BatchJobColumnImpl implements PatternNameMatchBatchJobColumn {

    private final String namePattern;

    public PatternNameMatchBatchJobColumnImpl(ValidationPolicy validationPolicy, Type type, @Nullable String prefix,
        String namePattern) {
        super(validationPolicy, type, prefix);
        this.namePattern = namePattern;
    }

    public PatternNameMatchBatchJobColumnImpl(
        com.extole.reporting.entity.batch.column.PatternNameMatchBatchJobColumn column) {
        super(ValidationPolicy.valueOf(column.getValidationPolicy().name()), Type.valueOf(column.getType().name()),
            column.getPrefix().orElse(null));
        this.namePattern = column.getNamePattern().pattern();
    }

    @Override
    public String getNamePattern() {
        return namePattern;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
