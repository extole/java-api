package com.extole.client.rest.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailDomainRestException extends ExtoleRestException {

    public static final ErrorCode<EmailDomainRestException> EMAIL_DOMAIN_NOT_FOUND =
        new ErrorCode<>("email_domain_not_found", 404, "Email domain not found", "email_domain_id");

    public EmailDomainRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
