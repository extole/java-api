package com.extole.client.rest.auth.provider.user.override;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderUserOverrideRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderUserOverrideRestException> AUTH_PROVIDER_USER_OVERRIDE_NOT_FOUND =
        new ErrorCode<>(
            "auth_provider_user_override_not_found", 400, "Auth provider user override is not found",
            "auth_provider_user_override_id");

    public AuthProviderUserOverrideRestException(String uniqueId,
        ErrorCode<AuthProviderUserOverrideRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
