package com.extole.client.rest.campaign.controller.trigger.audience.membership;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerAudienceMembershipValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerAudienceMembershipValidationRestException> AUDIENCE_NOT_FOUND =
            new ErrorCode<>("audience_not_found", 400, "Was unable to find audience with id", "audience_id");

    public static final ErrorCode<
        CampaignControllerTriggerAudienceMembershipValidationRestException> DISABLED_AUDIENCE =
            new ErrorCode<>("disabled_audience", 400, "Can't refer to a disabled audience", "audience_id");

    public CampaignControllerTriggerAudienceMembershipValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
