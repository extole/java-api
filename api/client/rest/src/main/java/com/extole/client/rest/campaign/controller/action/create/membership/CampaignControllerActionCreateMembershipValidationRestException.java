package com.extole.client.rest.campaign.controller.action.create.membership;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionCreateMembershipValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionCreateMembershipValidationRestException> AUDIENCE_NOT_FOUND =
        new ErrorCode<>("campaign_controller_action_create_membership_audience_not_found", 400,
            "Was unable to find audience with id", "audience_id");

    public CampaignControllerActionCreateMembershipValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionCreateMembershipValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
