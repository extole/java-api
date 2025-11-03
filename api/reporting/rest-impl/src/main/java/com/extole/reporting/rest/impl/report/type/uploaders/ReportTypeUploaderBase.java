package com.extole.reporting.rest.impl.report.type.uploaders;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.report.type.ReportType;
import com.extole.model.entity.report.type.ReportTypeTagType;
import com.extole.model.service.report.type.ReportTypeBuilder;
import com.extole.model.service.report.type.ReportTypeNotFoundException;
import com.extole.model.service.report.type.ReportTypeService;
import com.extole.reporting.rest.report.ReportTypeRestException;
import com.extole.reporting.rest.report.type.ReportTypeCreateRequest;
import com.extole.reporting.rest.report.type.ReportTypeUpdateRequest;

@Component
public class ReportTypeUploaderBase {

    private final ReportTypeService reportTypeService;

    @Autowired
    public ReportTypeUploaderBase(ReportTypeService reportTypeService) {
        this.reportTypeService = reportTypeService;
    }

    ReportTypeBuilder<?, ?> builder(Authorization authorization, ReportTypeCreateRequest reportTypeRequest)
        throws AuthorizationException {
        return reportTypeService.createReportType(authorization,
            ReportType.Type.valueOf(reportTypeRequest.getType().name()));
    }

    public ReportTypeBuilder<?, ?> builder(Authorization authorization, String name)
        throws AuthorizationException, ReportTypeRestException {
        try {
            return reportTypeService.updateReportType(authorization, name);
        } catch (ReportTypeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ReportTypeRestException.class)
                .withErrorCode(ReportTypeRestException.REPORT_TYPE_NOT_FOUND)
                .addParameter("id", name)
                .withCause(e)
                .build();
        }
    }

    void upload(ReportTypeCreateRequest reportTypeRequest, ReportTypeBuilder<?, ?> builder) {
        if (reportTypeRequest.getTags().isPresent()) {
            builder.withTags(reportTypeRequest.getTags().get().stream()
                .map(tag -> new ReportTypeTagImpl(tag.getName(),
                    tag.getType().map(tagType -> ReportTypeTagType.valueOf(tagType.name()))
                        .orElse(ReportTypeTagType.PRIVATE)))
                .collect(Collectors.toSet()));
        }
    }

    void upload(ReportTypeUpdateRequest reportTypeRequest, ReportTypeBuilder<?, ?> builder) {
        if (reportTypeRequest.getTags().isPresent()) {
            builder.withTags(reportTypeRequest.getTags().get().stream()
                .map(tag -> new ReportTypeTagImpl(tag.getName(),
                    tag.getType().map(tagType -> ReportTypeTagType.valueOf(tagType.name()))
                        .orElse(ReportTypeTagType.PRIVATE)))
                .collect(Collectors.toSet()));
        }
    }
}
