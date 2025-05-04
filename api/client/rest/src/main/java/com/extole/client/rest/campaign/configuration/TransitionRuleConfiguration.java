package com.extole.client.rest.campaign.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class TransitionRuleConfiguration {

    public static final String TRANSITION_RULE_ID = "transition_rule_id";
    public static final String ACTION_TYPE = "action_type";
    public static final String APPROVE_LOW_QUALITY = "approve_low_quality";
    public static final String APPROVE_HIGH_QUALITY = "approve_high_quality";
    public static final String TRANSITION_PERIOD_MILLISECONDS = "transition_period_milliseconds";

    private final Omissible<Id<TransitionRuleConfiguration>> transitionRuleId;
    private final RuleActionType actionType;
    private final Boolean approveLowQuality;
    private final Boolean approveHighQuality;
    private final Long transitionPeriodMilliseconds;

    public TransitionRuleConfiguration(
        @JsonProperty(TRANSITION_RULE_ID) Omissible<Id<TransitionRuleConfiguration>> transitionRuleId,
        @JsonProperty(ACTION_TYPE) RuleActionType actionType,
        @JsonProperty(APPROVE_LOW_QUALITY) Boolean approveLowQuality,
        @JsonProperty(APPROVE_HIGH_QUALITY) Boolean approveHighQuality,
        @JsonProperty(TRANSITION_PERIOD_MILLISECONDS) Long transitionPeriodMilliseconds) {
        this.transitionRuleId = transitionRuleId;
        this.actionType = actionType;
        this.approveLowQuality = approveLowQuality;
        this.approveHighQuality = approveHighQuality;
        this.transitionPeriodMilliseconds = transitionPeriodMilliseconds;
    }

    @JsonProperty(TRANSITION_RULE_ID)
    public Omissible<Id<TransitionRuleConfiguration>> getTransitionRuleId() {
        return transitionRuleId;
    }

    @JsonProperty(ACTION_TYPE)
    public RuleActionType getActionType() {
        return actionType;
    }

    @JsonProperty(APPROVE_LOW_QUALITY)
    public Boolean getApproveLowQuality() {
        return approveLowQuality;
    }

    @JsonProperty(APPROVE_HIGH_QUALITY)
    public Boolean getApproveHighQuality() {
        return approveHighQuality;
    }

    @JsonProperty(TRANSITION_PERIOD_MILLISECONDS)
    public Long getTransitionPeriodMilliseconds() {
        return transitionPeriodMilliseconds;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
