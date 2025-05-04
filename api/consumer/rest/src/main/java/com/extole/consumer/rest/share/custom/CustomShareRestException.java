package com.extole.consumer.rest.share.custom;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CustomShareRestException extends ExtoleRestException {

    public static final ErrorCode<CustomShareRestException> MISSING_RECIPIENT = new ErrorCode<>(
        "MISSING_RECIPIENT", 400, "Recipient is missing.");
    public static final ErrorCode<CustomShareRestException> INVALID_RECIPIENT = new ErrorCode<>(
        "INVALID_RECIPIENT", 400, "Recipient has a wrong syntax.", "recipient");
    public static final ErrorCode<CustomShareRestException> MISSING_MESSAGE = new ErrorCode<>(
        "MISSING_MESSAGE", 400, "Share message is missing.");
    public static final ErrorCode<CustomShareRestException> INVALID_MESSAGE_LENGTH = new ErrorCode<>(
        "INVALID_MESSAGE_LENGTH", 400, "Share message cannot exceed 2048 characters");
    public static final ErrorCode<CustomShareRestException> INVALID_SHARE_MESSAGE_LINK = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_LINK", 400, "Sorry, we can not put that link in a share message.", "link");
    public static final ErrorCode<CustomShareRestException> CHANNEL_MISSING = new ErrorCode<>(
        "CHANNEL_MISSING", 400, "Channel is missing");
    public static final ErrorCode<CustomShareRestException> INVALID_SHARE_MESSAGE_CHARACTERS = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_CHARACTERS", 400, "Sorry, we can not put these characters in a share message.",
        "forbidden_characters", "forbidden_characters_as_unicode", "share_message");

    public CustomShareRestException(String uniqueId, ErrorCode<CustomShareRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
