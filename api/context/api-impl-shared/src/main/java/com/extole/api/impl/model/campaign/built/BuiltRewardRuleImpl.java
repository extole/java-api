package com.extole.api.impl.model.campaign.built;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import com.extole.api.model.campaign.built.BuiltRewardRule;

public final class BuiltRewardRuleImpl implements BuiltRewardRule {
    private final com.extole.model.entity.campaign.built.BuiltRewardRule builtRewardRule;

    public BuiltRewardRuleImpl(com.extole.model.entity.campaign.built.BuiltRewardRule builtRewardRule) {
        this.builtRewardRule = builtRewardRule;
    }

    @Override
    public String getId() {
        return builtRewardRule.getId().getValue();
    }

    @Override
    public String getRewardSupplierId() {
        return builtRewardRule.getRewardSupplierId().getValue();
    }

    @Override
    public String getRewardee() {
        return builtRewardRule.getRewardee().name();
    }

    @Override
    public Integer getReferralsPerReward() {
        return builtRewardRule.getReferralsPerReward();
    }

    @Override
    public BigDecimal getMinCartValue() {
        return builtRewardRule.getMinCartValue();
    }

    @Override
    public String getRuleActionType() {
        return builtRewardRule.getRuleActionType().name();
    }

    @Override
    public String[] getRewardSlots() {
        return builtRewardRule.getRewardSlots().toArray(String[]::new);
    }

    @Override
    public Integer getRewardCountLimit() {
        return builtRewardRule.getRewardCountLimit();
    }

    @Override
    public Integer getRewardCountSinceMonth() {
        return builtRewardRule.getRewardCountSinceMonth();
    }

    @Override
    public Integer getRewardCountSinceDays() {
        return builtRewardRule.getRewardCountSinceDays();
    }

    @Override
    public BigDecimal getRewardValueLimit() {
        return builtRewardRule.getRewardValueLimit();
    }

    @Override
    public Integer getRewardValueSinceMonth() {
        return builtRewardRule.getRewardValueSinceMonth();
    }

    @Override
    public Integer getRewardValueSinceDays() {
        return builtRewardRule.getRewardValueSinceDays();
    }

    @Override
    public Integer getRewardEveryXFriendActions() {
        return builtRewardRule.getRewardEveryXFriendActions();
    }

    @Override
    public boolean isUniqueFriendRequired() {
        return builtRewardRule.isUniqueFriendRequired();
    }

    @Override
    public boolean isReferralLoopAllowed() {
        return builtRewardRule.isReferralLoopAllowed();
    }

    @Override
    public boolean isEmailRequired() {
        return builtRewardRule.isEmailRequired();
    }

    @Override
    public String getDataAttributeName() {
        return builtRewardRule.getDataAttributeName();
    }

    @Override
    public String getDataAttributeValue() {
        return builtRewardRule.getDataAttributeValue();
    }

    @Nullable
    @Override
    public String getExpression() {
        return builtRewardRule.getExpression().map(value -> value.getValue()).orElse(null);
    }

    @Override
    public String getDataAttributeMatcherType() {
        return builtRewardRule.getDataAttributeMatcherType().name();
    }

    @Override
    public boolean isRewardCountingBasedOnPartnerUserId() {
        return builtRewardRule.isRewardCountingBasedOnPartnerUserId();
    }

    @Override
    public String getCreatedDate() {
        return builtRewardRule.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtRewardRule.getUpdatedDate().toString();
    }
}
