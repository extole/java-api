package com.extole.email.rest.audit;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailSendRestException extends ExtoleRestException {
    public static final ErrorCode<EmailSendRestException> EMAIL_RESPONSE_NOT_FOUND =
        new ErrorCode<>("email_response_not_found", 403, "Email response not found", "email_id");

    public EmailSendRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
