package com.extole.client.rest.campaign.controller.trigger.reward.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerRewardEventValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerRewardEventValidationRestException> EVENT_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_reward_event_event_name_length_out_of_range", 400,
                "Event name is not of valid length", "event_name");

    public static final ErrorCode<
        CampaignControllerTriggerRewardEventValidationRestException> EVENT_NAME_CONTAINS_ILLEGAL_CHARACTER =
            new ErrorCode<>("campaign_controller_trigger_reward_event_event_name_contains_illegal_character", 400,
                "Event name should contain alphanumeric, underscore, dash, dot and space characters only",
                "event_name");

    public static final ErrorCode<CampaignControllerTriggerRewardEventValidationRestException> INVALID_TAG =
        new ErrorCode<>("campaign_controller_trigger_reward_event_invalid_event_tag", 400,
            "tag should contain alphanumeric or _ - characters, length should be between 0 and 255",
            "campaign_id", "controller_id", "tag");

    public static final ErrorCode<CampaignControllerTriggerRewardEventValidationRestException> INVALID_REWARD_STATES =
        new ErrorCode<>("campaign_controller_trigger_reward_event_invalid_reward_states", 400,
            "at least one reward state is required");

    public CampaignControllerTriggerRewardEventValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
