package com.extole.client.rest.campaign.controller.action;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public abstract class CampaignControllerActionRestException extends ExtoleRestException {

    public CampaignControllerActionRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
