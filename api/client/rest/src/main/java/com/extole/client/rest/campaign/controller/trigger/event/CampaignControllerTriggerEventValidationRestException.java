package com.extole.client.rest.campaign.controller.trigger.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerEventValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerEventValidationRestException> MISSING_EVENT_NAME =
        new ErrorCode<>("campaign_controller_trigger_event_missing_name", 400, "Missing event name");

    public static final ErrorCode<
        CampaignControllerTriggerEventValidationRestException> RESERVED_EVENT_NAME_FOR_NOT_FOUND_PAGE =
            new ErrorCode<>("reserved_event_name_for_not_found_page", 400,
                "Event name is only allowed for a single frontend controller with a buildtime display action " +
                    "on the global campaign",
                "event_name");

    public static final ErrorCode<CampaignControllerTriggerEventValidationRestException> UNSUPPORTED_EVENT_TYPE =
        new ErrorCode<>("campaign_controller_trigger_event_type_unsupported", 400,
            "Event type is not supported anymore", "event_type", "allowed_event_types");

    public CampaignControllerTriggerEventValidationRestException(String uniqueId,
        ErrorCode<?> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
