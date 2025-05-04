package com.extole.reporting.rest.batch;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BatchJobValidationRestException extends ExtoleRestException {

    public static final ErrorCode<BatchJobValidationRestException> EVENT_NAME_INVALID =
        new ErrorCode<>("batch_job_event_name_invalid", 400,
            "Invalid batch job event_name, name should have a value, max length 255");

    public static final ErrorCode<BatchJobValidationRestException> NAME_INVALID =
        new ErrorCode<>("batch_job_name_invalid", 400,
            "Invalid batch job name, name should have a value, max length 255");

    public static final ErrorCode<BatchJobValidationRestException> TAG_INVALID =
        new ErrorCode<>("batch_job_tag_invalid", 400, "Invalid batch job tag, name should have a value, max length 255",
            "tag");

    public static final ErrorCode<BatchJobValidationRestException> DATA_SOURCE_EMPTY =
        new ErrorCode<>("batch_job_data_source_empty", 400, "Data source is required");

    public static final ErrorCode<BatchJobValidationRestException> UNAUTHORIZED_SCOPES = new ErrorCode<>(
        "batch_job_unauthorized_scopes", 400, "Attempt to update scopes to unauthorized values");

    public BatchJobValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
