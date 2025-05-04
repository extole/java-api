package com.extole.consumer.rest.share;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ShareUnconstrainedRestException extends ExtoleRestException {

    public static final ErrorCode<ShareUnconstrainedRestException> PARTNER_ID_MISSING =
        new ErrorCode<>("partner_id_missing", 400, "Please pass a partner id");

    public ShareUnconstrainedRestException(String uniqueId, ErrorCode<ShareUnconstrainedRestException> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
