package com.extole.client.rest.campaign.controller.action.creative;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionCreativeMissingRestException extends CampaignControllerActionRestException {
    public static final ErrorCode<CampaignControllerActionCreativeMissingRestException> MISSING_ARCHIVE =
        new ErrorCode<>("campaign_controller_action_creative_missing_archive", 400,
            "Creative action does not have associated creative archive", "action_id");

    public CampaignControllerActionCreativeMissingRestException(String uniqueId,
        ErrorCode<CampaignControllerActionCreativeMissingRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
