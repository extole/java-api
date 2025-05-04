package com.extole.consumer.rest.share.email;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class EmailShareRestException extends ExtoleRestException {

    public static final ErrorCode<EmailShareRestException> INVALID_RECIPIENT = new ErrorCode<>(
        "INVALID_RECIPIENT", 400, "Email recipient has the wrong syntax.", "recipients");
    public static final ErrorCode<EmailShareRestException> NO_RECIPIENT = new ErrorCode<>(
        "NO_RECIPIENT", 400, "There is no recipient in the share request.");

    public static final ErrorCode<EmailShareRestException> MESSAGE_MISSING = new ErrorCode<>(
        "MESSAGE_MISSING", 400, "Email message is missing.");
    public static final ErrorCode<EmailShareRestException> INVALID_MESSAGE_LENGTH = new ErrorCode<>(
        "INVALID_MESSAGE_LENGTH", 400, "Email message cannot exceed 2048 characters.", "email_message");
    public static final ErrorCode<EmailShareRestException> INVALID_SUBJECT_LENGTH = new ErrorCode<>(
        "INVALID_SUBJECT_LENGTH", 400, "Email subject cannot exceed 78 characters.", "subject");
    public static final ErrorCode<EmailShareRestException> INVALID_SHARE_MESSAGE_LINK = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_LINK", 400, "Sorry, we can not put that link in a share message.", "link",
        "share_message");
    public static final ErrorCode<EmailShareRestException> INVALID_SHARE_SUBJECT_LINK = new ErrorCode<>(
        "INVALID_SHARE_SUBJECT_LINK", 400, "Sorry, we can not put that link in a share subject.", "link", "subject");
    public static final ErrorCode<EmailShareRestException> INVALID_SHARE_MESSAGE_CHARACTERS = new ErrorCode<>(
        "INVALID_SHARE_MESSAGE_CHARACTERS", 400, "Sorry, we can not put these characters in a share message.",
        "forbidden_characters", "forbidden_characters_as_unicode", "share_message");
    public static final ErrorCode<EmailShareRestException> INVALID_SHARE_SUBJECT_CHARACTERS = new ErrorCode<>(
        "INVALID_SHARE_SUBJECT_CHARACTERS", 400, "Sorry, we can not put these characters in a share subject.",
        "forbidden_characters", "forbidden_characters_as_unicode", "subject");

    public EmailShareRestException(String uniqueId, ErrorCode<EmailShareRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
