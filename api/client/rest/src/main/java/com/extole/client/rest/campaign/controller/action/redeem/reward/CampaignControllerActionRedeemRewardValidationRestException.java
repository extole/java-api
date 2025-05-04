package com.extole.client.rest.campaign.controller.action.redeem.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionRedeemRewardValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionRedeemRewardValidationRestException> MISSING_REWARD_ID =
        new ErrorCode<>("campaign_controller_action_redeem_reward_missing_reward_id",
            400, "Reward Id can't be empty");

    public static final ErrorCode<
        CampaignControllerActionRedeemRewardValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
            new ErrorCode<>("campaign_controller_action_redeem_reward_data_attribute_name_invalid", 400,
                "Data attribute name is invalid, should not exceed 2000 characters", "name");

    public static final ErrorCode<
        CampaignControllerActionRedeemRewardValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_action_redeem_reward_data_attribute_value_invalid", 400,
                "Data attribute value is invalid, should not exceed 2000 characters", "name");

    public static final ErrorCode<CampaignControllerActionRedeemRewardValidationRestException> INVALID_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_redeem_reward_invalid_expression",
            400, "Invalid SpEL expression", "expression");

    public static final ErrorCode<CampaignControllerActionRedeemRewardValidationRestException> TOO_LONG_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_redeem_reward_too_long_expression",
            400, "Expression should not exceed 2000 characters", "expression");

    public static final ErrorCode<CampaignControllerActionRedeemRewardValidationRestException> UNDEFINED_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_redeem_reward_undefined_expression",
            400, "Expression can't be empty");

    public CampaignControllerActionRedeemRewardValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionRedeemRewardValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
