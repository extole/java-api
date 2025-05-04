package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientValidationRestException extends ExtoleRestException {

    public static final ErrorCode<ClientValidationRestException> IDENTITY_KEY_INCOMPATIBLE_USAGE =
        new ErrorCode<>("identity_key_incompatible_usage", 400,
            "Non email Identity key cannot be used with reward every x friend actions reward rule",
            "incompatible_reward_rules_by_campaign_id");

    public static final ErrorCode<ClientValidationRestException> IDENTITY_KEY_INVALID_LENGTH =
        new ErrorCode<>("identity_key_invalid_length", 400, "Identity key has invalid length", "max_allowed_length",
            "min_allowed_length");

    public static final ErrorCode<ClientValidationRestException> IDENTITY_KEY_CONTAINS_RESTRICTED_CHARACTERS =
        new ErrorCode<>("identity_key_contains_restricted_characters", 400,
            "Identity key contains restricted characters");

    public static final ErrorCode<ClientValidationRestException> DUPLICATE_CLIENT =
        new ErrorCode<>("duplicate_client", 400, "Client already defined", "name");

    public static final ErrorCode<ClientValidationRestException> DUPLICATE_DOMAIN =
        new ErrorCode<>("duplicate_domain", 400, "Domain already defined", "domain_name");

    public static final ErrorCode<ClientValidationRestException> INVALID_NAME =
        new ErrorCode<>("invalid_name", 400, "The provided client name was invalid", "name");

    public static final ErrorCode<ClientValidationRestException> INVALID_SHORT_NAME = new ErrorCode<>(
        "invalid_short_name", 400, "The provided short name was invalid", "short_name");

    public static final ErrorCode<ClientValidationRestException> SHORT_NAME_RESERVED_KEYWORD_FOR_PROGRAM_DOMAIN =
        new ErrorCode<>("short_name_reserved_keyword_for_program_domain", 400,
            "Provided short name cannot be used, it contains a reserved keyword and program domain cannot be created",
            "short_name", "reserved_word");

    public static final ErrorCode<ClientValidationRestException> INVALID_POD = new ErrorCode<>(
        "invalid_pod", 400, "The provided pod was invalid", "pod", "available_pods");

    public static final ErrorCode<ClientValidationRestException> INVALID_CASE_DOMAIN_NAME = new ErrorCode<>(
        "invalid_case_domain_name", 400, "The provided domain name is not lowercase", "domain_name");

    public static final ErrorCode<ClientValidationRestException> INVALID_USER_EMAIL =
        new ErrorCode<>("invalid_user_email", 400, "The provided user email was invalid", "user_email");

    public static final ErrorCode<ClientValidationRestException> INVALID_USER_FIRST_NAME =
        new ErrorCode<>("invalid_user_first_name", 400, "The provided user first name was invalid", "first_name");

    public static final ErrorCode<ClientValidationRestException> INVALID_USER_LAST_NAME =
        new ErrorCode<>("invalid_user_last_name", 400, "The provided user last name was invalid", "last_name");

    public static final ErrorCode<ClientValidationRestException> INVALID_PASSWORD_LENGTH =
        new ErrorCode<>("invalid_password_length", 400, "Password is too short", "minimum_length", "maximum_length");

    public static final ErrorCode<ClientValidationRestException> PASSWORD_ALREADY_USED =
        new ErrorCode<>("password_already_used", 400, "Password had already been used, choose another one");

    public static final ErrorCode<ClientValidationRestException> INVALID_PASSWORD_CHANGE_LIMIT =
        new ErrorCode<>("invalid_password_change_limit", 400, "Password has been changed too many times. "
            + "Your account has been disabled. Please contact: support@extole.com");

    public static final ErrorCode<ClientValidationRestException> INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS =
        new ErrorCode<>("invalid_password_strength_letters_digits", 400, "Password is too weak, it must contain at "
            + "least one digit, one lowercase, and one uppercase letter");

    public static final ErrorCode<ClientValidationRestException> INVALID_PASSWORD_STRENGTH_LETTERS_DIGITS_PUNCTUATION =
        new ErrorCode<>("invalid_password_strength_letters_digits_punctuation", 400, "Password is too weak, it must "
            + "contain at least one digit, one lowercase letter, one uppercase letter, and one punctuation mark");

    public static final ErrorCode<ClientValidationRestException> COMMON_PASSWORD =
        new ErrorCode<>("common_password", 400, "Password is too weak");

    public static final ErrorCode<ClientValidationRestException> MISSING_VERIFICATION_CODE =
        new ErrorCode<>("missing_verification_code", 400, "Verification code is missing");

    public static final ErrorCode<ClientValidationRestException> INVALID_VERIFICATION_CODE =
        new ErrorCode<>("invalid_verification_code", 400, "Verification code is invalid", "verification_code");

    public static final ErrorCode<ClientValidationRestException> VERIFICATION_CODE_ALREADY_USED = new ErrorCode<>(
        "verification_code_already_used", 400, "Verification code was already used", "verification_code");

    public static final ErrorCode<ClientValidationRestException> ACCOUNT_DISABLED =
        new ErrorCode<>("account_disabled", 400, "Your account is disabled. Please contact: support@extole.com");

    public static final ErrorCode<ClientValidationRestException> PROPERTY_NAME_INVALID =
        new ErrorCode<>("invalid_property_name", 400, "Invalid property name", "name");

    public static final ErrorCode<ClientValidationRestException> PROPERTY_NAME_TOO_LONG =
        new ErrorCode<>("property_name_too_long", 400, "Property name must be between 1 and 255 characters", "name");

    public static final ErrorCode<ClientValidationRestException> PROPERTY_VALUE_TOO_LONG =
        new ErrorCode<>("property_value_too_long", 400, "Property value must be under 255 characters", "name", "value");

    public static final ErrorCode<ClientValidationRestException> PROPERTY_NULL_VALUE =
        new ErrorCode<>("property_null_value", 400, "Property value cannot be null", "name", "value");

    public static final ErrorCode<ClientValidationRestException> INVALID_SLACK_CHANNEL_NAME =
        new ErrorCode<>("invalid_slack_channel_name", 400, "Client slack channel name is invalid");

    public ClientValidationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
