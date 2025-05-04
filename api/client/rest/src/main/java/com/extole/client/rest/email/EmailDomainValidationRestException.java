package com.extole.client.rest.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailDomainValidationRestException extends ExtoleRestException {

    public static final ErrorCode<EmailDomainValidationRestException> INVALID_DOMAIN =
        new ErrorCode<>("invalid_domain", 400, "Invalid domain", "value", "message");

    public static final ErrorCode<EmailDomainValidationRestException> REQUIRED_DOMAIN_NAME =
        new ErrorCode<>("required_domain_name", 400, "Required domain name");

    public static final ErrorCode<EmailDomainValidationRestException> TOO_LONG_FIELD =
        new ErrorCode<>("too_long_email_domain_field", 400, "Email domain too long field", "max_length", "field_name");

    public static final ErrorCode<EmailDomainValidationRestException> DUPLICATE_EMAIL_DOMAIN =
        new ErrorCode<>("duplicate_email_domain", 400, "Duplicate email domain", "email_domain");

    public EmailDomainValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
