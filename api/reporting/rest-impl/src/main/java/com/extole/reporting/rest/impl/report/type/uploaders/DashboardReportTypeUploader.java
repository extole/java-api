package com.extole.reporting.rest.impl.report.type.uploaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.reporting.entity.report.ReportType;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.DashboardReportTypeUpdateRequest;
import com.extole.reporting.rest.report.type.ReportTypeValidationRestException;
import com.extole.reporting.service.report.type.DashboardReportTypeBuilder;
import com.extole.reporting.service.report.type.ReportTypeEmptyTagNameException;

@Component
public class DashboardReportTypeUploader implements ReportTypeUpdateUploader<DashboardReportTypeUpdateRequest> {

    private final ReportTypeUploaderBase reportTypeUploaderBase;

    @Autowired
    public DashboardReportTypeUploader(ReportTypeUploaderBase reportTypeUploaderBase) {
        this.reportTypeUploaderBase = reportTypeUploaderBase;
    }

    @Override
    public ReportType upload(Authorization authorization, String name,
        DashboardReportTypeUpdateRequest reportTypeRequest)
        throws AuthorizationException, ReportTypeRestException, ReportTypeValidationRestException {
        try {
            DashboardReportTypeBuilder<?, ?> builder =
                (DashboardReportTypeBuilder<?, ?>) reportTypeUploaderBase.builder(authorization, name);
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
        return ReportType.Type.DASHBOARD;
    }
}
