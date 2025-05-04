package com.extole.reporting.rest.impl.audience.operation.modification.data.source;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.entity.report.audience.operation.AudienceOperationDataSourceType;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.modification.data.source.ReportAudienceOperationDataSourceValidationRestException;
import com.extole.reporting.rest.impl.audience.operation.AudienceOperationDataSourceRequestUploader;
import com.extole.reporting.service.audience.operation.AudienceOperationBuilder;
import com.extole.reporting.service.audience.operation.AudienceOperationDataSourceBuildException;
import com.extole.reporting.service.audience.operation.AudienceOperationParameterUpdateNotAllowedException;
import com.extole.reporting.service.audience.operation.modification.data.source.ReportAudienceOperationDataSourceBuilder;
import com.extole.reporting.service.audience.operation.modification.data.source.ReportAudienceOperationDataSourceInvalidReportScopesException;
import com.extole.reporting.service.audience.operation.modification.data.source.ReportAudienceOperationDataSourceMissingReportIdException;
import com.extole.reporting.service.audience.operation.modification.data.source.ReportAudienceOperationDataSourceNotFoundException;

@Component
public class ReportAudienceOperationDataSourceRequestUploader
    implements AudienceOperationDataSourceRequestUploader<ReportAudienceOperationDataSourceRequest> {

    @Override
    public void upload(AudienceOperationBuilder builder, ReportAudienceOperationDataSourceRequest request)
        throws ReportAudienceOperationDataSourceValidationRestException {
        try {
            ReportAudienceOperationDataSourceBuilder sourceBuilder =
                builder.withDataSource(AudienceOperationDataSourceType.REPORT);
            request.getEventColumns().ifPresent(eventColumns -> sourceBuilder.withEventColumns(eventColumns));
            request.getEventData().ifPresent(eventData -> sourceBuilder.withEventData(eventData));
            sourceBuilder.withReportId(Id.valueOf(request.getReportId().getValue()))
                .done();
        } catch (ReportAudienceOperationDataSourceNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(ReportAudienceOperationDataSourceValidationRestException.REPORT_NOT_FOUND)
                .addParameter("report_id", e.getReportId())
                .withCause(e)
                .build();
        } catch (ReportAudienceOperationDataSourceMissingReportIdException e) {
            throw RestExceptionBuilder.newBuilder(ReportAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(ReportAudienceOperationDataSourceValidationRestException.MISSING_REPORT_ID)
                .withCause(e)
                .build();
        } catch (ReportAudienceOperationDataSourceInvalidReportScopesException e) {
            throw RestExceptionBuilder
                .newBuilder(ReportAudienceOperationDataSourceValidationRestException.class)
                .withErrorCode(ReportAudienceOperationDataSourceValidationRestException.REPORT_NOT_ACCESSIBLE)
                .addParameter("report_id", request.getReportId())
                .withCause(e)
                .build();
        } catch (AudienceOperationDataSourceBuildException | AudienceOperationParameterUpdateNotAllowedException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public AudienceOperationDataSourceType getType() {
        return AudienceOperationDataSourceType.REPORT;
    }

}
