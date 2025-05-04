package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthClientCredentialsAccessTokenRestException extends ExtoleRestException {

    public static final ErrorCode<OAuthClientCredentialsAccessTokenRestException> UNSUPPORTED_GRANT_TYPE =
        new ErrorCode<>("unsupported_grant_type", 400, "Unsupported grant_type", "grant_type");

    public OAuthClientCredentialsAccessTokenRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
