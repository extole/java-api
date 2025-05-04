package com.extole.client.rest.campaign.controller.trigger.data.intelligence.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerDataIntelligenceEventValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerDataIntelligenceEventValidationRestException> INVALID_EVENT_NAME =
            new ErrorCode<>("campaign_controller_trigger_data_intelligence_event_invalid_event_name",
                400, "Event name not valid");

    public static final ErrorCode<
        CampaignControllerTriggerDataIntelligenceEventValidationRestException> EVENT_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_trigger_data_intelligence_event_name_out_of_range", 400,
                "Event name length must be between 2 and 255 characters", "event_name");

    public static final ErrorCode<
        CampaignControllerTriggerDataIntelligenceEventValidationRestException> MISSING_EVENT_NAME =
            new ErrorCode<>(
                "campaign_controller_trigger_data_intelligence_event_missing_event_name", 400,
                "Event name missing");

    public CampaignControllerTriggerDataIntelligenceEventValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
