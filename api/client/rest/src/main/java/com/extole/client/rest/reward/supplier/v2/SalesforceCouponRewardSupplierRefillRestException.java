package com.extole.client.rest.reward.supplier.v2;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class SalesforceCouponRewardSupplierRefillRestException extends ExtoleRestException {

    public static final ErrorCode<SalesforceCouponRewardSupplierRefillRestException> REFILL_ALREADY_IN_PROGRESS =
        new ErrorCode<>("refill_already_in_progress", 400, "Refill already in progress", "client_id",
            "reward_supplier_id");

    public SalesforceCouponRewardSupplierRefillRestException(String uniqueId,
        ErrorCode<SalesforceCouponRewardSupplierRefillRestException> code,
        Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
