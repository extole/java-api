package com.extole.client.rest.security.key.oauth;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthClientKeyValidationRestException extends ExtoleRestException {

    public static final ErrorCode<OAuthClientKeyValidationRestException> OAUTH_CLIENT_ID_TOO_LONG =
        new ErrorCode<>("oauth_client_id_too_long", 400, "OAuth client id is too long", "oauth_client_id",
            "max_length");

    public static final ErrorCode<OAuthClientKeyValidationRestException> SCOPE_TOO_LONG =
        new ErrorCode<>("scope_too_long", 400, "Scope is too long", "scope", "max_length");

    public static final ErrorCode<OAuthClientKeyValidationRestException> MISSING_OAUTH_CLIENT_ID =
        new ErrorCode<>("missing_oauth_client_id", 400, "OAuth client id is not specified");

    public OAuthClientKeyValidationRestException(String uniqueId, ErrorCode<OAuthClientKeyValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
