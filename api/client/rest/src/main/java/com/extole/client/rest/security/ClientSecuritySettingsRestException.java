package com.extole.client.rest.security;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientSecuritySettingsRestException extends ExtoleRestException {
    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_PASSWORD_LENGTH_MINIMUM =
        new ErrorCode<>("invalid_password_length_minimum", 403, "Invalid password length minimum",
            "password_length_minimum");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_PASSWORD_REUSE_LIMIT =
        new ErrorCode<>("invalid_password_reuse_limit", 403, "Invalid password reuse limit", "password_reuse_limit");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_PASSWORD_CHANGE_LIMIT = new ErrorCode<>(
        "invalid_password_change_limit", 403, "Invalid daily password change limit", "password_change_limit");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_PASSWORD_LIFETIME =
        new ErrorCode<>("invalid_password_lifetime", 403, "Invalid password lifetime", "password_lifetime");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_PASSWORD_STRENGTH =
        new ErrorCode<>("invalid_password_strength", 403,
            "Invalid password strength value, supported values are NONE,LETTERS_AND_DIGITS,LETTERS_DIGITS_PUNCTUATION",
            "password_strength");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_CLIENT_TOKEN_LIFETIME =
        new ErrorCode<>("invalid_client_token_lifetime", 403, "Invalid client token lifetime", "client_token_lifetime",
            "min_client_token_lifetime",
            "max_client_token_lifetime");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_CONSUMER_TOKEN_LIFETIME =
        new ErrorCode<>("invalid_consumer_token_lifetime", 403, "Invalid consumer token lifetime",
            "consumer_token_lifetime",
            "min_consumer_token_lifetime",
            "max_consumer_token_lifetime");

    public static final ErrorCode<ClientSecuritySettingsRestException> INVALID_FAILED_LOGINS_LIMIT =
        new ErrorCode<>("invalid_failed_logins_limit", 403, "Invalid failed logins limit", "failed_logins_limit");

    public ClientSecuritySettingsRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
