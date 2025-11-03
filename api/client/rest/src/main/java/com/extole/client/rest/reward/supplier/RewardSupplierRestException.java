package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardSupplierRestException extends ExtoleRestException {

    public static final ErrorCode<RewardSupplierRestException> REWARD_SUPPLIER_NOT_FOUND = new ErrorCode<>(
        "reward_supplier_not_found", 403, "Reward supplier not found", "reward_supplier_id");

    public static final ErrorCode<RewardSupplierRestException> INVALID_PARTNER_REWARD_SUPPLIER_ID =
        new ErrorCode<>("partner_reward_supplier_id_invalid", 400,
            "Partner reward supplier id is too long, max length is 255",
            "partner_reward_supplier_id");

    public static final ErrorCode<RewardSupplierRestException> INVALID_EXPIRATION_DATE =
        new ErrorCode<>("invalid_expiration_date", 400, "Expiration date is invalid or not ISO-8601 compliant",
            "line_number", "reward_supplier_id");

    public RewardSupplierRestException(String uniqueId, ErrorCode<RewardSupplierRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
