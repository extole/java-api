package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceValidationRestException;

public class ReportAudienceOperationDataSourceValidationRestException
    extends AudienceOperationDataSourceValidationRestException {

    public static final ErrorCode<ReportAudienceOperationDataSourceValidationRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("modification_audience_operation_report_data_source_report_not_found", 400, "Report not found",
            "report_id");

    public static final ErrorCode<ReportAudienceOperationDataSourceValidationRestException> MISSING_REPORT_ID =
        new ErrorCode<>(
            "modification_audience_operation_report_data_source_missing_report_id", 400, "Report ID is missing");

    public static final ErrorCode<ReportAudienceOperationDataSourceValidationRestException> REPORT_NOT_ACCESSIBLE =
        new ErrorCode<>("modification_audience_operation_report_data_source_report_not_accessible", 400,
            "Report is not accessible for clients", "report_id");

    public ReportAudienceOperationDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
