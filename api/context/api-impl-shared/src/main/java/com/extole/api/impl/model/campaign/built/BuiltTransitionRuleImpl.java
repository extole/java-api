package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltTransitionRule;

public final class BuiltTransitionRuleImpl implements BuiltTransitionRule {
    private final com.extole.model.entity.campaign.built.BuiltTransitionRule transitionRule;

    public BuiltTransitionRuleImpl(com.extole.model.entity.campaign.built.BuiltTransitionRule transitionRule) {
        this.transitionRule = transitionRule;
    }

    @Override
    public String getId() {
        return transitionRule.getId().getValue();
    }

    @Override
    public String getActionType() {
        return transitionRule.getActionType().name();
    }

    @Override
    public boolean getApproveLowQuality() {
        return transitionRule.getApproveLowQuality();
    }

    @Override
    public boolean getApproveHighQuality() {
        return transitionRule.getApproveHighQuality();
    }

    @Override
    public long getTransitionPeriodInMilliseconds() {
        return transitionRule.getTransitionPeriod().toMillis();
    }

    @Override
    public String getCreatedDate() {
        return transitionRule.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return transitionRule.getUpdatedDate().toString();
    }
}
