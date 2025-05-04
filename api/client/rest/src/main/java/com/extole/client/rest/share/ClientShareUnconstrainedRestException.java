package com.extole.client.rest.share;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClientShareUnconstrainedRestException extends ExtoleRestException {

    public static final ErrorCode<ClientShareUnconstrainedRestException> PARTNER_ID_MISSING =
        new ErrorCode<>("partner_id_missing", 400, "Please pass a partner id");

    public ClientShareUnconstrainedRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
