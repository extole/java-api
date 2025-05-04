package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;

public class RewardSupplierCreationRestException extends BuildRewardSupplierRestException {

    public static final ErrorCode<RewardSupplierCreationRestException> NAME_MISSING = new ErrorCode<>(
        "name_missing", 403, "Name must be a non-null value upon creation");

    public static final ErrorCode<RewardSupplierCreationRestException> MERCHANT_TOKEN_MISSING = new ErrorCode<>(
        "merchant_token_missing", 403, "Non blank Merchant token should be specified upon creation");

    public static final ErrorCode<RewardSupplierCreationRestException> FACE_VALUE_MISSING = new ErrorCode<>(
        "face_value_missing", 403, "Face value must be a non-null value upon creation");

    public static final ErrorCode<RewardSupplierCreationRestException> FACE_VALUE_TYPE_MISSING = new ErrorCode<>(
        "face_value_type_missing", 403, "Face value type must be a non-null value upon creation");

    public static final ErrorCode<RewardSupplierCreationRestException> MISSING_COUPON_COUNT_WARN_LIMIT =
        new ErrorCode<>("missing_coupon_count_warn_limit", 403,
            "Coupon count warn limit must be defined");

    public RewardSupplierCreationRestException(String uniqueId, ErrorCode<BuildRewardSupplierRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
