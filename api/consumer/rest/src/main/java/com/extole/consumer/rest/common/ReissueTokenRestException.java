package com.extole.consumer.rest.common;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ReissueTokenRestException extends ExtoleRestException {

    public static final ErrorCode<ReissueTokenRestException> DURATION_DENIED =
        new ErrorCode<>("duration_denied", 403,
            "Duration specified is greater than maximum.", "maximum", "duration");
    public static final ErrorCode<ReissueTokenRestException> SCOPES_DENIED =
        new ErrorCode<>("scopes_denied", 403,
            "Requested scopes is not a subset of current scopes.", "denied_scopes");

    public static final ErrorCode<ReissueTokenRestException> JWT_AUTHENTICATION_FAILED =
        new ErrorCode<>("jwt_error", 403, "The jwt authentication failed.", "reason", "description");

    public ReissueTokenRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
