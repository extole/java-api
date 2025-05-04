package com.extole.client.rest.campaign.controller.trigger.access;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerAccessValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerAccessValidationRestException> MISSING_TRUSTED_SCOPES =
        new ErrorCode<>("campaign_controller_trigger_access_missing_trusted_scopes", 400, "Missing trusted scopes");

    public CampaignControllerTriggerAccessValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
