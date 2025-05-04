package com.extole.reporting.rest.batch.column.response;

import java.util.Optional;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.batch.column.BatchJobColumnType;
import com.extole.reporting.rest.batch.column.BatchJobColumnValidationPolicy;

public class PatternNameMatchBatchJobColumnResponse extends BatchJobColumnResponse {

    static final String COLUMN_TYPE = "PATTERN_NAME_MATCH";

    private static final String NAME_PATTERN = "name_pattern";

    private final Pattern namePattern;

    public PatternNameMatchBatchJobColumnResponse(
        @JsonProperty(VALIDATION_POLICY) BatchJobColumnValidationPolicy validationPolicy,
        @JsonProperty(PREFIX) Optional<String> prefix,
        @JsonProperty(NAME_PATTERN) Pattern namePattern) {
        super(validationPolicy, prefix, BatchJobColumnType.PATTERN_NAME_MATCH);
        this.namePattern = namePattern;
    }

    @JsonProperty(NAME_PATTERN)
    public Pattern getNamePattern() {
        return namePattern;
    }

}
