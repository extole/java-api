package com.extole.client.rest.reward.supplier;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CustomRewardSupplierRestException extends ExtoleRestException {

    public static final ErrorCode<CustomRewardSupplierRestException> CUSTOM_REWARD_TYPE_MISSING =
        new ErrorCode<>("custom_reward_type_missing", 400, "Custom reward type id missing");

    public static final ErrorCode<CustomRewardSupplierRestException> NEGATIVE_ALERT_DELAY =
        new ErrorCode<>("negative_missing_fulfillment_alert_delay_ms", 400,
            "Missing fulfillment alert delay should be a positive number",
            "missing_fulfillment_alert_delay_ms");

    public static final ErrorCode<CustomRewardSupplierRestException> NEGATIVE_AUTO_FAIL_DELAY =
        new ErrorCode<>("negative_missing_fulfillment_auto_fail_delay_ms", 400,
            "Missing fulfillment auto fail delay should be a positive number",
            "missing_fulfillment_auto_fail_delay_ms");

    public static final ErrorCode<CustomRewardSupplierRestException> INVALID_AUTO_FAIL_DELAY =
        new ErrorCode<>("invalid_missing_fulfillment_auto_fail_delay_ms", 400,
            "Missing fulfillment auto fail delay should not be lower than alert delay",
            "missing_fulfillment_alert_delay_ms", "missing_fulfillment_auto_fail_delay_ms");

    public CustomRewardSupplierRestException(String uniqueId,
        ErrorCode<CustomRewardSupplierRestException> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }

}
