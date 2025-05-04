package com.extole.reporting.rest.impl.batch.data.source.request;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceType;
import com.extole.reporting.rest.batch.data.source.BatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.ReportBatchJobDataSourceValidationRestException;
import com.extole.reporting.rest.batch.data.source.request.ReportBatchJobDataSourceRequest;
import com.extole.reporting.service.batch.BatchJobBuilder;
import com.extole.reporting.service.batch.data.source.BatchJobDataSourceEmptyIdException;
import com.extole.reporting.service.batch.data.source.BatchJobDataSourceMissingIdException;
import com.extole.reporting.service.batch.data.source.ReportBatchJobDataSourceBuilder;
import com.extole.reporting.service.batch.data.source.ReportBatchJobDataSourceNotFoundException;

@Component
class ReportBatchJobDataSourceRequestMapper
    implements BatchJobDataSourceRequestMapper<ReportBatchJobDataSourceRequest> {

    @Override
    public void upload(BatchJobBuilder builder, ReportBatchJobDataSourceRequest request)
        throws BatchJobDataSourceValidationRestException {
        ReportBatchJobDataSourceBuilder reportDataSourceBuilder =
            builder.withDataSource(com.extole.reporting.entity.batch.data.source.BatchJobDataSourceType.REPORT);

        try {
            if (!Strings.isNullOrEmpty(request.getReportId())) {
                reportDataSourceBuilder.withDataSourceId(Id.valueOf(request.getReportId()));
            }
            reportDataSourceBuilder.done();
        } catch (ReportBatchJobDataSourceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportBatchJobDataSourceValidationRestException.class)
                .withErrorCode(ReportBatchJobDataSourceValidationRestException.REPORT_NOT_FOUND)
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceMissingIdException e) {
            throw RestExceptionBuilder.newBuilder(ReportBatchJobDataSourceValidationRestException.class)
                .withErrorCode(ReportBatchJobDataSourceValidationRestException.REPORT_ID_MISSING)
                .withCause(e)
                .build();
        } catch (BatchJobDataSourceEmptyIdException e) {
            // should never happen
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public BatchJobDataSourceType getType() {
        return BatchJobDataSourceType.REPORT;
    }
}
