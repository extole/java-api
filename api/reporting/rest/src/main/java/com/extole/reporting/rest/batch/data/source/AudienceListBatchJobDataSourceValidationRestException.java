package com.extole.reporting.rest.batch.data.source;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class AudienceListBatchJobDataSourceValidationRestException extends BatchJobDataSourceValidationRestException {

    public static final ErrorCode<AudienceListBatchJobDataSourceValidationRestException> AUDIENCE_LIST_NOT_FOUND =
        new ErrorCode<>("batch_job_data_source_audience_list_not_found", 400,
            "AudienceList not found");

    public static final ErrorCode<AudienceListBatchJobDataSourceValidationRestException> AUDIENCE_LIST_ID_MISSING =
        new ErrorCode<>("batch_job_data_source_audience_list_id_missing", 400, "audienceListId is missing");

    public AudienceListBatchJobDataSourceValidationRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
