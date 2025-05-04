package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.entity.report.audience.operation.modification.data.source.ReportAudienceOperationDataSource;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceResponse;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceResponseMapper;

@Component
public class ReportAudienceOperationDataSourceResponseMapper implements AudienceOperationDataSourceResponseMapper<
    ReportAudienceOperationDataSource, ReportAudienceOperationDataSourceResponse> {

    @Override
    public ReportAudienceOperationDataSourceResponse toResponse(ReportAudienceOperationDataSource dataSource) {
        return new ReportAudienceOperationDataSourceResponse(dataSource.getName(),
            dataSource.getEventColumns(),
            dataSource.getEventData(),
            Id.valueOf(dataSource.getReportId().getValue()));
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.REPORT;
    }

}
