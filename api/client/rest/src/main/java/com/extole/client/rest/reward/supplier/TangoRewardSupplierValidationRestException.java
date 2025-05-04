package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class TangoRewardSupplierValidationRestException extends ExtoleRestException {

    public static final ErrorCode<TangoRewardSupplierValidationRestException> CASH_BACK_LIMITS_OUT_OF_BOUNDS =
        new ErrorCode<>(
            "cash_back_limits_out_of_bounds", 400, "Cash back min/max limits should be within brand limits",
            "utid", "min_cash_back", "max_cash_back", "min_brand_item_value", "max_brand_item_value");

    public static final ErrorCode<TangoRewardSupplierValidationRestException> FACE_VALUE_OUT_OF_BOUNDS =
        new ErrorCode<>(
            "face_value", 400, "Face value should be within brand limits",
            "utid", "face_value", "min_brand_item_value", "max_brand_item_value");

    public static final ErrorCode<TangoRewardSupplierValidationRestException> UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE =
        new ErrorCode<>(
            "unsupported_face_value_algorithm_type", 400,
            "Face value algorithm type is not supported by this brand item",
            "utid", "face_value_algorithm_type");

    public TangoRewardSupplierValidationRestException(String uniqueId,
        ErrorCode<RewardSupplierCreationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
