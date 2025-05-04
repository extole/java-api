package com.extole.reporting.rest.batch.column.request;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.column.BatchJobColumnType;
import com.extole.reporting.rest.batch.column.BatchJobColumnValidationPolicy;

public class FullNameMatchBatchJobColumnRequest extends BatchJobColumnRequest {

    static final String COLUMN_TYPE = "FULL_NAME_MATCH";

    private static final String NAME = "name";

    private final String name;

    public FullNameMatchBatchJobColumnRequest(
        @JsonProperty(VALIDATION_POLICY) BatchJobColumnValidationPolicy validationPolicy,
        @JsonProperty(PREFIX) Optional<String> prefix,
        @JsonProperty(NAME) String name) {
        super(validationPolicy, prefix, BatchJobColumnType.FULL_NAME_MATCH);
        this.name = name;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

}
