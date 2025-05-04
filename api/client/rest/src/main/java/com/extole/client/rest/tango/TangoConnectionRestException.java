package com.extole.client.rest.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoConnectionRestException extends ExtoleRestException {

    public static final ErrorCode<TangoConnectionRestException> TANGO_SERVICE_UNAVAILABLE =
        new ErrorCode<>("tango_service_unavailable", 503, "Tango service unavailable");

    public TangoConnectionRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
