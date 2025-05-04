package com.extole.client.rest.campaign.flow.step.app;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFlowStepAppRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFlowStepAppRestException> INVALID_CAMPAIGN_FLOW_STEP_APP_ID = new ErrorCode<>(
        "invalid_campaign_flow_step_app_id", 400, "Invalid campaign flow step app id", "campaign_id", "flow_step_id",
        "flow_step_app_id");

    public CampaignFlowStepAppRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
