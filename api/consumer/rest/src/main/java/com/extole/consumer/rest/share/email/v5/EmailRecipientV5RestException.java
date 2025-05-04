package com.extole.consumer.rest.share.email.v5;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO kept for zazzle-client only - ENG-18976
public class EmailRecipientV5RestException extends ExtoleRestException {

    public static final ErrorCode<EmailRecipientV5RestException> INVALID_RECIPIENT = new ErrorCode<>(
        "INVALID_RECIPIENT", 400, "Email recipient has the wrong syntax.", "recipients");
    public static final ErrorCode<EmailRecipientV5RestException> NO_RECIPIENT = new ErrorCode<>(
        "NO_RECIPIENT", 400, "There is no recipient in the share request.");

    public EmailRecipientV5RestException(String uniqueId, ErrorCode<EmailRecipientV5RestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
