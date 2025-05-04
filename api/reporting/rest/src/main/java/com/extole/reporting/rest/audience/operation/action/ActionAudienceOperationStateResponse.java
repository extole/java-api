package com.extole.reporting.rest.audience.operation.action;

import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.audience.operation.AudienceOperationErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationState;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;

public class ActionAudienceOperationStateResponse extends AudienceOperationStateResponse {

    public ActionAudienceOperationStateResponse(@JsonProperty(TYPE) AudienceOperationType type,
        @JsonProperty(STATE) AudienceOperationState state,
        @JsonProperty(ERROR_CODE) Optional<AudienceOperationErrorCode> errorCode,
        @JsonProperty(INPUT_ROWS_COUNT) Optional<Long> inputRowsCount,
        @JsonProperty(CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(UPDATED_DATE) ZonedDateTime updateDate,
        @JsonProperty(STATE_REACHED_DATE) ZonedDateTime stateReachedDate) {
        super(type, state, errorCode, inputRowsCount, createdDate, updateDate, stateReachedDate);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
