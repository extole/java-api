package com.extole.api.impl.model.campaign;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.RewardRule;

public final class RewardRuleImpl implements RewardRule {
    private final com.extole.model.entity.campaign.RewardRule rewardRule;

    public RewardRuleImpl(com.extole.model.entity.campaign.RewardRule rewardRule) {
        this.rewardRule = rewardRule;
    }

    @Override
    public String getId() {
        return rewardRule.getId().getValue();
    }

    @Override
    public String getRewardSupplierId() {
        return rewardRule.getRewardSupplierId().getValue();
    }

    @Override
    public String getRewardee() {
        return rewardRule.getRewardee().name();
    }

    @Override
    public int getReferralsPerReward() {
        return rewardRule.getReferralsPerReward();
    }

    @Override
    public BigDecimal getMinCartValue() {
        return rewardRule.getMinCartValue();
    }

    @Override
    public String getRuleActionType() {
        return rewardRule.getRuleActionType().name();
    }

    @Override
    public String[] getRewardSlots() {
        return rewardRule.getRewardSlots().toArray(String[]::new);
    }

    @Override
    public int getRewardCountLimit() {
        return rewardRule.getRewardCountLimit().intValue();
    }

    @Override
    public int getRewardCountSinceMonth() {
        return rewardRule.getRewardCountSinceMonth().intValue();
    }

    @Override
    public int getRewardCountSinceDays() {
        return rewardRule.getRewardCountSinceDays();
    }

    @Override
    public BigDecimal getRewardValueLimit() {
        return rewardRule.getRewardValueLimit();
    }

    @Override
    public int getRewardValueSinceMonth() {
        return rewardRule.getRewardValueSinceMonth().intValue();
    }

    @Override
    public int getRewardValueSinceDays() {
        return rewardRule.getRewardValueSinceDays().intValue();
    }

    @Override
    public int getRewardEveryXFriendActions() {
        return rewardRule.getRewardEveryXFriendActions().intValue();
    }

    @Override
    public boolean isUniqueFriendRequired() {
        return rewardRule.isUniqueFriendRequired();
    }

    @Override
    public boolean isReferralLoopAllowed() {
        return rewardRule.isReferralLoopAllowed();
    }

    @Override
    public boolean isEmailRequired() {
        return rewardRule.isEmailRequired();
    }

    @Override
    public String getDataAttributeName() {
        return rewardRule.getDataAttributeName();
    }

    @Override
    public String getDataAttributeValue() {
        return rewardRule.getDataAttributeValue();
    }

    @Nullable
    @Override
    public String getExpression() {
        return rewardRule.getExpression().map(value -> value.getValue()).orElse(null);
    }

    @Override
    public String getDataAttributeMatcherType() {
        return rewardRule.getDataAttributeMatcherType().name();
    }

    @Override
    public boolean isRewardCountingBasedOnPartnerUserId() {
        return rewardRule.isRewardCountingBasedOnPartnerUserId();
    }

    @Override
    public String getCreatedDate() {
        return rewardRule.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return rewardRule.getUpdatedDate().toString();
    }
}
