package com.extole.client.rest.campaign.controller.trigger.audience.membership.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerAudienceMembershipEventValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerAudienceMembershipEventValidationRestException> AUDIENCE_NOT_FOUND =
            new ErrorCode<>("audience_not_found", 400, "Was unable to find audience with id", "audience_id");

    public CampaignControllerTriggerAudienceMembershipEventValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
