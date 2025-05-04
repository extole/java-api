package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AccessTokenAuthCodeResponseValidateRestException extends ExtoleRestException {

    public static final ErrorCode<AccessTokenAuthCodeResponseValidateRestException> AUTHORIZATION_CODE_MISSING_CODE =
        new ErrorCode<>("authorization_code_missing_code", 403,
            "Authorization Code Flow required code parameter is missing");

    public static final ErrorCode<AccessTokenAuthCodeResponseValidateRestException> AUTHORIZATION_CODE_MISSING_STATE =
        new ErrorCode<>("authorization_code_missing_state", 403,
            "Authorization Code Flow required state parameter is missing");

    public static final ErrorCode<AccessTokenAuthCodeResponseValidateRestException> AUTHORIZATION_CODE_MISSING_CSRF =
        new ErrorCode<>("authorization_code_missing_csrf_token", 403,
            "Authorization Code Flow required csrf token header is missing");

    public static final ErrorCode<AccessTokenAuthCodeResponseValidateRestException> AUTHORIZATION_CODE_MISSING_NONCE =
        new ErrorCode<>("authorization_code_missing_nonce", 403,
            "Authorization Code Flow required nonce header is missing");

    public static final ErrorCode<
        AccessTokenAuthCodeResponseValidateRestException> AUTHORIZATION_CODE_RESPONSE_INVALID =
            new ErrorCode<>("authorization_code_response_invalid", 403,
                "Authorization Code Response is invalid or expired.", "error_code");

    public AccessTokenAuthCodeResponseValidateRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
