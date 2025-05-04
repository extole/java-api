package com.extole.client.rest.auth.provider.type.extole;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ExtoleAuthProviderTypeRestException extends ExtoleRestException {

    public static final ErrorCode<ExtoleAuthProviderTypeRestException> EXTOLE_AUTH_PROVIDER_TYPE_NOT_FOUND =
        new ErrorCode<>(
            "extole_auth_provider_type_not_found", 400, "Extole Auth provider type is not found",
            "extole_auth_provider_type_id");

    public ExtoleAuthProviderTypeRestException(String uniqueId,
        ErrorCode<ExtoleAuthProviderTypeRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
