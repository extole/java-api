package com.extole.client.rest.security.key.jwt;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class JwtClientKeyJwtVerifyRestException extends ExtoleRestException {

    public static final ErrorCode<JwtClientKeyJwtVerifyRestException> CLIENT_KEY_VERIFY_MISSING_JWT = new ErrorCode<>(
        "jwt_missing", 400, "No JWT specified for verification");

    public JwtClientKeyJwtVerifyRestException(String uniqueId, ErrorCode<JwtClientKeyJwtVerifyRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
