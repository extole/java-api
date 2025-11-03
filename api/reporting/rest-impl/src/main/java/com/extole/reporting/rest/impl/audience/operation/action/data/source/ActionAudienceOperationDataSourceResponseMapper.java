package com.extole.reporting.rest.impl.audience.operation.action.data.source;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.entity.report.audience.operation.action.data.source.ActionAudienceOperationDataSource;
import com.extole.reporting.rest.audience.operation.action.data.source.ActionAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceResponseMapper;

@Component
public class ActionAudienceOperationDataSourceResponseMapper implements
    AudienceOperationDataSourceResponseMapper<ActionAudienceOperationDataSource,
        ActionAudienceOperationDataSourceResponse> {

    @Override
    public ActionAudienceOperationDataSourceResponse toResponse(ActionAudienceOperationDataSource dataSource) {
        return new ActionAudienceOperationDataSourceResponse(dataSource.getEventName(),
            dataSource.getEventColumns(),
            dataSource.getEventData());
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.ACTION;
    }

}
