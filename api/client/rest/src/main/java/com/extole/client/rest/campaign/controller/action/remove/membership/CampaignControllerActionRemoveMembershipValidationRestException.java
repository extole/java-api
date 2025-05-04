package com.extole.client.rest.campaign.controller.action.remove.membership;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionRemoveMembershipValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionRemoveMembershipValidationRestException> AUDIENCE_NOT_FOUND =
        new ErrorCode<>("campaign_controller_action_remove_membership_audience_not_found", 400,
            "Was unable to find audience with id", "audience_id");

    public CampaignControllerActionRemoveMembershipValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionRemoveMembershipValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
