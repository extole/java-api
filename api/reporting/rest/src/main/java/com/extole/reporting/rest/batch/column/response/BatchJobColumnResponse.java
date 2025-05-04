package com.extole.reporting.rest.batch.column.response;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.reporting.rest.batch.column.BatchJobColumnType;
import com.extole.reporting.rest.batch.column.BatchJobColumnValidationPolicy;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BatchJobColumnResponse.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FullNameMatchBatchJobColumnResponse.class,
        name = FullNameMatchBatchJobColumnResponse.COLUMN_TYPE),
    @JsonSubTypes.Type(value = PatternNameMatchBatchJobColumnResponse.class,
        name = PatternNameMatchBatchJobColumnResponse.COLUMN_TYPE)
})
public abstract class BatchJobColumnResponse {

    protected static final String VALIDATION_POLICY = "validation_policy";
    protected static final String PREFIX = "prefix";
    protected static final String TYPE = "type";

    private final BatchJobColumnValidationPolicy validationPolicy;
    private final Optional<String> prefix;
    private final BatchJobColumnType type;

    public BatchJobColumnResponse(
        @JsonProperty(VALIDATION_POLICY) BatchJobColumnValidationPolicy validationPolicy,
        @JsonProperty(PREFIX) Optional<String> prefix,
        @JsonProperty(TYPE) BatchJobColumnType type) {
        this.validationPolicy = validationPolicy;
        this.prefix = prefix;
        this.type = type;
    }

    @JsonProperty(VALIDATION_POLICY)
    public BatchJobColumnValidationPolicy getValidationPolicy() {
        return validationPolicy;
    }

    @JsonProperty(PREFIX)
    public Optional<String> getPrefix() {
        return prefix;
    }

    @JsonProperty(TYPE)
    public BatchJobColumnType getType() {
        return type;
    }

}
