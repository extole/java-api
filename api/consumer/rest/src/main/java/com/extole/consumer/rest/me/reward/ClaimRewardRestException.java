package com.extole.consumer.rest.me.reward;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class ClaimRewardRestException extends ExtoleRestException {

    public static final ErrorCode<ClaimRewardRestException> INVALID_CAMPAIGN_ID =
        new ErrorCode<>("invalid_campaign_id", 400, "Invalid campaign id", "campaign_id");

    public static final ErrorCode<ClaimRewardRestException> INVALID_REWARDER_NAME =
        new ErrorCode<>("invalid_rewarder_name", 400, "Invalid rewarder name", "rewarder_name");

    public ClaimRewardRestException(String uniqueId, ErrorCode<?> errorCode, Map<String, Object> parameters,
        Throwable cause) {
        super(uniqueId, errorCode, parameters, cause);
    }
}
