package com.extole.reporting.rest.impl.audience.operation.modification;

import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperation;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDetails;
import com.extole.reporting.entity.report.audience.operation.modification.ModificationAudienceOperationState;
import com.extole.reporting.entity.report.audience.operation.modification.ModificationAudienceOperationStateDetails;
import com.extole.reporting.rest.audience.operation.AudienceOperationErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationState;
import com.extole.reporting.rest.audience.operation.AudienceOperationType;
import com.extole.reporting.rest.audience.operation.modification.AudienceOperationResultResponse;
import com.extole.reporting.rest.audience.operation.modification.ModificationAudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.modification.ModificationAudienceOperationStateResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationStateResponseMapper;

@Component
public class ModificationAudienceOperationStateResponseMapper implements
    AudienceOperationStateResponseMapper<ModificationAudienceOperationStateResponse,
        ModificationAudienceOperationStateDebugResponse> {

    @Override
    public ModificationAudienceOperationStateResponse toResponse(AudienceOperation audienceOperation, ZoneId timeZone) {
        ModificationAudienceOperationState state = (ModificationAudienceOperationState) audienceOperation.getState();
        return new ModificationAudienceOperationStateResponse(
            AudienceOperationType.valueOf(state.getType().name()),
            AudienceOperationState.valueOf(state.getState().name()),
            state.getErrorCode().map(errorCode -> AudienceOperationErrorCode.valueOf(errorCode.name())),
            state.getInputRowsCount(),
            state.getCreatedDate().atZone(timeZone),
            state.getUpdatedDate().atZone(timeZone),
            state.getStateReachedDate().atZone(timeZone),
            state.getRetryCount(),
            state.getResult().map(result -> new AudienceOperationResultResponse(result.getMemberCount(),
                result.getAnonymousCount(),
                result.getNonProcessedCount(),
                result.getLastUpdateDate().atZone(timeZone))));
    }

    @Override
    public ModificationAudienceOperationStateDebugResponse toDebugResponse(AudienceOperationDetails audienceOperation,
        ZoneId timeZone) {
        ModificationAudienceOperationStateDetails state =
            (ModificationAudienceOperationStateDetails) audienceOperation.getState();
        return new ModificationAudienceOperationStateDebugResponse(
            AudienceOperationType.valueOf(state.getType().name()),
            AudienceOperationState.valueOf(state.getState().name()),
            state.getDebugMessage(),
            state.getErrorCode().map(errorCode -> AudienceOperationErrorCode.valueOf(errorCode.name())),
            state.getErrorMessage(),
            state.getInputRowsCount(),
            state.getCreatedDate().atZone(timeZone),
            state.getUpdatedDate().atZone(timeZone),
            state.getStateReachedDate().atZone(timeZone),
            state.getRetryCount(),
            state.getResult().map(result -> new AudienceOperationResultResponse(result.getMemberCount(),
                result.getAnonymousCount(),
                result.getNonProcessedCount(),
                result.getLastUpdateDate().atZone(timeZone))));
    }

}
