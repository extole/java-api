package com.extole.reporting.rest.impl.report;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.report.type.ReportInvalidParametersException;
import com.extole.model.entity.report.type.ReportMissingAggregationInvalidParametersException;
import com.extole.reporting.rest.report.execution.ReportValidationRestException;

public final class ReportInvalidParametersRestExceptionMapper {

    private static final ReportInvalidParametersRestExceptionMapper INSTANCE =
        new ReportInvalidParametersRestExceptionMapper();

    public static ReportInvalidParametersRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private ReportInvalidParametersRestExceptionMapper() {
    }

    public ReportValidationRestException map(ReportInvalidParametersException exception) {
        if (exception instanceof ReportMissingAggregationInvalidParametersException castedException) {
            return RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
                .withErrorCode(ReportValidationRestException.REPORT_MISSING_AGGREGATION_PARAMETER)
                .addParameter("parameters", exception.getParameterNames())
                .addParameter("function_names", castedException.getFunctionNames())
                .withCause(exception).build();
        }

        return RestExceptionBuilder.newBuilder(ReportValidationRestException.class)
            .withErrorCode(ReportValidationRestException.REPORT_INVALID_PARAMETER)
            .addParameter("parameters", exception.getParameterNames())
            .addParameter("details", exception.getMessage())
            .withCause(exception).build();
    }
}
