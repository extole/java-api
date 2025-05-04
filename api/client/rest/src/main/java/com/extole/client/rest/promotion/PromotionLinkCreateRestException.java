package com.extole.client.rest.promotion;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PromotionLinkCreateRestException extends ExtoleRestException {

    public static final ErrorCode<PromotionLinkCreateRestException> CODE_MISSING =
        new ErrorCode<>("code_missing", 403, "Missing code", "code");

    public PromotionLinkCreateRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
