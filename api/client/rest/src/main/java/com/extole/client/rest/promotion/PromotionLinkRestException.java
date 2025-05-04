package com.extole.client.rest.promotion;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PromotionLinkRestException extends ExtoleRestException {
    public static final ErrorCode<PromotionLinkRestException> PROMOTION_LINK_NOT_FOUND =
        new ErrorCode<>("promotion_link_not_found", 403, "Promotion link not found", "code");

    public PromotionLinkRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
