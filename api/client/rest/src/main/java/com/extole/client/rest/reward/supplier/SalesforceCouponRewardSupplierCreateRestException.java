package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SalesforceCouponRewardSupplierCreateRestException extends ExtoleRestException {

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> INVALID_INITIAL_OFFSET =
        new ErrorCode<>("invalid_initial_offset", 400, "Initial offset must be a positive integer or zero",
            "initial_offset");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> COUPON_POOL_ID_NOT_FOUND =
        new ErrorCode<>("inexisting_coupon_pool_id", 400, "Coupon pool id not found in Salesforce", "coupon_pool_id");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> COUPON_POOL_ID_ALREADY_IN_USE =
        new ErrorCode<>("coupon_pool_id_already_in_use", 400,
            "Salesforce Commerce Cloud Coupon Set is already used", "coupon_pool_id");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> INVALID_COUPON_POOL_ID =
        new ErrorCode<>("invalid_coupon_pool_id", 400,
            "Salesforce coupon pool id must be a valid URL path segment", "coupon_pool_id");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> COUPON_POOL_ID_MISSING =
        new ErrorCode<>("coupon_pool_id_missing", 400, "Coupon pool id is missing");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> BALANCE_REFILL_AMOUNT_MISSING =
        new ErrorCode<>("balance_refill_amount_missing", 400, "Balance refill amount is missing");

    public static final ErrorCode<SalesforceCouponRewardSupplierCreateRestException> SETTINGS_ID_MISSING =
        new ErrorCode<>("settings_id_missing", 400, "Settings id missing");

    public SalesforceCouponRewardSupplierCreateRestException(String uniqueId,
        ErrorCode<SalesforceCouponRewardSupplierCreateRestException> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
