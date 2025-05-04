package com.extole.client.rest.person.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonValidationV2RestException extends ExtoleRestException {
    public static final ErrorCode<PersonValidationV2RestException> PICTURE_URL_INVALID =
        new ErrorCode<>("invalid_picture_url", 400, "Invalid picture_url", "picture_url");

    public static final ErrorCode<PersonValidationV2RestException> EMAIL_INVALID =
        new ErrorCode<>("invalid_email", 400, "Invalid email", "email");

    public static final ErrorCode<PersonValidationV2RestException> PARTNER_USER_ID_INVALID_LENGTH =
        new ErrorCode<>("partner_user_id_invalid_length", 400, "Partner user id length greater than 255 characters",
            "partner_user_id");

    public static final ErrorCode<PersonValidationV2RestException> FIRST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_first_name", 400, "First Name length greater than 50 characters",
            "first_name");

    public static final ErrorCode<PersonValidationV2RestException> LAST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_last_name", 400, "Last Name length greater than 50 characters", "last_name");

    public static final ErrorCode<PersonValidationV2RestException> PERSON_PARTNER_USER_ID_ALREADY_DEFINED =
        new ErrorCode<>("person_partner_user_id_already_defined", 400, "Person partner user id already defined",
            "partner_user_id");

    public static final ErrorCode<PersonValidationV2RestException> PERSON_EMAIL_ALREADY_DEFINED =
        new ErrorCode<>("person_email_already_defined", 400, "Person email already defined", "email");

    public static final ErrorCode<PersonValidationV2RestException> INVALID_BLOCK_REASON =
        new ErrorCode<>("invalid_block_reason", 400, "Block reason can't be empty");

    public PersonValidationV2RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
