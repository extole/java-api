package com.extole.reporting.rest.batch.column.request;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.reporting.rest.batch.column.BatchJobColumnType;
import com.extole.reporting.rest.batch.column.BatchJobColumnValidationPolicy;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = BatchJobColumnRequest.TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FullNameMatchBatchJobColumnRequest.class,
        name = FullNameMatchBatchJobColumnRequest.COLUMN_TYPE),
    @JsonSubTypes.Type(value = PatternNameMatchBatchJobColumnRequest.class,
        name = PatternNameMatchBatchJobColumnRequest.COLUMN_TYPE)
})
public abstract class BatchJobColumnRequest {

    protected static final String VALIDATION_POLICY = "validation_policy";
    protected static final String PREFIX = "prefix";
    protected static final String TYPE = "type";

    private final BatchJobColumnValidationPolicy validationPolicy;
    private final Optional<String> prefix;
    private final BatchJobColumnType type;

    BatchJobColumnRequest(
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
