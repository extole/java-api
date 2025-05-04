package com.extole.client.rest.campaign.controller.action.creative;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionCreativeValidationRestException extends CampaignControllerActionRestException {
    public static final ErrorCode<CampaignControllerActionCreativeValidationRestException> INVALID_ARCHIVE =
        new ErrorCode<>("campaign_controller_action_creative_archive_error", 403, "Invalid creative archive",
            "archive_id");

    public CampaignControllerActionCreativeValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionCreativeValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
