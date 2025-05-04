package com.extole.client.rest.support;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SupportValidationRestException extends ExtoleRestException {

    public static final ErrorCode<SupportValidationRestException> FIELD_LENGTH_EXCEEDED =
        new ErrorCode<>("field_length_exceeded", 400, "Support field length exceeded", "name", "max_length");

    public static final ErrorCode<SupportValidationRestException> INVALID_SLACK_CHANNEL =
        new ErrorCode<>("invalid_slack_channel", 400, "Provided an invalid slack channel", "name");

    public static final ErrorCode<SupportValidationRestException> INVALID_SUPPORT_FIELD =
        new ErrorCode<>("invalid_support_field", 400, "Provided an invalid support field", "name");

    public SupportValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
