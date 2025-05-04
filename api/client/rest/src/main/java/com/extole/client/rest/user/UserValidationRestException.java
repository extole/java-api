package com.extole.client.rest.user;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UserValidationRestException extends ExtoleRestException {
    public static final ErrorCode<UserValidationRestException> INVALID_USER_EMAIL =
        new ErrorCode<>("invalid_user_email", 400, "The provided user email was invalid", "email");

    public static final ErrorCode<UserValidationRestException> INVALID_USER_FIRST_NAME =
        new ErrorCode<>("invalid_user_first_name", 400, "The provided user first name was invalid", "first_name");

    public static final ErrorCode<UserValidationRestException> INVALID_USER_LAST_NAME =
        new ErrorCode<>("invalid_user_last_name", 400, "The provided user last name was invalid", "last_name");

    public static final ErrorCode<UserValidationRestException> INVALID_PASSWORD_LENGTH =
        new ErrorCode<>("invalid_password_length", 400, "Password is too short", "minimum_length", "maximum_length");

    public static final ErrorCode<UserValidationRestException> PASSWORD_ALREADY_USED =
        new ErrorCode<>("password_already_used", 400, "Password had already been used, choose another one");

    public static final ErrorCode<UserValidationRestException> PASSWORD_CHANGE_LIMIT =
        new ErrorCode<>("invalid_password_change_limit", 400, "Password has been changed too many times. "
            + "Your account has been disabled. Please contact: support@extole.com");

    public static final ErrorCode<UserValidationRestException> INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS =
        new ErrorCode<>("invalid_password_strength_letters_digits", 400, "Password is too weak, it must contain at "
            + "least one digit, one lowercase, and one uppercase letter");

    public static final ErrorCode<UserValidationRestException> INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION =
        new ErrorCode<>("invalid_password_strength_letters_digits_punctuation", 400, "Password is too weak, it must "
            + "contain at least one digit, one lowercase letter, one uppercase letter, and one punctuation mark");

    public static final ErrorCode<UserValidationRestException> COMMON_PASSWORD =
        new ErrorCode<>("common_password", 400, "Password is too weak");

    public static final ErrorCode<UserValidationRestException> UNAUTHORIZED_SCOPE =
        new ErrorCode<>("unauthorized_scope", 400, "User is not authorized to have the specified role", "role");

    public static final ErrorCode<UserValidationRestException> USER_SCOPE_SELF_REMOVAL =
        new ErrorCode<>("suer_scope_self_removal", 400, "User cannot remove role from itself", "role");

    public UserValidationRestException(
        String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
