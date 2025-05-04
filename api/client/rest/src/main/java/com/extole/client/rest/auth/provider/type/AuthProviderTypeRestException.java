package com.extole.client.rest.auth.provider.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderTypeRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderTypeRestException> AUTH_PROVIDER_TYPE_NOT_FOUND = new ErrorCode<>(
        "auth_provider_type_not_found", 400, "Auth provider type is not found", "auth_provider_type_id");

    public AuthProviderTypeRestException(String uniqueId, ErrorCode<AuthProviderTypeRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
