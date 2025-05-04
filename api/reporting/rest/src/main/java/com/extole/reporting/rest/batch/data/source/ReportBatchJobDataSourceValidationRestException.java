package com.extole.reporting.rest.batch.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class ReportBatchJobDataSourceValidationRestException extends BatchJobDataSourceValidationRestException {

    public static final ErrorCode<ReportBatchJobDataSourceValidationRestException> REPORT_NOT_FOUND =
        new ErrorCode<>("batch_job_data_source_report_not_found", 400,
            "Report not found");

    public static final ErrorCode<ReportBatchJobDataSourceValidationRestException> REPORT_ID_MISSING =
        new ErrorCode<>("batch_job_data_source_report_id_missing", 400, "reportId is missing");

    public ReportBatchJobDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
