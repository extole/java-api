package com.extole.client.rest.campaign.flow.step.metric;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignFlowStepMetricRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignFlowStepMetricRestException> INVALID_CAMPAIGN_FLOW_STEP_METRIC_ID =
        new ErrorCode<>(
            "invalid_campaign_flow_step_metric_id", 400, "Invalid campaign flow step metric id", "campaign_id",
            "flow_step_id", "flow_step_metric_id");

    public CampaignFlowStepMetricRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
