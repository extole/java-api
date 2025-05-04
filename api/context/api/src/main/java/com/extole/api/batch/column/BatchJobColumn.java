package com.extole.api.batch.column;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface BatchJobColumn {

    enum ValidationPolicy {
        OPTIONAL, REQUIRED_COLUMN, REQUIRED_VALUE
    }

    enum Type {
        FULL_NAME_MATCH, PATTERN_NAME_MATCH
    }

    ValidationPolicy getValidationPolicy();

    Type getType();

    @Nullable
    String getPrefix();

}
