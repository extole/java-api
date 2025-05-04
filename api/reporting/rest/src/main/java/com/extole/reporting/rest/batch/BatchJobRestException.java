package com.extole.reporting.rest.batch;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BatchJobRestException extends ExtoleRestException {
    public static final ErrorCode<BatchJobRestException> NOT_FOUND =
        new ErrorCode<>("batch_job_not_found", 404, "Batch job not found", "batch_job_id");

    public static final ErrorCode<BatchJobRestException> INVALID_STATE_TRANSITION = new ErrorCode<>(
        "batch_job_invalid_state_transition", 400, "Current state doesn't allow the specified operation",
        "batch_job_id", "current_status", "target_status");

    public static final ErrorCode<BatchJobRestException> DELETE_NOT_ALLOWED =
        new ErrorCode<>("batch_job_delete_not_allowed", 400, "Batch job cannot be deleted while it's in current status",
            "batch_job_id", "status");

    public static final ErrorCode<BatchJobRestException> LOCKED =
        new ErrorCode<>("batch_job_locked", 423, "Batch job is locked by another process. Please try again later.",
            "batch_job_id");

    public BatchJobRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
