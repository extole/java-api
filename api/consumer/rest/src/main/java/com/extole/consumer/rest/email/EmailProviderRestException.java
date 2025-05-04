package com.extole.consumer.rest.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailProviderRestException extends ExtoleRestException {
    public static final ErrorCode<EmailProviderRestException> INVALID_EMAIL =
        new ErrorCode<>("invalid_email", 403, "Invalid email", "email");
    public static final ErrorCode<EmailProviderRestException> INVALID_DOMAIN =
        new ErrorCode<>("invalid_domain", 403, "Invalid domain", "domain");

    public EmailProviderRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
