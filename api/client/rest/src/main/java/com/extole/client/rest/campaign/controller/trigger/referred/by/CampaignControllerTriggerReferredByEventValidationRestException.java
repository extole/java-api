package com.extole.client.rest.campaign.controller.trigger.referred.by;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerReferredByEventValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerReferredByEventValidationRestException> INVALID_REFERRAL_ORIGINATOR =
            new ErrorCode<>("campaign_controller_trigger_referred_by_event_invalid_referral_originator", 400,
                "Invalid referral originator");

    public CampaignControllerTriggerReferredByEventValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
