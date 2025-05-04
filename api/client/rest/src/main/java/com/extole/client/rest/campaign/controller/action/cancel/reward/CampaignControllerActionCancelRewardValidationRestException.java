package com.extole.client.rest.campaign.controller.action.cancel.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionCancelRewardValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionCancelRewardValidationRestException> INVALID_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_cancel_reward_invalid_expression",
            400, "Invalid SpEL expression", "expression");

    public static final ErrorCode<CampaignControllerActionCancelRewardValidationRestException> TOO_LONG_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_cancel_reward_too_long_expression",
            400, "Expression should not exceed 2048 characters", "expression");

    public static final ErrorCode<CampaignControllerActionCancelRewardValidationRestException> UNDEFINED_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_cancel_reward_undefined_expression",
            400, "Expression can't be empty", "expression");

    public static final ErrorCode<CampaignControllerActionCancelRewardValidationRestException> EMPTY_REWARD_ID =
        new ErrorCode<>("campaign_controller_action_cancel_reward_empty_reward_id",
            400, "Reward Id can't be empty");

    public CampaignControllerActionCancelRewardValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionCancelRewardValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
