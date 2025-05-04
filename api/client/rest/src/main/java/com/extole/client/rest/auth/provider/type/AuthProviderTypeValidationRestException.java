package com.extole.client.rest.auth.provider.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderTypeValidationRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_NAME =
        new ErrorCode<>(
            "auth_provider_type_missing_name", 400, "Auth Provider type required name is not specified");

    public static final ErrorCode<AuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_MISSING_SCOPES =
        new ErrorCode<>(
            "auth_provider_type_missing_scopes", 400,
            "Auth Provider type no scopes are specified");

    public static final ErrorCode<AuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_NAME =
        new ErrorCode<>(
            "auth_provider_type_invalid_name", 400, "Allowed name length is 255 containing ASCII characters", "name");

    public static final ErrorCode<AuthProviderTypeValidationRestException> AUTH_PROVIDER_TYPE_INVALID_DESCRIPTION =
        new ErrorCode<>(
            "auth_provider_type_invalid_description", 400,
            "Allowed description length is 1024 containing ASCII characters",
            "description");

    public AuthProviderTypeValidationRestException(String uniqueId,
        ErrorCode<AuthProviderTypeValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
