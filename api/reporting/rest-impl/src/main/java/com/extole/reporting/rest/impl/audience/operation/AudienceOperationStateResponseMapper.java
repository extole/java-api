package com.extole.reporting.rest.impl.audience.operation;

import java.time.ZoneId;

import com.extole.reporting.entity.report.audience.operation.AudienceOperation;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDetails;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateDebugResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationStateResponse;

public interface AudienceOperationStateResponseMapper<RESPONSE extends AudienceOperationStateResponse,
    DEBUG_RESPONSE extends AudienceOperationStateDebugResponse> {

    RESPONSE toResponse(AudienceOperation audienceOperation, ZoneId timeZone);

    DEBUG_RESPONSE toDebugResponse(AudienceOperationDetails audienceOperation, ZoneId timeZone);

}
