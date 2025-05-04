package com.extole.client.rest.campaign.controller.trigger.send.reward.event;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerSendRewardEventValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerSendRewardEventValidationRestException> REWARD_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>("campaign_controller_trigger_send_reward_event_reward_name_length_out_of_range", 400,
                "Reward name is not of valid length. Maximum length is 255 characters", "reward_name");

    public static final ErrorCode<CampaignControllerTriggerSendRewardEventValidationRestException> INVALID_TAG =
        new ErrorCode<>("campaign_controller_trigger_send_reward_event_invalid_event_tag", 400,
            "Tag should contain alphanumeric or _ - characters, length should be between 0 and 255", "tag");

    public CampaignControllerTriggerSendRewardEventValidationRestException(
        String uniqueId, ErrorCode<?> code, Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
