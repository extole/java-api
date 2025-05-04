package com.extole.reporting.rest.audience.operation;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.action.ActionAudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.modification.ModificationAudienceOperationStateDebugResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = AudienceOperationStateDebugResponse.TYPE, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ModificationAudienceOperationStateDebugResponse.class,
        names = {"ADD", "REMOVE", "REPLACE"}),
    @JsonSubTypes.Type(value = ActionAudienceOperationStateDebugResponse.class, name = "ACTION")
})
@Schema(discriminatorProperty = AudienceOperationStateDebugResponse.TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = "ADD",
        schema = ModificationAudienceOperationStateDebugResponse.class),
    @DiscriminatorMapping(value = "REMOVE",
        schema = ModificationAudienceOperationStateDebugResponse.class),
    @DiscriminatorMapping(value = "REPLACE",
        schema = ModificationAudienceOperationStateDebugResponse.class),
    @DiscriminatorMapping(value = "ACTION",
        schema = ActionAudienceOperationStateDebugResponse.class)
})
public abstract class AudienceOperationStateDebugResponse {

    protected static final String TYPE = "type";
    protected static final String STATE = "state";
    protected static final String DEBUG_MESSAGE = "debug_message";
    protected static final String ERROR_CODE = "error_code";
    protected static final String ERROR_MESSAGE = "error_message";
    protected static final String INPUT_ROWS_COUNT = "input_rows_count";
    protected static final String CREATED_DATE = "created_date";
    protected static final String UPDATED_DATE = "updated_date";
    protected static final String STATE_REACHED_DATE = "state_reached_date";

    private final AudienceOperationType type;
    private final AudienceOperationState state;
    private final Optional<String> debugMessage;
    private final Optional<AudienceOperationErrorCode> errorCode;
    private final Optional<String> errorMessage;
    private final Optional<Long> inputRowsCount;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;
    private final ZonedDateTime stateReachedDate;

    protected AudienceOperationStateDebugResponse(@JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(STATE) AudienceOperationState state,
        @JsonProperty(DEBUG_MESSAGE) Optional<String> debugMessage,
        @JsonProperty(ERROR_CODE) Optional<AudienceOperationErrorCode> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage,
        @JsonProperty(INPUT_ROWS_COUNT) Optional<Long> inputRowsCount,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updatedDate,
        @JsonProperty(STATE_REACHED_DATE) ZonedDateTime stateReachedDate) {
        this.type = type;
        this.state = state;
        this.debugMessage = debugMessage;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
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

    @JsonProperty(DEBUG_MESSAGE)
    public Optional<String> getDebugMessage() {
        return debugMessage;
    }

    @JsonProperty(ERROR_CODE)
    public Optional<AudienceOperationErrorCode> getErrorCode() {
        return errorCode;
    }

    @JsonProperty(ERROR_MESSAGE)
    public Optional<String> getErrorMessage() {
        return errorMessage;
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
