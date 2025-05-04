package com.extole.consumer.rest.share.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BatchEmailRecipientRestException extends ExtoleRestException {
    public static final ErrorCode<BatchEmailRecipientRestException> INVALID_RECIPIENT_SIZE = new ErrorCode<>(
        "INVALID_RECIPIENT_SIZE", 400, "Cannot share to more than 25 recipients at once.");

    public static final ErrorCode<BatchEmailRecipientRestException> RECIPIENT_JSON_CONVERSION_ERROR = new ErrorCode<>(
        "RECEIPIENT_JSON_CONVERSION_ERROR", 400, "Error occurred when converting recipient" +
            " list to JSON");

    public BatchEmailRecipientRestException(String uniqueId, ErrorCode<BatchEmailRecipientRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
