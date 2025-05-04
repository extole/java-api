package com.extole.client.rest.campaign.incentive;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignIncentiveRestException extends ExtoleRestException {
    public static final ErrorCode<CampaignIncentiveRestException> INVALID_CAMPAIGN_ID = new ErrorCode<>(
        "invalid_campaign_id", 400, "Invalid Campaign Id", "campaign_id");

    public CampaignIncentiveRestException(String uniqueId, ErrorCode<CampaignIncentiveRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
