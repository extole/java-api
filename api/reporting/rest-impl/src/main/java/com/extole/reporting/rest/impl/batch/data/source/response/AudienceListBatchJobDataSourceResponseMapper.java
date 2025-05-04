package com.extole.reporting.rest.impl.batch.data.source.response;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.batch.data.source.AudienceListBatchJobDataSource;
import com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.rest.batch.data.source.response.AudienceListBatchJobDataSourceResponse;

@Component
class AudienceListBatchJobDataSourceResponseMapper implements
    BatchJobDataSourceResponseMapper<AudienceListBatchJobDataSource, AudienceListBatchJobDataSourceResponse> {

    @Override
    public AudienceListBatchJobDataSourceResponse toResponse(AudienceListBatchJobDataSource dataSource) {
        return new AudienceListBatchJobDataSourceResponse(dataSource.getId().getValue(),
            dataSource.getAudienceListId().getValue());
    }

    @Override
    public BatchJobDataSourceType getType() {
        return BatchJobDataSourceType.AUDIENCE_LIST;
    }
}
