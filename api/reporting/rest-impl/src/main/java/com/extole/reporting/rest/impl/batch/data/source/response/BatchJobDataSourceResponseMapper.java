package com.extole.reporting.rest.impl.batch.data.source.response;

import com.extole.reporting.entity.batch.data.source.BatchJobDataSource;
import com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.rest.batch.data.source.response.BatchJobDataSourceResponse;

public interface BatchJobDataSourceResponseMapper<FROM extends BatchJobDataSource, TO extends BatchJobDataSourceResponse> {

    TO toResponse(FROM source);

    BatchJobDataSourceType getType();
}
