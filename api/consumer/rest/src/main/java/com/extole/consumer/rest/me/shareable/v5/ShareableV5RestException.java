package com.extole.consumer.rest.me.shareable.v5;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ShareableV5RestException extends ExtoleRestException {
    public static final ErrorCode<ShareableV5RestException> SHAREABLE_NOT_FOUND =
        new ErrorCode<>("shareable_not_found", 403, "Shareable not found.", "code");

    public static final ErrorCode<ShareableV5RestException> PERSON_NOT_REWARDABLE =
        new ErrorCode<>("person_not_rewardable", 403, "Person does not have email address", "person_id");

    public static final ErrorCode<ShareableV5RestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 403, "Person not found", "person_id");

    public ShareableV5RestException(String uniqueId, ErrorCode<ShareableV5RestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
