package com.extole.consumer.rest.me.email;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.model.RestError;

public class SendEmailError extends RestError {

    public static final ErrorCode<SendEmailError> EMAIL_SEND_FAILED_RETRYING =
        new ErrorCode<>("email_send_failed_retrying", 403, "Email send failed. Will retry.", "polling_id");

    public static final ErrorCode<SendEmailError> EMAIL_SEND_FAILED =
        new ErrorCode<>("email_send_failed", 403, "Email send failed", "polling_id");

    public static final ErrorCode<SendEmailError> EMAIL_TARGETING_FAILED =
        new ErrorCode<>("email_targeting_failed", 403, "Email targeting failed", "polling_id");

    public static final ErrorCode<SendEmailError> SOFTWARE_ERROR =
        new ErrorCode<>("software_error", 500, "Software error", "polling_id");

    @JsonCreator
    protected SendEmailError(
        @JsonProperty("unique_id") String uniqueId,
        @JsonProperty("http_status_code") int httpStatusCode,
        @JsonProperty("code") String code,
        @JsonProperty("message") String message,
        @JsonProperty("parameters") Map<String, ? extends Object> parameters) {
        super(uniqueId, httpStatusCode, code, message, parameters);
    }

    public SendEmailError(RestError restError) {
        super(restError);
    }

}
