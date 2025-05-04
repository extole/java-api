package com.extole.reporting.rest.audience.operation.action;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.AudienceOperationErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationState;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;

public class ActionAudienceOperationStateDebugResponse extends AudienceOperationStateDebugResponse {

    public ActionAudienceOperationStateDebugResponse(@JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(STATE) AudienceOperationState state,
        @JsonProperty(DEBUG_MESSAGE) Optional<String> debugMessage,
        @JsonProperty(ERROR_CODE) Optional<AudienceOperationErrorCode> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage,
        @JsonProperty(INPUT_ROWS_COUNT) Optional<Long> inputRowsCount,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updateDate,
        @JsonProperty(STATE_REACHED_DATE) ZonedDateTime stateReachedDate) {
        super(type, state, debugMessage, errorCode, errorMessage, inputRowsCount, createdDate, updateDate,
            stateReachedDate);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
