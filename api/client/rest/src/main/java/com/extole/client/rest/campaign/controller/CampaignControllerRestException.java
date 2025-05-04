package com.extole.client.rest.campaign.controller;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignControllerRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignControllerRestException> INVALID_CAMPAIGN_CONTROLLER_ID = new ErrorCode<>(
        "invalid_campaign_controller_id", 403, "Invalid campaign controller id", "campaign_id", "controller_id");

    public static final ErrorCode<CampaignControllerRestException> INVALID_CONTROLLER_ACTION_ID = new ErrorCode<>(
        "invalid_controller_action_id", 403, "Invalid campaign controller action id", "controller_id", "action_id");

    public static final ErrorCode<CampaignControllerRestException> INVALID_CONTROLLER_TRIGGER_ID = new ErrorCode<>(
        "invalid_controller_trigger_id", 403, "Invalid campaign controller trigger id", "controller_id", "trigger_id");

    public CampaignControllerRestException(String uniqueId, ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
