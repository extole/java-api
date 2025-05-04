package com.extole.client.rest.campaign.incentive.reward.rule;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class RewardRuleCreateRequest {

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
    private static final String RULE_ACTION_TYPE = "trigger_action_type";
    private static final String EMAIL_REQUIRED = "is_email_required";
    private static final String DATA_ATTRIBUTE_NAME = "data_attribute_name";
    private static final String DATA_ATTRIBUTE_VALUE = "data_attribute_value";
    private static final String DATA_ATTRIBUTE_MATCHER_TYPE = "data_attribute_matcher_type";
    private static final String EXPRESSION = "expression";
    private static final String REWARD_EVERY_X_FRIEND_ACTIONS = "reward_every_x_friend_actions";
    private static final String COUNT_REWARDS_BASED_ON_PARTNER_USER_ID = "count_rewards_based_on_partner_user_id";

    private final Omissible<Rewardee> rewardee;
    private final String rewardSupplierId;
    private final Omissible<Integer> referralsPerReward;
    private final Omissible<Integer> rewardCountLimit;
    private final Omissible<Integer> rewardCountSinceMonth;
    private final Omissible<Integer> rewardCountSinceDays;
    private final Omissible<BigDecimal> rewardValueLimit;
    private final Omissible<Integer> rewardValueSinceMonth;
    private final Omissible<Integer> rewardValueSinceDays;
    private final Omissible<Boolean> uniqueFriendRequired;
    private final Omissible<Boolean> referralLoopAllowed;
    private final Omissible<Set<String>> rewardSlots;
    private final Omissible<BigDecimal> minCartValue;
    private final Omissible<RuleActionType> ruleActionType;
    private final Omissible<Boolean> isEmailRequired;
    private final Omissible<String> dataAttributeName;
    private final Omissible<String> dataAttributeValue;
    private final Omissible<RuleDataMatcherType> dataAttributeMatcherType;
    private final Omissible<RewardRuleExpression> rewardRuleExpression;
    private final Omissible<Integer> rewardEveryXFriendActions;
    private final Omissible<Boolean> countRewardsBasedOnPartnerUserId;

    @JsonCreator
    public RewardRuleCreateRequest(@JsonProperty(REWARDEE) Omissible<Rewardee> rewardee,
        @JsonProperty(REWARD_SUPPLIER_ID) String rewardSupplierId,
        @JsonProperty(REFERRALS_PER_REWARD) Omissible<Integer> referralsPerReward,
        @JsonProperty(REWARD_COUNT_LIMIT) Omissible<Integer> rewardCountLimit,
        @JsonProperty(REWARD_COUNT_SINCE_MONTH) Omissible<Integer> rewardCountSinceMonth,
        @JsonProperty(REWARD_COUNT_SINCE_DAYS) Omissible<Integer> rewardCountSinceDays,
        @JsonProperty(REWARD_VALUE_LIMIT) Omissible<BigDecimal> rewardValueLimit,
        @JsonProperty(REWARD_VALUE_SINCE_MONTH) Omissible<Integer> rewardValueSinceMonth,
        @JsonProperty(REWARD_VALUE_SINCE_DAYS) Omissible<Integer> rewardValueSinceDays,
        @JsonProperty(UNIQUE_FRIEND_REQUIRED) Omissible<Boolean> uniqueFriendRequired,
        @JsonProperty(REFERRAL_LOOP_ALLOWED) Omissible<Boolean> referralLoopAllowed,
        @JsonProperty(REWARD_SLOTS) Omissible<Set<String>> rewardSlots,
        @JsonProperty(MIN_CART_VALUE) Omissible<BigDecimal> minCartValue,
        @JsonProperty(RULE_ACTION_TYPE) Omissible<RuleActionType> ruleActionType,
        @JsonProperty(EMAIL_REQUIRED) Omissible<Boolean> isEmailRequired,
        @JsonProperty(DATA_ATTRIBUTE_NAME) Omissible<String> dataAttributeName,
        @JsonProperty(DATA_ATTRIBUTE_VALUE) Omissible<String> dataAttributeValue,
        @JsonProperty(DATA_ATTRIBUTE_MATCHER_TYPE) Omissible<RuleDataMatcherType> dataAttributeMatcherType,
        @JsonProperty(EXPRESSION) Omissible<RewardRuleExpression> rewardRuleExpression,
        @JsonProperty(REWARD_EVERY_X_FRIEND_ACTIONS) Omissible<Integer> rewardEveryXFriendActions,
        @JsonProperty(COUNT_REWARDS_BASED_ON_PARTNER_USER_ID) Omissible<Boolean> countRewardsBasedOnPartnerUserId) {
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
        this.ruleActionType = ruleActionType;
        this.isEmailRequired = isEmailRequired;
        this.dataAttributeName = dataAttributeName;
        this.dataAttributeValue = dataAttributeValue;
        this.dataAttributeMatcherType = dataAttributeMatcherType;
        this.rewardRuleExpression = rewardRuleExpression;
        this.rewardEveryXFriendActions = rewardEveryXFriendActions;
        this.countRewardsBasedOnPartnerUserId = countRewardsBasedOnPartnerUserId;
    }

    @JsonProperty(REWARDEE)
    public Omissible<Rewardee> getRewardee() {
        return rewardee;
    }

    @JsonProperty(REWARD_SUPPLIER_ID)
    public String getRewardSupplierId() {
        return rewardSupplierId;
    }

    @JsonProperty(REFERRALS_PER_REWARD)
    public Omissible<Integer> getReferralsPerReward() {
        return referralsPerReward;
    }

    @JsonProperty(REWARD_COUNT_LIMIT)
    public Omissible<Integer> getRewardCountLimit() {
        return rewardCountLimit;
    }

    @JsonProperty(REWARD_COUNT_SINCE_MONTH)
    public Omissible<Integer> getRewardCountSinceMonth() {
        return rewardCountSinceMonth;
    }

    @JsonProperty(REWARD_COUNT_SINCE_DAYS)
    public Omissible<Integer> getRewardCountSinceDays() {
        return rewardCountSinceDays;
    }

    @JsonProperty(REWARD_VALUE_LIMIT)
    public Omissible<BigDecimal> getRewardValueLimit() {
        return rewardValueLimit;
    }

    @JsonProperty(REWARD_VALUE_SINCE_MONTH)
    public Omissible<Integer> getRewardValueSinceMonth() {
        return rewardValueSinceMonth;
    }

    @JsonProperty(REWARD_VALUE_SINCE_DAYS)
    public Omissible<Integer> getRewardValueSinceDays() {
        return rewardValueSinceDays;
    }

    @JsonProperty(UNIQUE_FRIEND_REQUIRED)
    public Omissible<Boolean> getUniqueFriendRequired() {
        return uniqueFriendRequired;
    }

    @JsonProperty(REFERRAL_LOOP_ALLOWED)
    public Omissible<Boolean> isReferralLoopAllowed() {
        return referralLoopAllowed;
    }

    @JsonProperty(REWARD_SLOTS)
    public Omissible<Set<String>> getRewardSlots() {
        return rewardSlots;
    }

    @JsonProperty(MIN_CART_VALUE)
    public Omissible<BigDecimal> getMinCartValue() {
        return minCartValue;
    }

    @JsonProperty(RULE_ACTION_TYPE)
    public Omissible<RuleActionType> getRuleActionType() {
        return ruleActionType;
    }

    @JsonProperty(EMAIL_REQUIRED)
    public Omissible<Boolean> isEmailRequired() {
        return isEmailRequired;
    }

    @JsonProperty(DATA_ATTRIBUTE_NAME)
    public Omissible<String> getDataAttributeName() {
        return dataAttributeName;
    }

    @JsonProperty(DATA_ATTRIBUTE_VALUE)
    public Omissible<String> getDataAttributeValue() {
        return dataAttributeValue;
    }

    @JsonProperty(DATA_ATTRIBUTE_MATCHER_TYPE)
    public Omissible<RuleDataMatcherType> getDataAttributeMatcherType() {
        return dataAttributeMatcherType;
    }

    @JsonProperty(EXPRESSION)
    public Omissible<RewardRuleExpression> getExpression() {
        return rewardRuleExpression;
    }

    @JsonProperty(REWARD_EVERY_X_FRIEND_ACTIONS)
    public Omissible<Integer> getRewardEveryXFriendActions() {
        return rewardEveryXFriendActions;
    }

    @JsonProperty(COUNT_REWARDS_BASED_ON_PARTNER_USER_ID)
    public Omissible<Boolean> getCountRewardsBasedOnPartnerUserId() {
        return countRewardsBasedOnPartnerUserId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private Omissible<Rewardee> rewardee = Omissible.omitted();
        private String rewardSupplierId;
        private Omissible<Integer> referralsPerReward = Omissible.omitted();
        private Omissible<Integer> rewardCountLimit = Omissible.omitted();
        private Omissible<Integer> rewardCountSinceMonth = Omissible.omitted();
        private Omissible<Integer> rewardCountSinceDays = Omissible.omitted();
        private Omissible<BigDecimal> rewardValueLimit = Omissible.omitted();
        private Omissible<Integer> rewardValueSinceMonth = Omissible.omitted();
        private Omissible<Integer> rewardValueSinceDays = Omissible.omitted();
        private Omissible<Boolean> uniqueFriendRequired = Omissible.omitted();
        private Omissible<Boolean> referralLoopAllowed = Omissible.omitted();
        private Omissible<Set<String>> rewardSlot = Omissible.omitted();
        private Omissible<BigDecimal> minCartValue = Omissible.omitted();
        private Omissible<RuleActionType> ruleActionType = Omissible.omitted();
        private Omissible<Boolean> isEmailRequired = Omissible.omitted();
        private Omissible<String> dataAttributeName = Omissible.omitted();
        private Omissible<String> dataAttributeValue = Omissible.omitted();
        private Omissible<RuleDataMatcherType> dataAttributeMatcherType = Omissible.omitted();
        private Omissible<RewardRuleExpression> rewardRuleExpression = Omissible.omitted();
        private Omissible<Boolean> countRewardsBasedOnPartnerUserId = Omissible.omitted();
        private Omissible<Integer> rewardEveryXFriendActions = Omissible.omitted();

        private Builder() {
        }

        public Builder withRewardee(Rewardee rewardee) {
            this.rewardee = Omissible.of(rewardee);
            return this;
        }

        public Builder withRewardSupplierId(String rewardSupplierId) {
            this.rewardSupplierId = rewardSupplierId;
            return this;
        }

        public Builder withReferralsPerReward(Integer referralsPerReward) {
            this.referralsPerReward = Omissible.of(referralsPerReward);
            return this;
        }

        public Builder withRewardCountLimit(Integer rewardCountLimit) {
            this.rewardCountLimit = Omissible.of(rewardCountLimit);
            return this;
        }

        public Builder withRewardCountSinceMonth(Integer rewardCountSinceMonth) {
            this.rewardCountSinceMonth = Omissible.of(rewardCountSinceMonth);
            return this;
        }

        public Builder withRewardCountSinceDays(Integer rewardCountSinceDays) {
            this.rewardCountSinceDays = Omissible.of(rewardCountSinceDays);
            return this;
        }

        public Builder withRewardValueLimit(BigDecimal rewardValueLimit) {
            this.rewardValueLimit = Omissible.of(rewardValueLimit);
            return this;
        }

        public Builder withRewardValueSinceMonth(Integer rewardValueSinceMonth) {
            this.rewardValueSinceMonth = Omissible.of(rewardValueSinceMonth);
            return this;
        }

        public Builder withRewardValueSinceDays(Integer rewardValueSinceDays) {
            this.rewardValueSinceDays = Omissible.of(rewardValueSinceDays);
            return this;
        }

        public Builder withUniqueFriendRequired(Boolean uniqueFriendRequired) {
            this.uniqueFriendRequired = Omissible.of(uniqueFriendRequired);
            return this;
        }

        public Builder withReferralLoopAllowed(Boolean referralLoopAllowed) {
            this.referralLoopAllowed = Omissible.of(referralLoopAllowed);
            return this;
        }

        public Builder withRewardSlots(Set<String> rewardSlots) {
            this.rewardSlot = Omissible.of(rewardSlots);
            return this;
        }

        public Builder withMinCartValue(BigDecimal minCartValue) {
            this.minCartValue = Omissible.of(minCartValue);
            return this;
        }

        public Builder withRuleActionType(RuleActionType ruleActionType) {
            this.ruleActionType = Omissible.of(ruleActionType);
            return this;
        }

        public Builder withIsEmailRequired(Boolean isEmailRequired) {
            this.isEmailRequired = Omissible.of(isEmailRequired);
            return this;
        }

        public Builder withDataAttributeName(String dataAttributeName) {
            this.dataAttributeName = Omissible.of(dataAttributeName);
            return this;
        }

        public Builder withDataAttributeValue(String dataAttributeValue) {
            this.dataAttributeValue = Omissible.of(dataAttributeValue);
            return this;
        }

        public Builder withDataAttributeMatcherType(
            RuleDataMatcherType dataAttributeMatcherType) {
            this.dataAttributeMatcherType = Omissible.of(dataAttributeMatcherType);
            return this;
        }

        public Builder withExpression(RewardRuleExpression expression) {
            this.rewardRuleExpression = Omissible.of(expression);
            return this;
        }

        public Builder withCountRewardsBasedOnPartnerUserId(Boolean value) {
            this.countRewardsBasedOnPartnerUserId = Omissible.of(value);
            return this;
        }

        public Builder withRewardEveryXFriendActions(Integer rewardEveryXFriendActions) {
            this.rewardEveryXFriendActions = Omissible.of(rewardEveryXFriendActions);
            return this;
        }

        public RewardRuleCreateRequest build() {
            return new RewardRuleCreateRequest(rewardee, rewardSupplierId, referralsPerReward, rewardCountLimit,
                rewardCountSinceMonth, rewardCountSinceDays, rewardValueLimit, rewardValueSinceMonth,
                rewardValueSinceDays, uniqueFriendRequired, referralLoopAllowed, rewardSlot, minCartValue,
                ruleActionType, isEmailRequired, dataAttributeName, dataAttributeValue, dataAttributeMatcherType,
                rewardRuleExpression, rewardEveryXFriendActions, countRewardsBasedOnPartnerUserId);
        }
    }
}
