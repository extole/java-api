package com.extole.client.rest.auth.provider;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderRestException> AUTH_PROVIDER_NOT_FOUND =
        new ErrorCode<>(
            "auth_provider_not_found", 400, "Auth provider is not found",
            "auth_provider_id");

    public AuthProviderRestException(String uniqueId,
        ErrorCode<AuthProviderRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
