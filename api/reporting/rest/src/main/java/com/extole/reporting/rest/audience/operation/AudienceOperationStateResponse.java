package com.extole.reporting.rest.audience.operation;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.action.ActionAudienceOperationStateResponse;
import com.extole.reporting.rest.audience.operation.modification.ModificationAudienceOperationStateResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceOperationStateResponse.TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ModificationAudienceOperationStateResponse.class, names = {"ADD", "REMOVE", "REPLACE"}),
    @JsonSubTypes.Type(value = ActionAudienceOperationStateResponse.class, name = "ACTION")
})
@Schema(discriminatorProperty = AudienceOperationStateResponse.TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = "ADD", schema = ModificationAudienceOperationStateResponse.class),
    @DiscriminatorMapping(value = "REMOVE", schema = ModificationAudienceOperationStateResponse.class),
    @DiscriminatorMapping(value = "REPLACE", schema = ModificationAudienceOperationStateResponse.class),
    @DiscriminatorMapping(value = "ACTION", schema = ActionAudienceOperationStateResponse.class)
})
public abstract class AudienceOperationStateResponse {

    protected static final String TYPE = "type";
    protected static final String STATE = "state";
    protected static final String ERROR_CODE = "error_code";
    protected static final String INPUT_ROWS_COUNT = "input_rows_count";
    protected static final String CREATED_DATE = "created_date";
    protected static final String UPDATED_DATE = "updated_date";
    protected static final String STATE_REACHED_DATE = "state_reached_date";

    private final AudienceOperationType type;
    private final AudienceOperationState state;
    private final Optional<AudienceOperationErrorCode> errorCode;
    private final Optional<Long> inputRowsCount;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final ZonedDateTime stateReachedDate;

    protected AudienceOperationStateResponse(@JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(STATE) AudienceOperationState state,
        @JsonProperty(ERROR_CODE) Optional<AudienceOperationErrorCode> errorCode,
        @JsonProperty(INPUT_ROWS_COUNT) Optional<Long> inputRowsCount,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(STATE_REACHED_DATE) ZonedDateTime stateReachedDate) {
        this.type = type;
        this.state = state;
        this.errorCode = errorCode;
        this.inputRowsCount = inputRowsCount;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.stateReachedDate = stateReachedDate;
    }

    @JsonProperty(TYPE)
    public AudienceOperationType getType() {
        return type;
    }

    @JsonProperty(STATE)
    public AudienceOperationState getState() {
        return state;
    }

    @JsonProperty(ERROR_CODE)
    public Optional<AudienceOperationErrorCode> getErrorCode() {
        return errorCode;
    }

    @JsonProperty(INPUT_ROWS_COUNT)
    public Optional<Long> getInputRowsCount() {
        return inputRowsCount;
    }

    @JsonProperty(CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @JsonProperty(STATE_REACHED_DATE)
    public ZonedDateTime getStateReachedDate() {
        return stateReachedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
