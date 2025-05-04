package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SalesforceCouponRewardSupplierValidationRestException extends ExtoleRestException {

    public static final ErrorCode<SalesforceCouponRewardSupplierValidationRestException> INVALID_BALANCE_REFILL_AMOUNT =
        new ErrorCode<>("invalid_balance_refill_amount", 400,
            "Balance refill amount must be a positive integer, greater than zero", "balance_refill_amount");

    public SalesforceCouponRewardSupplierValidationRestException(String uniqueId,
        ErrorCode<SalesforceCouponRewardSupplierValidationRestException> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
