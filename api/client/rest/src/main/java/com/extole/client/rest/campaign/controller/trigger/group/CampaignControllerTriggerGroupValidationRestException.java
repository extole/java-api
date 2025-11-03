package com.extole.client.rest.campaign.controller.trigger.group;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerGroupValidationRestException
    extends CampaignControllerTriggerRestException {

    public CampaignControllerTriggerGroupValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
