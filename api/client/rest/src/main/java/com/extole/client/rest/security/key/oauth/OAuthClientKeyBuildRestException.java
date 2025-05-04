package com.extole.client.rest.security.key.oauth;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class OAuthClientKeyBuildRestException extends ExtoleRestException {

    public static final ErrorCode<OAuthClientKeyBuildRestException> AUTHORIZATION_URL_INVALID_URI =
        new ErrorCode<>("authorization_url_invalid_uri", 400, "Authorization URL is an invalid URI",
            "authorization_url");

    public static final ErrorCode<OAuthClientKeyBuildRestException> AUTHORIZATION_URL_TOO_LONG =
        new ErrorCode<>("authorization_url_too_long", 400, "Authorization URL is too long", "authorization_url",
            "max_length");

    public static final ErrorCode<OAuthClientKeyBuildRestException> AUTHORIZATION_URL_INVALID =
        new ErrorCode<>("authorization_url_invalid", 400, "Authorization URL must be a valid HTTPS URL",
            "authorization_url");

    public static final ErrorCode<OAuthClientKeyBuildRestException> MISSING_AUTHORIZATION_URL =
        new ErrorCode<>("missing_authorization_url", 400, "Authorization URL is not specified");

    public OAuthClientKeyBuildRestException(String uniqueId, ErrorCode<OAuthClientKeyBuildRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
