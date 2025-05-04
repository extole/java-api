package com.extole.client.rest.campaign.controller.trigger.legacy.quality;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerLegacyQualityValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerLegacyQualityValidationRestException> MISSING_ACTION_TYPE =
        new ErrorCode<>("campaign_controller_trigger_legacy_quality_missing_action_type", 400,
            "Action type is missing");

    public CampaignControllerTriggerLegacyQualityValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
