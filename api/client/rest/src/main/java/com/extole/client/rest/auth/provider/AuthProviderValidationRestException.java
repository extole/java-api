package com.extole.client.rest.auth.provider;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderValidationRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderValidationRestException> AUTH_PROVIDER_MISSING_NAME =
        new ErrorCode<>(
            "auth_provider_missing_name", 400, "Auth Provider required name is not specified");

    public static final ErrorCode<AuthProviderValidationRestException> AUTH_PROVIDER_MISSING_TYPE =
        new ErrorCode<>(
            "auth_provider_missing_auth_provider_type_id", 400,
            "Auth Provider required auth provider type Id is not specified");

    public static final ErrorCode<AuthProviderValidationRestException> AUTH_PROVIDER_INVALID_NAME =
        new ErrorCode<>(
            "auth_provider_invalid_name", 400, "Allowed name length is 255 containing ASCII characters", "name");

    public static final ErrorCode<AuthProviderValidationRestException> AUTH_PROVIDER_INVALID_DESCRIPTION =
        new ErrorCode<>(
            "auth_provider_invalid_description", 400,
            "Allowed description length is 1024 containing ASCII characters",
            "description");

    public AuthProviderValidationRestException(String uniqueId,
        ErrorCode<AuthProviderValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
