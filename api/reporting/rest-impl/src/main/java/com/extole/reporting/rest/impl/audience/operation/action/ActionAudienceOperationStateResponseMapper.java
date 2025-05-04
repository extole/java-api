package com.extole.reporting.rest.impl.audience.operation.action;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperation;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDetails;
import com.extole.reporting.rest.audience.operation.AudienceOperationErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationState;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;
import com.extole.reporting.rest.audience.operation.action.ActionAudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.action.ActionAudienceOperationStateResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationStateResponseMapper;

@Component
public class ActionAudienceOperationStateResponseMapper implements
    AudienceOperationStateResponseMapper<ActionAudienceOperationStateResponse,
        ActionAudienceOperationStateDebugResponse> {

    @Override
    public ActionAudienceOperationStateResponse toResponse(AudienceOperation audienceOperation,
        ZoneId timeZone) {
        return new ActionAudienceOperationStateResponse(
            AudienceOperationType.valueOf(audienceOperation.getState().getType().name()),
            AudienceOperationState.valueOf(audienceOperation.getState().getState().name()),
            audienceOperation.getState().getErrorCode()
                .map(errorCode -> AudienceOperationErrorCode.valueOf(errorCode.name())),
            audienceOperation.getState().getInputRowsCount(),
            audienceOperation.getState().getCreatedDate().atZone(timeZone),
            audienceOperation.getState().getUpdatedDate().atZone(timeZone),
            audienceOperation.getState().getStateReachedDate().atZone(timeZone));
    }

    @Override
    public ActionAudienceOperationStateDebugResponse toDebugResponse(AudienceOperationDetails audienceOperation,
        ZoneId timeZone) {
        return new ActionAudienceOperationStateDebugResponse(
            AudienceOperationType.valueOf(audienceOperation.getState().getType().name()),
            AudienceOperationState.valueOf(audienceOperation.getState().getState().name()),
            audienceOperation.getState().getDebugMessage(),
            audienceOperation.getState().getErrorCode()
                .map(errorCode -> AudienceOperationErrorCode.valueOf(errorCode.name())),
            audienceOperation.getState().getErrorMessage(),
            audienceOperation.getState().getInputRowsCount(),
            audienceOperation.getState().getCreatedDate().atZone(timeZone),
            audienceOperation.getState().getUpdatedDate().atZone(timeZone),
            audienceOperation.getState().getStateReachedDate().atZone(timeZone));
    }

}
