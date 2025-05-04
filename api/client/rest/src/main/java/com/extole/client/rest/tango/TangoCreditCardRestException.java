package com.extole.client.rest.tango;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoCreditCardRestException extends ExtoleRestException {

    public static final ErrorCode<TangoCreditCardRestException> INVALID_CREDIT_CARD_ID =
        new ErrorCode<>("invalid_credit_card_id", 400, "Provided credit card id is invalid");

    public TangoCreditCardRestException(String uniqueId, ErrorCode<?> errorCode,
        Map<String, Object> parameters, Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
