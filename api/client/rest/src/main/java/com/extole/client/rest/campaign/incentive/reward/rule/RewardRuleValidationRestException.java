package com.extole.client.rest.campaign.incentive.reward.rule;

import java.util.Map;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;

public class RewardRuleValidationRestException extends ExtoleRestException {

    public static final ErrorCode<RewardRuleValidationRestException> IDENTITY_KEY_INCOMPATIBLE_USAGE =
        new ErrorCode<>("identity_key_incompatible_usage", 400,
            "Non email Identity key cannot be used with reward every x friend actions reward rule");

    public static final ErrorCode<RewardRuleValidationRestException> MIN_CART_VALUE_INVALID = new ErrorCode<>(
        "min_cart_value_invalid", 400, "Minimum cart value must be a non negative number", "min_cart_value");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_EVERY_X_FRIEND_ACTIONS = new ErrorCode<>(
        "reward_every_x_friend_actions_invalid", 400, "Reward every X friend actions must be a non negative number",
        "reward_every_x_friend_actions");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_VALUE_LIMIT_INVALID = new ErrorCode<>(
        "reward_value_limit_invalid", 400, "Reward value limit must be a non negative number", "reward_value_limit");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_VALUE_SINCE_MONTH_INVALID = new ErrorCode<>(
        "reward_value_since_month_invalid", 400, "Reward value since month must be 0-12", "reward_value_since_month");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_VALUE_SINCE_DAYS_INVALID =
        new ErrorCode<>("reward_value_since_days_invalid", 400,
            "Reward value since days must be a non negative integer", "reward_value_since_days");

    public static final ErrorCode<RewardRuleValidationRestException> ACTION_COUNT_INVALID =
        new ErrorCode<>("action_count_invalid", 400, "Action count must be a positive integer", "action_count");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_COUNT_LIMIT_INVALID = new ErrorCode<>(
        "reward_count_limit_invalid", 400, "Reward count limit must be a positive integer", "reward_count_limit");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_COUNT_SINCE_MONTH_INVALID = new ErrorCode<>(
        "reward_count_since_month_invalid", 400, "Reward count since month must be 0-12", "reward_count_since_month");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_COUNT_SINCE_DAYS_INVALID =
        new ErrorCode<>("reward_count_since_days_invalid", 400,
            "Reward count since days must be a non negative integer", "reward_count_since_days");

    public static final ErrorCode<RewardRuleValidationRestException> DATA_ATTRIBUTE_REGEXP_INVALID =
        new ErrorCode<>("data_attribute_regexp_invalid", 400, "Data attribute value shoult be valid regexp",
            "data_attribute_value");

    public static final ErrorCode<RewardRuleValidationRestException> DATA_ATTRIBUTE_VALUE_TYPE_REQUIRED =
        new ErrorCode<>("data_attribute_value_type_required", 400, "Data attribute value type is required");

    public static final ErrorCode<RewardRuleValidationRestException> DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_value_out_of_range", 400, "Data attribute value length must not exceed 255 characters");

    public static final ErrorCode<RewardRuleValidationRestException> DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>(
            "data_attribute_name_out_of_range", 400, "Data attribute name length must not exceed 255 characters");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_SLOT_LENGTH_OUT_OF_RANGE =
        new ErrorCode<>("reward_slots_out_of_range", 400,
            "Reward slot length must be between 1 and 255 characters", "reward_slots");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_SLOT_INVALID_NAME =
        new ErrorCode<>("reward_slots_invalid_name", 400,
            "Reward slot name should contain only characters, digits and '_','-' or ,", "reward_slots");

    public static final ErrorCode<RewardRuleValidationRestException> REWARD_SLOTS_CONCATENATED_LENGTH_EXCEPTION =
        new ErrorCode<>("reward_slots_concatenated_length_exception", 400,
            "Rewards slots concatenated length should be less than 255 characters", "reward_slots");

    public static final ErrorCode<RewardRuleValidationRestException> EXPRESSION_VALUE_INVALID =
        new ErrorCode<>("expression_value_invalid", 400,
            "Reward expression is not valid", "expression_value", "expression_type");

    public static final ErrorCode<RewardRuleValidationRestException> EXPRESSION_VALUE_INVALID_LENGTH =
        new ErrorCode<>("expression_value_invalid_length", 400,
            "Expression value is not of valid length", "expression_value", "max_length");

    public static final ErrorCode<RewardRuleValidationRestException> EXPRESSION_INVALID_TYPE =
        new ErrorCode<>("expression_invalid_type", 400,
            "Invalid expression type", "expression_type");

    public static final ErrorCode<RewardRuleValidationRestException> EXPRESSION_MISSING_TYPE =
        new ErrorCode<>("expression_missing_type", 400,
            "Expression type missing");

    public static final ErrorCode<RewardRuleValidationRestException> EXPRESSION_MISSING_VALUE =
        new ErrorCode<>("expression_missing_value", 400,
            "Expression value missing");

    public RewardRuleValidationRestException(String uniqueId, ErrorCode<RewardRuleValidationRestException> code,
        Map<String, Object> attributes, Throwable cause) {
        super(uniqueId, code, attributes, cause);
    }
}
