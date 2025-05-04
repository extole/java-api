package com.extole.client.rest.campaign.controller.trigger;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public abstract class CampaignControllerTriggerRestException extends ExtoleRestException {

    public CampaignControllerTriggerRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
