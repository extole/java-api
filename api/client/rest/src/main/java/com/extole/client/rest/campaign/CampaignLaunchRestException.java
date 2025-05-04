package com.extole.client.rest.campaign;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignLaunchRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignLaunchRestException> INVALID_CAMPAIGN_STATE = new ErrorCode<>(
        "invalid_campaign_state", 403, "Live campaigns cannot be launched", "campaign_id");

    public CampaignLaunchRestException(String uniqueId, ErrorCode<CampaignLaunchRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
