package com.extole.client.rest.campaign.controller.trigger.has.prior.reward;

import java.util.Map;

import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRestException;
import com.extole.common.rest.exception.ErrorCode;

public class CampaignControllerTriggerHasPriorRewardValidationRestException
    extends CampaignControllerTriggerRestException {

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_FILTER_TAGS_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_filter_tags_length", 400,
                "Cumulative tags length can't be greater than 2048");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_FILTER_EXPRESSION_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_filter_value_length", 400,
                "Filter expression value length can't be greater than 2000");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> MISSING_FILTER_EXPRESSION_VALUE =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_missing_filter_value", 400,
                "Filter expression value can't be null or empty");

    public static final ErrorCode<CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_EXPRESSION =
        new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_expression", 400,
            "Invalid expression", "expression");

    public static final ErrorCode<CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_TAG_LENGTH =
        new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_tag_length", 400,
            "Tag length can't be greater than 255", "tag");

    public static final ErrorCode<CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_TAG =
        new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_tag", 400,
            "Tag can't be null or empty");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_FILTER_NAME_LENGTH =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_filter_name_length", 400,
                "Filter name length can't be greater than 255");

    public static final ErrorCode<CampaignControllerTriggerHasPriorRewardValidationRestException> MISSING_FILTER_NAME =
        new ErrorCode<>("campaign_controller_trigger_has_prior_reward_missing_filter_name", 400,
            "Filter name can't be null or empty");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> REWARD_SUPPLIER_NOT_FOUND =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_supplier_not_found", 400,
                "Reward supplier not found", "reward_supplier_id");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> MISSING_AGGREGATION_CONDITION =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_missing_aggregation_condition", 400,
                "At least one aggregation condition is required");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_AGGREGATION_SUM =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_aggregation_sum", 400,
                "Invalid aggregation sum, only non negative values are allowed", "value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_AGGREGATION_COUNT =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_aggregation_count", 400,
                "Invalid aggregation count, only non negative values are allowed", "value");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_AGGREGATION_SUM_CONFIGURATION =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_aggregation_sum_configuration", 400,
                "Invalid aggregation sum value, maxValue should be greater than minValue");

    public static final ErrorCode<
        CampaignControllerTriggerHasPriorRewardValidationRestException> INVALID_AGGREGATION_COUNT_CONFIGURATION =
            new ErrorCode<>("campaign_controller_trigger_has_prior_reward_invalid_aggregation_count_configuration", 400,
                "Invalid aggregation count value, countMax should be greater than countMin");

    public CampaignControllerTriggerHasPriorRewardValidationRestException(String uniqueId, ErrorCode<?> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }

}
