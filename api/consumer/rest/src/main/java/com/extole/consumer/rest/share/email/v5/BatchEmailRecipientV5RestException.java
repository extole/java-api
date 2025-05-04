package com.extole.consumer.rest.share.email.v5;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO kept for zazzle-client only - ENG-18976
public class BatchEmailRecipientV5RestException extends ExtoleRestException {
    public static final ErrorCode<BatchEmailRecipientV5RestException> INVALID_RECIPIENT_SIZE = new ErrorCode<>(
        "INVALID_RECIPIENT_SIZE", 400, "Cannot share to more than 100 recipients at once.");

    public static final ErrorCode<BatchEmailRecipientV5RestException> RECIPIENT_JSON_CONVERSION_ERROR = new ErrorCode<>(
        "RECEIPIENT_JSON_CONVERSION_ERROR", 400, "Error occurred when converting recipient" +
            " list to JSON");

    public BatchEmailRecipientV5RestException(String uniqueId, ErrorCode<BatchEmailRecipientV5RestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
