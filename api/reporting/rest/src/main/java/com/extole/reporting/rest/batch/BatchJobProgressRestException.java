package com.extole.reporting.rest.batch;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BatchJobProgressRestException extends ExtoleRestException {
    public static final ErrorCode<BatchJobProgressRestException> PROGRESS_RETRIEVAL_FAILURE =
        new ErrorCode<>("batch_job_progress_retrieval_failure", 400, "Unable to get progress", "batch_job_id");

    public static final ErrorCode<BatchJobProgressRestException> PROGRESS_NOT_FOUND =
        new ErrorCode<>("batch_job_progress_not_found", 400, "Progress not found", "batch_job_id");

    public BatchJobProgressRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
