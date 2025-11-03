package com.extole.reporting.rest.impl.audience.operation;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSource;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;

public interface AudienceOperationDataSourceResponseMapper<FROM extends AudienceOperationDataSource, TO extends AudienceOperationDataSourceResponse> {

    TO toResponse(FROM source);

    AudienceOperationDataSourceType getType();

}
