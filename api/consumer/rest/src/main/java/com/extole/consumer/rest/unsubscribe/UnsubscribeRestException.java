package com.extole.consumer.rest.unsubscribe;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UnsubscribeRestException extends ExtoleRestException {

    public static final ErrorCode<UnsubscribeRestException> MISSING_ENCRYPTED_EMAIL =
        new ErrorCode<>("missing_encrypted_email", 400, "The encrypted_email query parameter is required.");
    public static final ErrorCode<UnsubscribeRestException> INVALID_ENCRYPTED_EMAIL =
        new ErrorCode<>("invalid_encrypted_email", 400, "The encrypted_email provided is not valid.",
            "encrypted_email");

    public static final ErrorCode<UnsubscribeRestException> MISSING_LIST_NAME =
        new ErrorCode<>("missing_list_name", 400, "The list_name query parameter is required.");

    public UnsubscribeRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
