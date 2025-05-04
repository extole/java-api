package com.extole.client.rest.campaign.controller.trigger.score;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerScoreValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerScoreValidationRestException> MISSING_CAUSE_EVENT_NAME =
        new ErrorCode<>("campaign_controller_trigger_score_missing_cause_event_name", 400, "Missing cause event name");

    public static final ErrorCode<
        CampaignControllerTriggerScoreValidationRestException> CAUSE_EVENT_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_score_cause_event_name_length_out_of_range", 400,
                "Cause event name is not of valid length", "cause_event_name");

    public static final ErrorCode<
        CampaignControllerTriggerScoreValidationRestException> CAUSE_EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER =
            new ErrorCode<>("campaign_controller_trigger_score_cause_event_name_contains_illegal_character", 400,
                "Cause event name should contain alphanumeric, underscore, dash, dot and space characters only",
                "cause_event_name");

    public CampaignControllerTriggerScoreValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
