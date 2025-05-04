package com.extole.client.rest.campaign.controller.action.fulfill.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionFulfillRewardValidationRestException
    extends CampaignControllerActionRestException {

    public static final ErrorCode<
        CampaignControllerActionFulfillRewardValidationRestException> INVALID_SPEL_EXPRESSION =
            new ErrorCode<>("campaign_controller_action_fulfill_reward_invalid_spel_expression",
                400, "SPeL expression is not valid", "expression");

    public static final ErrorCode<CampaignControllerActionFulfillRewardValidationRestException> TOO_LONG_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_fulfill_reward_too_long_expression",
            400, "Expression should not exceed 2048 characters", "expression");

    public static final ErrorCode<CampaignControllerActionFulfillRewardValidationRestException> UNDEFINED_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_fulfill_reward_undefined_expression",
            400, "Expression can't be empty", "expression");

    public static final ErrorCode<CampaignControllerActionFulfillRewardValidationRestException> EMPTY_REWARD_ID =
        new ErrorCode<>("campaign_controller_action_fulfill_reward_empty_reward_id",
            400, "Reward Id can't be empty");

    public CampaignControllerActionFulfillRewardValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionFulfillRewardValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
