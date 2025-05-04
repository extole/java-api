package com.extole.client.rest.campaign.controller.trigger.max.mind;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerMaxMindValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerMaxMindValidationRestException> INVALID_THRESHOLD =
        new ErrorCode<>("campaign_controller_trigger_max_mind_invalid_threshold", 400,
            "Invalid threshold", "campaign_id", "controller_id");

    public CampaignControllerTriggerMaxMindValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
