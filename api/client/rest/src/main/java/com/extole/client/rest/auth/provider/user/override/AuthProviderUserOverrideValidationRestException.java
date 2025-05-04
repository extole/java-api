package com.extole.client.rest.auth.provider.user.override;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderUserOverrideValidationRestException extends ExtoleRestException {

    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_MISSING_USER =
            new ErrorCode<>(
                "auth_provider_user_override_missing_user_id", 400,
                "Auth Provider user override required user id is not specified");

    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_MISSING_AUTH_PROVIDER_ENABLED =
            new ErrorCode<>(
                "auth_provider_user_override_missing_auth_provider_enabled_for_user", 400,
                "Auth Provider user override required auth provider enabled for user is not specified");

    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_INVALID_NAME =
            new ErrorCode<>(
                "auth_provider_user_override_invalid_name", 400,
                "Allowed name length is 255 containing ASCII characters",
                "name");
    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_ALREADY_DEFINED =
            new ErrorCode<>(
                "auth_provider_user_override_already_defined", 400,
                "Specified user already has defined override for this auth provider",
                "user_id");
    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_USER_NOT_FOUND =
            new ErrorCode<>(
                "auth_provider_user_override_user_not_found", 400, "User with such user_id is not found",
                "user_id");

    public static final ErrorCode<
        AuthProviderUserOverrideValidationRestException> AUTH_PROVIDER_USER_OVERRIDE_INVALID_DESCRIPTION =
            new ErrorCode<>(
                "auth_provider_user_override_invalid_description", 400,
                "Allowed description length is 1024 containing ASCII characters",
                "description");

    public AuthProviderUserOverrideValidationRestException(String uniqueId,
        ErrorCode<AuthProviderUserOverrideValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
