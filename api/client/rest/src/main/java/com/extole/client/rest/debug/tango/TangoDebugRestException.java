package com.extole.client.rest.debug.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoDebugRestException extends ExtoleRestException {

    public static final ErrorCode<TangoDebugRestException> INVALID_CUSTOMER_ID =
        new ErrorCode<>("invalid_customer_id", 400, "Inavlid customer id", "customer_id");

    public TangoDebugRestException(String uniqueId, ErrorCode<TangoDebugRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
