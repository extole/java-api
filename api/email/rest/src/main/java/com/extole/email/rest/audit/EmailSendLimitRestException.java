package com.extole.email.rest.audit;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailSendLimitRestException extends ExtoleRestException {
    public static final ErrorCode<EmailSendLimitRestException> EMAIL_LIMIT_INVALID =
        new ErrorCode<>("email_limit_invalid", 403, "Email limit not valid", "limit");

    public EmailSendLimitRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
