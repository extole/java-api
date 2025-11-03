package com.extole.reporting.rest.impl.report.type.uploaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.service.report.type.ReportTypeEmptyTagNameException;
import com.extole.model.service.report.type.SparkReportTypeBuilder;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;
import com.extole.reporting.rest.report.type.SparkReportTypeUpdateRequest;

@Component
public class SparkReportTypeUploader implements ReportTypeUpdateUploader<SparkReportTypeUpdateRequest> {

    private final ReportTypeUploaderBase reportTypeUploaderBase;

    @Autowired
    public SparkReportTypeUploader(ReportTypeUploaderBase reportTypeUploaderBase) {
        this.reportTypeUploaderBase = reportTypeUploaderBase;
    }

    @Override
    public ReportType upload(Authorization authorization, String name,
        SparkReportTypeUpdateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
        try {
            SparkReportTypeBuilder<?, ?> builder =
                (SparkReportTypeBuilder<?, ?>) reportTypeUploaderBase.builder(authorization, name);
            reportTypeUploaderBase.upload(reportTypeRequest, builder);

            return builder.save();
        } catch (ReportTypeEmptyTagNameException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeValidationRestException.class)
                .withErrorCode(ReportTypeValidationRestException.EMPTY_TAG_NAME)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ReportType.Type getType() {
        return ReportType.Type.SPARK;
    }
}
