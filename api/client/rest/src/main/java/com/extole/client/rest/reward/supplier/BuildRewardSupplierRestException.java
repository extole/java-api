package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class BuildRewardSupplierRestException extends ExtoleRestException {

    public static final ErrorCode<BuildRewardSupplierRestException> COUPON_POOL_ID_MISSING =
        new ErrorCode<>("coupon_pool_id_missing", 400, "Coupon pool id is missing");

    public static final ErrorCode<BuildRewardSupplierRestException> FACE_VALUE_OUT_OF_RANGE =
        new ErrorCode<>("face_value_out_of_range", 400, "Face value is out of range",
            "face_value", "min_value", "max_value");

    public static final ErrorCode<BuildRewardSupplierRestException> ILLEGAL_CHARACTER_IN_NAME =
        new ErrorCode<>("illegal_character_in_name", 400, "Name can only contain alphanumeric characters", "name");

    public static final ErrorCode<BuildRewardSupplierRestException> ILLEGAL_CHARACTER_IN_DESCRIPTION =
        new ErrorCode<>("illegal_character_in_description", 400, "Description can only contain alphanumeric characters",
            "description");

    public static final ErrorCode<BuildRewardSupplierRestException> DUPLICATED_NAME = new ErrorCode<>(
        "reward_supplier_duplicated_name", 400, "Reward supplier with such name already exists", "name");

    public static final ErrorCode<BuildRewardSupplierRestException> NAME_TOO_LONG =
        new ErrorCode<>("name_too_long", 400, "Name should not exceed the specified length limit", "name",
            "max_name_length");

    public static final ErrorCode<BuildRewardSupplierRestException> DISPLAY_NAME_TOO_LONG =
        new ErrorCode<>("display_name_too_long", 400, "Display name should not exceed the specified length limit",
            "display_name", "max_length");

    public static final ErrorCode<BuildRewardSupplierRestException> DESCRIPTION_TOO_LONG =
        new ErrorCode<>("description_too_long", 400, "Description should not exceed the specified length limit",
            "description", "max_description_length");

    public static final ErrorCode<BuildRewardSupplierRestException> NEGATIVE_CASH_BACK_PERCENTAGE =
        new ErrorCode<>("negative_cash_back_percentage", 400, "Cash back percentage should be a non negative number",
            "cash_back_percentage");

    public static final ErrorCode<BuildRewardSupplierRestException> NEGATIVE_MIN_CASH_BACK =
        new ErrorCode<>("negative_min_cash_back", 400, "Min cash back limit should be a non negative number",
            "min_cash_back");

    public static final ErrorCode<BuildRewardSupplierRestException> NEGATIVE_MAX_CASH_BACK =
        new ErrorCode<>("negative_max_cash_back", 400, "Max cash back limit should be a non negative number",
            "max_cash_back");

    public static final ErrorCode<BuildRewardSupplierRestException> INVALID_CASH_BACK_LIMITS =
        new ErrorCode<>("invalid_cash_back_limits", 400,
            "Max cash back limit should greater or equal to min cash back limit",
            "min_cash_back", "max_cash_back");
    public static final ErrorCode<BuildRewardSupplierRestException> UNSUPPORTED_FACE_VALUE_ALGORITHM_TYPE =
        new ErrorCode<>(
            "unsupported_face_value_algorithm_type", 400,
            "Face value algorithm type is not supported for this reward supplier",
            "face_value_algorithm_type");

    public static final ErrorCode<BuildRewardSupplierRestException> INVALID_COUPON_COUNT_WARN_LIMIT =
        new ErrorCode<>("coupon_count_warn_limit_invalid", 400,
            "Coupon count warn limit must be a non-negative integer",
            "coupon_count_warn_limit");

    public static final ErrorCode<BuildRewardSupplierRestException> ILLEGAL_VALUE_OF_MINIMUM_COUPON_LIFETIME =
        new ErrorCode<>("illegal_value_of_minimum_coupon_lifetime", 400,
            "Minimum coupon lifetime should be greater than 0",
            "minimum_coupon_lifetime");

    public static final ErrorCode<BuildRewardSupplierRestException> LIMIT_OUT_OF_RANGE =
        new ErrorCode<>("limit_out_of_range", 400, "Limit is out of range",
            "limit", "min_value", "max_value");

    public static final ErrorCode<BuildRewardSupplierRestException> INVALID_LIMITS =
        new ErrorCode<>("invalid_limits", 400,
            "Limit per day should be greater or equal to limit per hour",
            "limit_per_day", "limit_per_hour");

    public static final ErrorCode<BuildRewardSupplierRestException> INVALID_TAG =
        new ErrorCode<>("invalid_tag", 400, "Invalid tag", "tag", "tag_max_length");

    public static final ErrorCode<BuildRewardSupplierRestException> REWARD_SUPPLIER_BUILD_FAILED =
        new ErrorCode<>("reward_supplier_build_failed", 400, "Reward supplier build failed",
            "reward_supplier_id", "evaluatable_name", "evaluatable");

    public static final ErrorCode<BuildRewardSupplierRestException> REWARD_SUPPLIER_VALIDATION_FAILED =
        new ErrorCode<>("reward_supplier_validation_failed", 400, "Reward supplier validation failed");

    public BuildRewardSupplierRestException(String uniqueId, ErrorCode<BuildRewardSupplierRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
