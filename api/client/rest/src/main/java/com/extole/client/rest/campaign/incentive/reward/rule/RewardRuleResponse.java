package com.extole.client.rest.campaign.incentive.reward.rule;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ObjectUtils;

import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.common.lang.ToString;

public class RewardRuleResponse {

    private static final String REWARD_RULE_ID = "id";
    private static final String REWARDEE = "rewardee";
    private static final String REWARD_SUPPLIER_ID = "reward_supplier_id";
    private static final String REFERRALS_PER_REWARD = "referrals_per_reward";
    private static final String REWARD_COUNT_LIMIT = "reward_count_limit";
    private static final String REWARD_COUNT_SINCE_MONTH = "reward_count_since_month";
    private static final String REWARD_COUNT_SINCE_DAYS = "reward_count_since_days";
    private static final String REWARD_VALUE_LIMIT = "reward_value_limit";
    private static final String REWARD_VALUE_SINCE_MONTH = "reward_value_since_month";
    private static final String REWARD_VALUE_SINCE_DAYS = "reward_value_since_days";
    private static final String UNIQUE_FRIEND_REQUIRED = "is_unique_friend_required";
    private static final String REFERRAL_LOOP_ALLOWED = "is_referral_loop_allowed";
    private static final String REWARD_SLOTS = "reward_slots";
    private static final String MIN_CART_VALUE = "min_cart_value";
    private static final String TRIGGER_ACTION_TYPE = "trigger_action_type";
    private static final String EMAIL_REQUIRED = "is_email_required";
    private static final String DATA_ATTRIBUTE_NAME = "data_attribute_name";
    private static final String DATA_ATTRIBUTE_VALUE = "data_attribute_value";
    private static final String DATA_ATTRIBUTE_MATCHER_TYPE = "data_attribute_matcher_type";
    private static final String EXPRESSION = "expression";
    private static final String REWARD_EVERY_X_FRIEND_ACTIONS = "reward_every_x_friend_actions";
    private static final String COUNT_REWARDS_BASED_ON_PARTNER_USER_ID = "count_rewards_based_on_partner_user_id";

    private final String id;
    private final Rewardee rewardee;
    private final String rewardSupplierId;
    private final Integer referralsPerReward;
    private final Integer rewardCountLimit;
    private final Integer rewardCountSinceMonth;
    private final Integer rewardCountSinceDays;
    private final BigDecimal rewardValueLimit;
    private final Integer rewardValueSinceMonth;
    private final Integer rewardValueSinceDays;
    private final Boolean uniqueFriendRequired;
    private final Boolean referralLoopAllowed;
    private final Set<String> rewardSlots;
    private final BigDecimal minCartValue;
    private final RuleActionType triggerActionType;
    private final Boolean isEmailRequired;
    private final String dataAttributeName;
    private final String dataAttributeValue;
    private final RuleDataMatcherType dataAttributeMatcherType;
    private final RewardRuleExpression expression;
    private final Integer rewardEveryXFriendActions;
    private final Boolean countRewardsBasedOnPartnerUserId;

    public RewardRuleResponse(@JsonProperty(REWARD_RULE_ID) String id,
        @JsonProperty(REWARDEE) Rewardee rewardee,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(REFERRALS_PER_REWARD) Integer referralsPerReward,
        @JsonProperty(REWARD_COUNT_LIMIT) Integer rewardCountLimit,
        @Nullable @JsonProperty(REWARD_COUNT_SINCE_MONTH) Integer rewardCountSinceMonth,
        @Nullable @JsonProperty(REWARD_COUNT_SINCE_DAYS) Integer rewardCountSinceDays,
        @JsonProperty(REWARD_VALUE_LIMIT) BigDecimal rewardValueLimit,
        @Nullable @JsonProperty(REWARD_VALUE_SINCE_MONTH) Integer rewardValueSinceMonth,
        @Nullable @JsonProperty(REWARD_VALUE_SINCE_DAYS) Integer rewardValueSinceDays,
        @JsonProperty(UNIQUE_FRIEND_REQUIRED) Boolean uniqueFriendRequired,
        @JsonProperty(REFERRAL_LOOP_ALLOWED) Boolean referralLoopAllowed,
        @JsonProperty(REWARD_SLOTS) Set<String> rewardSlots,
        @JsonProperty(MIN_CART_VALUE) BigDecimal minCartValue,
        @JsonProperty(TRIGGER_ACTION_TYPE) RuleActionType triggerActionType,
        @JsonProperty(EMAIL_REQUIRED) Boolean isEmailRequired,
        @Nullable @JsonProperty(DATA_ATTRIBUTE_NAME) String dataAttributeName,
        @Nullable @JsonProperty(DATA_ATTRIBUTE_VALUE) String dataAttributeValue,
        @Nullable @JsonProperty(DATA_ATTRIBUTE_MATCHER_TYPE) RuleDataMatcherType dataAttributeMatcherType,
        @Nullable @JsonProperty(EXPRESSION) RewardRuleExpression expression,
        @Nullable @JsonProperty(REWARD_EVERY_X_FRIEND_ACTIONS) Integer rewardEveryXFriendActions,
        @Nullable @JsonProperty(COUNT_REWARDS_BASED_ON_PARTNER_USER_ID) Boolean countRewardsBasedOnPartnerUserId) {

        this.id = id;
        this.rewardee = rewardee;
        this.rewardSupplierId = rewardSupplierId;
        this.referralsPerReward = referralsPerReward;
        this.rewardCountLimit = rewardCountLimit;
        this.rewardCountSinceMonth = rewardCountSinceMonth;
        this.rewardCountSinceDays = rewardCountSinceDays;
        this.rewardValueLimit = rewardValueLimit;
        this.rewardValueSinceMonth = rewardValueSinceMonth;
        this.rewardValueSinceDays = rewardValueSinceDays;
        this.uniqueFriendRequired = uniqueFriendRequired;
        this.referralLoopAllowed = referralLoopAllowed;
        this.rewardSlots = rewardSlots;
        this.minCartValue = minCartValue;
        this.triggerActionType = triggerActionType;
        this.isEmailRequired = isEmailRequired;
        this.dataAttributeName = dataAttributeName;
        this.dataAttributeValue = dataAttributeValue;
        this.dataAttributeMatcherType = dataAttributeMatcherType;
        this.expression = expression;
        this.rewardEveryXFriendActions = rewardEveryXFriendActions;
        this.countRewardsBasedOnPartnerUserId =
            ObjectUtils.defaultIfNull(countRewardsBasedOnPartnerUserId, Boolean.FALSE);
    }

    @JsonProperty(REWARD_RULE_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(REWARDEE)
    public Rewardee getRewardee() {
        return rewardee;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(REFERRALS_PER_REWARD)
    public Integer getReferralsPerReward() {
        return referralsPerReward;
    }

    @JsonProperty(REWARD_COUNT_LIMIT)
    public Integer getRewardCountLimit() {
        return rewardCountLimit;
    }

    @JsonProperty(REWARD_COUNT_SINCE_MONTH)
    public Integer getRewardCountSinceMonth() {
        return rewardCountSinceMonth;
    }

    @JsonProperty(REWARD_COUNT_SINCE_DAYS)
    public Integer getRewardCountSinceDays() {
        return rewardCountSinceDays;
    }

    @JsonProperty(REWARD_VALUE_LIMIT)
    public BigDecimal getRewardValueLimit() {
        return rewardValueLimit;
    }

    @JsonProperty(REWARD_VALUE_SINCE_MONTH)
    public Integer getRewardValueSinceMonth() {
        return rewardValueSinceMonth;
    }

    @JsonProperty(REWARD_VALUE_SINCE_DAYS)
    public Integer getRewardValueSinceDays() {
        return rewardValueSinceDays;
    }

    @JsonProperty(UNIQUE_FRIEND_REQUIRED)
    public Boolean getUniqueFriendRequired() {
        return uniqueFriendRequired;
    }

    @JsonProperty(REFERRAL_LOOP_ALLOWED)
    public Boolean isReferralLoopAllowed() {
        return referralLoopAllowed;
    }

    @JsonProperty(REWARD_SLOTS)
    public Set<String> getRewardSlots() {
        return rewardSlots;
    }

    @JsonProperty(MIN_CART_VALUE)
    public BigDecimal getMinCartValue() {
        return minCartValue;
    }

    @JsonProperty(TRIGGER_ACTION_TYPE)
    public RuleActionType getTriggerActionType() {
        return triggerActionType;
    }

    @JsonProperty(EMAIL_REQUIRED)
    public Boolean isEmailRequired() {
        return isEmailRequired;
    }

    @JsonProperty(DATA_ATTRIBUTE_NAME)
    public String getDataAttributeName() {
        return dataAttributeName;
    }

    @JsonProperty(DATA_ATTRIBUTE_VALUE)
    public String getDataAttributeValue() {
        return dataAttributeValue;
    }

    @JsonProperty(DATA_ATTRIBUTE_MATCHER_TYPE)
    public RuleDataMatcherType getDataAttributeMatcherType() {
        return dataAttributeMatcherType;
    }

    @Nullable
    @JsonProperty(EXPRESSION)
    public RewardRuleExpression getExpression() {
        return expression;
    }

    @JsonProperty(REWARD_EVERY_X_FRIEND_ACTIONS)
    public Integer getRewardEveryXFriendActions() {
        return rewardEveryXFriendActions;
    }

    @JsonProperty(COUNT_REWARDS_BASED_ON_PARTNER_USER_ID)
    public Boolean getCountRewardsBasedOnPartnerUserId() {
        return countRewardsBasedOnPartnerUserId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
