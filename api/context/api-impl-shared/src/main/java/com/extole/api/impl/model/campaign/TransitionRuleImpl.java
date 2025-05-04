package com.extole.api.impl.model.campaign;

import com.extole.api.model.campaign.TransitionRule;

public final class TransitionRuleImpl implements TransitionRule {
    private final com.extole.model.entity.campaign.TransitionRule transitionRule;

    public TransitionRuleImpl(com.extole.model.entity.campaign.TransitionRule transitionRule) {
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
    public long getTransitionPeriodMilliseconds() {
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
