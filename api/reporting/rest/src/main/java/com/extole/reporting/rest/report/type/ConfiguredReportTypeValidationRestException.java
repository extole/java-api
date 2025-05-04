package com.extole.reporting.rest.report.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class ConfiguredReportTypeValidationRestException extends ReportTypeValidationRestException {

    public static final ErrorCode<ConfiguredReportTypeValidationRestException> MISSING_PARENT_REPORT_TYPE_ID =
        new ErrorCode<>("parent_report_type_id_missing", 400, "Parent report type id is missing");

    public ConfiguredReportTypeValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
