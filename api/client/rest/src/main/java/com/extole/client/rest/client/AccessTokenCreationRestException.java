package com.extole.client.rest.client;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class AccessTokenCreationRestException extends ExtoleRestException {

    public static final ErrorCode<AccessTokenCreationRestException> ACCESS_DENIED =
        new ErrorCode<>("access_denied", 403, "Access Denied");
    public static final ErrorCode<AccessTokenCreationRestException> CREDENTIALS_INVALID =
        new ErrorCode<>("invalid_credentials", 403, "The credentials provided with this request are invalid.");
    public static final ErrorCode<AccessTokenCreationRestException> CREDENTIALS_EXPIRED =
        new ErrorCode<>("expired_credentials", 403, "The credentials provided with this request are expired.");
    public static final ErrorCode<AccessTokenCreationRestException> CREDENTIALS_MISSING =
        new ErrorCode<>("missing_credentials", 403, "No credentials provided with this request.");
    public static final ErrorCode<AccessTokenCreationRestException> ACCOUNT_LOCKED =
        new ErrorCode<>("account_locked", 403, "The credentials provided with this request are invalid. "
            + "Account has been locked.");
    public static final ErrorCode<AccessTokenCreationRestException> ACCOUNT_DISABLED =
        new ErrorCode<>("account_disabled", 403, "The credentials provided with this request are invalid. "
            + "Account has been disabled.");
    public static final ErrorCode<AccessTokenCreationRestException> SCOPES_DENIED =
        new ErrorCode<>("scopes_denied", 403, "Requested scopes is not a subset of current scopes.",
            "denied_scopes");
    public static final ErrorCode<AccessTokenCreationRestException> INVALID_DURATION =
        new ErrorCode<>("invalid_duration", 400,
            "The requested duration for this token must end within the first ten millenium", "default_duration",
            "duration_seconds");

    public AccessTokenCreationRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
