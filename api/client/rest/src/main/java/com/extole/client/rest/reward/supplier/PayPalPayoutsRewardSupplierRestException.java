package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class PayPalPayoutsRewardSupplierRestException extends ExtoleRestException {

    public static final ErrorCode<PayPalPayoutsRewardSupplierRestException> ZERO_FACE_VALUE =
        new ErrorCode<>("zero_face_value", 400, "Face Value should be a positive non zero number");

    public static final ErrorCode<PayPalPayoutsRewardSupplierRestException> UNSUPPORTED_FACE_VALUE_TYPE =
        new ErrorCode<>("unsupported_face_value_type", 400, "Specified PayPal face value type is not supported",
            "face_value_type", "supported_face_value_types");

    public static final ErrorCode<PayPalPayoutsRewardSupplierRestException> BLANK_MERCHANT_TOKEN = new ErrorCode<>(
        "required_merchant_token", 403, "Merchant token required");

    public static final ErrorCode<PayPalPayoutsRewardSupplierRestException> UNSUPPORTED_DECIMAL_FACE_VALUE_TYPE =
        new ErrorCode<>("unsupported_decimal_face_value_type", 400,
            "Specified PayPal face value type does not support decimals",
            "face_value_type", "supported_decimal_face_value_types");

    public PayPalPayoutsRewardSupplierRestException(String uniqueId,
        ErrorCode<PayPalPayoutsRewardSupplierRestException> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
