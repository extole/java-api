package com.extole.client.rest.campaign.controller.trigger.step.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerStepEventValidationRestException extends CampaignControllerTriggerRestException {

    public static final ErrorCode<CampaignControllerTriggerStepEventValidationRestException> DATA_NAME_INVALID_LENGTH =
        new ErrorCode<>("data_name_invalid_length", 400, "Data name is either blank or is too long", "data_name",
            "max_length");

    public static final ErrorCode<CampaignControllerTriggerStepEventValidationRestException> EVENT_NAME_INVALID_LENGTH =
        new ErrorCode<>("event_name_invalid_length", 400, "Event name is either blank or is not of valid length",
            "event_name", "min_length", "max_length");

    public static final ErrorCode<
        CampaignControllerTriggerStepEventValidationRestException> ILLEGAL_CHARACTER_IN_EVENT_NAME =
            new ErrorCode<>("illegal_character_in_event_name", 400,
                "Event name should contain only alphanumeric, underscore, dash, dot and space characters",
                "event_name");

    public static final ErrorCode<CampaignControllerTriggerStepEventValidationRestException> MISSING_CONSTRAINT =
        new ErrorCode<>("missing_constraint", 400, "It is required to have at least one constraint defined");

    public CampaignControllerTriggerStepEventValidationRestException(String uniqueId, ErrorCode<?> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
