package com.extole.client.rest.campaign.component;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class CampaignComponentRestException extends ExtoleRestException {

    public static final ErrorCode<CampaignComponentRestException> CAMPAIGN_COMPONENT_NOT_FOUND = new ErrorCode<>(
        "campaign_component_not_found", 400, "Campaign Component not found", "campaign_id", "campaign_component_id");

    public static final ErrorCode<CampaignComponentRestException> SETTINGS_BUILD_FAILED = new ErrorCode<>(
        "settings_build_failed", 400, "Component settings build failed", "errors");

    public CampaignComponentRestException(String uniqueId, ErrorCode<CampaignComponentRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
