package com.extole.reporting.rest.impl.batch.data.source.response;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.entity.batch.data.source.ReportBatchJobDataSource;
import com.extole.reporting.rest.batch.data.source.response.ReportBatchJobDataSourceResponse;

@Component
class ReportBatchJobDataSourceResponseMapper
    implements BatchJobDataSourceResponseMapper<ReportBatchJobDataSource, ReportBatchJobDataSourceResponse> {

    @Override
    public ReportBatchJobDataSourceResponse toResponse(ReportBatchJobDataSource dataSource) {
        return new ReportBatchJobDataSourceResponse(dataSource.getId().getValue(), dataSource.getReportId().getValue());
    }

    @Override
    public BatchJobDataSourceType getType() {
        return BatchJobDataSourceType.REPORT;
    }
}
