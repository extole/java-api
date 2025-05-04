package com.extole.consumer.rest.share.event;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove ENG-10140
public class EventRestException extends ExtoleRestException {

    public static final ErrorCode<EventRestException> RECIPIENTS_MISSING = new ErrorCode<>(
        "RECIPIENTS_MISSING", 400, "Share recipient is missing.", "recipient");
    public static final ErrorCode<EventRestException> INVALID_RECIPIENTS = new ErrorCode<>(
        "INVALID_RECIPIENTS", 400, "Recipient has a wrong syntax.", "recipient");
    public static final ErrorCode<EventRestException> SHAREABLE_ID_MISSING = new ErrorCode<>(
        "SHAREABLE_ID_MISSING", 400, "ShareableId is missing in the share request.");
    public static final ErrorCode<EventRestException> INVALID_SHAREABLE_ID = new ErrorCode<>(
        "INVALID_SHAREABLE_ID", 400, "ShareableId not valid in the share request.", "shareable_id");
    public static final ErrorCode<EventRestException> SHAREABLE_NOT_FOUND = new ErrorCode<>(
        "SHAREABLE_NOT_FOUND", 403, "Shareable Not Found.", "shareable_id");
    public static final ErrorCode<EventRestException> MESSAGE_MISSING = new ErrorCode<>(
        "MESSAGE_MISSING", 403, "Share message is missing");
    public static final ErrorCode<EventRestException> INVALID_MESSAGE_LENGTH = new ErrorCode<>(
        "INVALID_MESSAGE_LENGTH", 403, "Share message cannot exceed 2048 characters");
    public static final ErrorCode<EventRestException> CHANNEL_MISSING = new ErrorCode<>(
        "CHANNEL_MISSING", 400, "Channel is missing, use one of EMAIL, FACEBOOK, TWITTER, SHARE_LINK.");
    public static final ErrorCode<EventRestException> INVALID_RECIPIENT_SIZE = new ErrorCode<>(
        "INVALID_RECIPIENT_SIZE", 403, "Cannot share to more than 25 recipients at once.");
    public static final ErrorCode<EventRestException> INVALID_SHARE_MESSAGE_LINK = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_LINK", 400, "Sorry, we can not put that link in a share message.", "link");
    public static final ErrorCode<EventRestException> INVALID_SHARE_MESSAGE_CHARACTERS = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_CHARACTERS", 400, "Sorry, we can not put these characters in a share message.",
        "forbidden_characters", "forbidden_characters_as_unicode", "share_message");

    public EventRestException(String uniqueId, ErrorCode<EventRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
