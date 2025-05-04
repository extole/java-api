package com.extole.client.rest.auth.provider.type;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AuthProviderTypeQueryRestException extends ExtoleRestException {

    public static final ErrorCode<AuthProviderTypeQueryRestException> AUTH_PROVIDER_TYPE_UNKNOWN_PROTOCOL =
        new ErrorCode<>(
            "auth_provider_type_unknown_protocol", 400, "Auth provider type protocol is unknown",
            "auth_provider_type_protocol");

    public AuthProviderTypeQueryRestException(String uniqueId, ErrorCode<AuthProviderTypeQueryRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
