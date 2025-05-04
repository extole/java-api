package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardSupplierArchiveRestException extends ExtoleRestException {

    public static final ErrorCode<RewardSupplierArchiveRestException> REWARD_SUPPLIER_IS_REFERENCED =
        new ErrorCode<>("reward_supplier_is_referenced", 403, "Reward supplier is referenced by entities",
            "references", "reward_supplier_id");

    public RewardSupplierArchiveRestException(String uniqueId, ErrorCode<RewardSupplierRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
