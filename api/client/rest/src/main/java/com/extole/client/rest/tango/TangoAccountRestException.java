package com.extole.client.rest.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoAccountRestException extends ExtoleRestException {

    public static final ErrorCode<TangoAccountRestException> INVALID_ACCOUNT_ID =
        new ErrorCode<>("invalid_account_id", 400, "TODO");

    public TangoAccountRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
