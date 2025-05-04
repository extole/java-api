package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ManagedAccessTokenRestException extends ExtoleRestException {

    public static final ErrorCode<ManagedAccessTokenRestException> INVALID_NAME =
        new ErrorCode<>("maximum_name_length", 400,
            "Name cannot be null, empty or blank and cannot be too long", "max_length");
    public static final ErrorCode<ManagedAccessTokenRestException> INVALID_DURATION =
        new ErrorCode<>("invalid_duration", 400,
            "The requested duration for this token must not be negative and be end within the first ten millenium",
            "duration_seconds");
    public static final ErrorCode<ManagedAccessTokenRestException> ACCOUNT_LOCKED =
        new ErrorCode<>("account_locked", 403, "The credentials provided with this request are invalid. "
            + "Account has been locked.");
    public static final ErrorCode<ManagedAccessTokenRestException> ACCOUNT_DISABLED =
        new ErrorCode<>("account_disabled", 403, "The credentials provided with this request are invalid. "
            + "Account has been disabled.");
    public static final ErrorCode<ManagedAccessTokenRestException> CREDENTIALS_MISSING =
        new ErrorCode<>("missing_credentials", 403, "No credentials provided with this request.");
    public static final ErrorCode<ManagedAccessTokenRestException> CREDENTIALS_INVALID =
        new ErrorCode<>("invalid_credentials", 403, "The credentials provided with this request are invalid.");
    public static final ErrorCode<ManagedAccessTokenRestException> CREDENTIALS_EXPIRED =
        new ErrorCode<>("expired_credentials", 403, "The credentials provided with this request are expired.");
    public static final ErrorCode<ManagedAccessTokenRestException> NO_SUCH_MANAGED_TOKEN =
        new ErrorCode<>("no_such_managed_token", 400,
            "The access_token provided is could not be found.");
    public static final ErrorCode<ManagedAccessTokenRestException> AUTHORIZATION_CODE_MISSING_CODE =
        new ErrorCode<>("authorization_code_missing_code", 403,
            "Authorization Code Flow required code parameter is missing");
    public static final ErrorCode<ManagedAccessTokenRestException> AUTHORIZATION_CODE_MISSING_STATE =
        new ErrorCode<>("authorization_code_missing_state", 403,
            "Authorization Code Flow required state parameter is missing");
    public static final ErrorCode<ManagedAccessTokenRestException> AUTHORIZATION_CODE_MISSING_CSRF =
        new ErrorCode<>("authorization_code_missing_csrf_token", 403,
            "Authorization Code Flow required csrf token header is missing");
    public static final ErrorCode<ManagedAccessTokenRestException> AUTHORIZATION_CODE_MISSING_NONCE =
        new ErrorCode<>("authorization_code_missing_nonce", 403,
            "Authorization Code Flow required nonce header is missing");
    public static final ErrorCode<ManagedAccessTokenRestException> AUTHORIZATION_CODE_RESPONSE_INVALID =
        new ErrorCode<>("authorization_code_response_invalid", 403,
            "Authorization Code Response is invalid or expired.");

    public ManagedAccessTokenRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
