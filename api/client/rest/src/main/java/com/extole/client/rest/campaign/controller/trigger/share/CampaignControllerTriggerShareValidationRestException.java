package com.extole.client.rest.campaign.controller.trigger.share;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerShareValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerShareValidationRestException> MISSING_CHANNELS =
        new ErrorCode<>("campaign_controller_trigger_share_missing_channels", 403, "Missing channels");

    public CampaignControllerTriggerShareValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
