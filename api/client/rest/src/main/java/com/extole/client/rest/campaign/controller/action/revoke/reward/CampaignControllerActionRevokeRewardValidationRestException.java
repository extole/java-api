package com.extole.client.rest.campaign.controller.action.revoke.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionRevokeRewardValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionRevokeRewardValidationRestException> MISSING_REWARD_ID =
        new ErrorCode<>("campaign_controller_action_revoke_reward_missing_reward_id",
            400, "Reward Id can't be empty");

    public static final ErrorCode<CampaignControllerActionRevokeRewardValidationRestException> INVALID_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_revoke_reward_invalid_expression",
            400, "Invalid SpEL expression", "expression");

    public static final ErrorCode<CampaignControllerActionRevokeRewardValidationRestException> TOO_LONG_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_revoke_reward_too_long_expression",
            400, "Expression should not exceed 2000 characters", "expression");

    public static final ErrorCode<CampaignControllerActionRevokeRewardValidationRestException> UNDEFINED_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_revoke_reward_undefined_expression",
            400, "Expression can't be empty");

    public CampaignControllerActionRevokeRewardValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionRevokeRewardValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
