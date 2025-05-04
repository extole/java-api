package com.extole.client.rest.campaign.controller.action.data.intelligence;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionDataIntelligenceValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionDataIntelligenceValidationRestException> INVALID_EVENT_NAME =
        new ErrorCode<>("campaign_controller_action_data_intelligence_event_invalid_event_name",
            400, "Event name not valid");

    public static final ErrorCode<
        CampaignControllerActionDataIntelligenceValidationRestException> EVENT_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_data_intelligence_event_name_out_of_range", 400,
                "Event name length must be between 2 and 200 characters", "event_name");

    public CampaignControllerActionDataIntelligenceValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionDataIntelligenceValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
