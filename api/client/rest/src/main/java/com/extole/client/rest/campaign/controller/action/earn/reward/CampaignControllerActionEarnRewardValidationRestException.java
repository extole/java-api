package com.extole.client.rest.campaign.controller.action.earn.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.action.CampaignControllerActionRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerActionEarnRewardValidationRestException extends CampaignControllerActionRestException {

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> REWARD_NAME_TOO_LONG =
        new ErrorCode<>("campaign_controller_action_earn_reward_reward_name_too_long",
            400, "Reward name should not exceed 255 characters", "reward_name");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> MISSING_REWARD_NAME =
        new ErrorCode<>("campaign_controller_action_earn_reward_missing_reward_name",
            400, "Reward name is required");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> REWARD_SUPPLIER_NOT_FOUND =
        new ErrorCode<>("campaign_controller_action_earn_reward_reward_supplier_id_not_found",
            400, "Reward supplier not found", "reward_supplier_id");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> INVALID_TAG =
        new ErrorCode<>("campaign_controller_action_earn_reward_invalid_tag",
            400, "Tag length should not exceed 255 characters", "tag");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> EMPTY_TAG =
        new ErrorCode<>("campaign_controller_action_earn_reward_invalid_tag",
            400, "Tag can't be empty");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> INVALID_TAGS =
        new ErrorCode<>("campaign_controller_action_earn_reward_missing_tags",
            400, "At least one tag should be provided");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> INVALID_TAGS_LENGTH =
        new ErrorCode<>("campaign_controller_action_earn_reward_invalid_tags_length",
            400, "Cumulative tags length can't be greater than 2048");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> TOO_LONG_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_earn_reward_too_long_expression",
            400, "Expression should not exceed 2048 characters", "expression");

    public static final ErrorCode<CampaignControllerActionEarnRewardValidationRestException> UNDEFINED_EXPRESSION =
        new ErrorCode<>("campaign_controller_action_earn_reward_undefined_expression",
            400, "Expression can't be empty", "expression");

    public static final ErrorCode<
        CampaignControllerActionEarnRewardValidationRestException> DATA_ATTRIBUTE_NAME_INVALID =
            new ErrorCode<>("campaign_controller_action_earn_reward_data_attribute_name_invalid", 400,
                "Data attribute name is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionEarnRewardValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_earn_reward_data_attribute_name_too_long", 400,
                "Data attribute name should not exceed 200 characters", "name");

    public static final ErrorCode<
        CampaignControllerActionEarnRewardValidationRestException> DATA_ATTRIBUTE_VALUE_INVALID =
            new ErrorCode<>("campaign_controller_action_earn_reward_data_attribute_value_invalid", 400,
                "Data attribute value is invalid", "name");

    public static final ErrorCode<
        CampaignControllerActionEarnRewardValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
            new ErrorCode<>(
                "campaign_controller_action_earn_reward_data_attribute_value_too_long", 400,
                "Data attribute value should not exceed 2048 characters", "name");

    public CampaignControllerActionEarnRewardValidationRestException(String uniqueId,
        ErrorCode<CampaignControllerActionEarnRewardValidationRestException> code, Map<String, Object> attributes,
        Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
