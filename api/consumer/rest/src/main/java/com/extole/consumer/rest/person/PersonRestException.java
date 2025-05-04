package com.extole.consumer.rest.person;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PersonRestException extends ExtoleRestException {
    public static final ErrorCode<PersonRestException> INVALID_PERSON_ID =
        new ErrorCode<>("invalid_person_id", 400, "Invalid person_id", "person_id");

    public static final ErrorCode<PersonRestException> INVALID_PROFILE_PICTURE_URL =
        new ErrorCode<>("invalid_profile_picture_url", 400, "Malformed profile_picture_url", "profile_picture_url");

    public static final ErrorCode<PersonRestException> INVALID_PERSON_EMAIL =
        new ErrorCode<>("invalid_person_email", 400, "Invalid person email", "email");

    public static final ErrorCode<PersonRestException> FIRST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_first_name", 400, "First Name length greater than 50 characters",
            "first_name");

    public static final ErrorCode<PersonRestException> LAST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_last_name", 400, "Last Name length greater than 50 characters", "last_name");

    public static final ErrorCode<PersonRestException> PERSON_EMAIL_ALREADY_DEFINED =
        new ErrorCode<>("person_email_already_defined", 400, "Person email already defined", "email");

    public static final ErrorCode<PersonRestException> PARTNER_USER_ID_ALREADY_DEFINED =
        new ErrorCode<>("partner_user_id_already_defined", 400, "Partner user id already defined", "partner_user_id");

    public static final ErrorCode<PersonRestException> PARTNER_USER_ID_INVALID_LENGTH = new ErrorCode<>(
        "partner_user_id_invalid_length", 400, "Partner user id length greater than 255 characters", "partner_user_id");

    public static final ErrorCode<PersonRestException> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 400, "Person not found", "person_id", "client_id");

    public PersonRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
