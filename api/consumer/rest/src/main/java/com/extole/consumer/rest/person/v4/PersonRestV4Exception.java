package com.extole.consumer.rest.person.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

@Deprecated // TODO remove ENG-10143
public class PersonRestV4Exception extends ExtoleRestException {
    public static final ErrorCode<PersonRestV4Exception> INVALID_PERSON_ID =
        new ErrorCode<>("invalid_person_id", 400, "Invalid person_id", "person_id");

    public static final ErrorCode<PersonRestV4Exception> INVALID_PROFILE_PICTURE_URL =
        new ErrorCode<>("invalid_profile_picture_url", 403, "Malformed profile_picture_url", "profile_picture_url");

    public static final ErrorCode<PersonRestV4Exception> INVALID_PERSON_EMAIL =
        new ErrorCode<>("invalid_person_email", 403, "Invalid person email", "email");

    public static final ErrorCode<PersonRestV4Exception> FIRST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_first_name", 403, "First Name length greater than 50 characters",
            "first_name");

    public static final ErrorCode<PersonRestV4Exception> LAST_NAME_INVALID_LENGTH =
        new ErrorCode<>("invalid_length_last_name", 403, "Last Name length greater than 50 characters", "last_name");

    public static final ErrorCode<PersonRestV4Exception> PERSON_EMAIL_ALREADY_DEFINED =
        new ErrorCode<>("person_email_already_defined", 403, "Person email already defined", "email");

    public static final ErrorCode<PersonRestV4Exception> PARTNER_USER_ID_ALREADY_DEFINED =
        new ErrorCode<>("partner_user_id_already_defined", 403, "Partner user id already defined", "partner_user_id");

    public static final ErrorCode<PersonRestV4Exception> PARTNER_USER_ID_INVALID_LENGTH = new ErrorCode<>(
        "partner_user_id_invalid_length", 403, "Partner user id length greater than 255 characters", "partner_user_id");

    public static final ErrorCode<PersonRestV4Exception> PERSON_NOT_FOUND =
        new ErrorCode<>("person_not_found", 403, "Person not found", "person_id", "client_id");

    public PersonRestV4Exception(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
