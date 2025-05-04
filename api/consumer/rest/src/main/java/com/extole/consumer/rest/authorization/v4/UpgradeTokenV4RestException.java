package com.extole.consumer.rest.authorization.v4;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class UpgradeTokenV4RestException extends ExtoleRestException {

    public static final ErrorCode<UpgradeTokenV4RestException> INVALID_SECRET =
        new ErrorCode<>("invalid_secret", 403, "The extole_secret provided is not valid for this access_token.");

    public UpgradeTokenV4RestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
