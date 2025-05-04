package com.extole.reporting.rest.impl.batch.data.source.request;

import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.request.BatchJobDataSourceRequest;
import com.extole.reporting.service.batch.BatchJobBuilder;

public interface BatchJobDataSourceRequestMapper<R extends BatchJobDataSourceRequest> {

    void upload(BatchJobBuilder builder, R request) throws BatchJobDataSourceValidationRestException;

    BatchJobDataSourceType getType();
}
