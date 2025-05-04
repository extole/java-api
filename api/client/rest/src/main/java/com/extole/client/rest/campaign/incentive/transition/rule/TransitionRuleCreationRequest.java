package com.extole.client.rest.campaign.incentive.transition.rule;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.common.lang.ToString;

public class TransitionRuleCreationRequest {

    public static final String ACTION_TYPE = "action_type";
    public static final String APPROVE_LOW_QUALITY = "approve_low_quality";
    public static final String APPROVE_HIGH_QUALITY = "approve_high_quality";
    public static final String TRANSITION_PERIOD_MILLISECONDS = "transition_period_milliseconds";

    private final RuleActionType actionType;
    private final Boolean approveLowQuality;
    private final Boolean approveHighQuality;
    private final Long transitionPeriodMilliseconds;

    public TransitionRuleCreationRequest(@JsonProperty(ACTION_TYPE) RuleActionType actionType,
        @Nullable @JsonProperty(APPROVE_LOW_QUALITY) Boolean approveLowQuality,
        @Nullable @JsonProperty(APPROVE_HIGH_QUALITY) Boolean approveHighQuality,
        @JsonProperty(TRANSITION_PERIOD_MILLISECONDS) Long transitionPeriodMilliseconds) {
        this.actionType = actionType;
        this.approveLowQuality = approveLowQuality;
        this.approveHighQuality = approveHighQuality;
        this.transitionPeriodMilliseconds = transitionPeriodMilliseconds;
    }

    @JsonProperty(ACTION_TYPE)
    public RuleActionType getActionType() {
        return actionType;
    }

    @Nullable
    @JsonProperty(APPROVE_LOW_QUALITY)
    public Boolean getApproveLowQuality() {
        return approveLowQuality;
    }

    @Nullable
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private RuleActionType actionType;
        private Boolean approveLowQuality;
        private Boolean approveHighQuality;
        private Long transitionPeriodMilliseconds;

        private Builder() {
        }

        public Builder withActionType(RuleActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public Builder withApproveLowQuality(Boolean approveLowQuality) {
            this.approveLowQuality = approveLowQuality;
            return this;
        }

        public Builder withApproveHighQuality(Boolean approveHighQuality) {
            this.approveHighQuality = approveHighQuality;
            return this;
        }

        public Builder withTransitionPeriodMilliseconds(Long transitionPeriodMilliseconds) {
            this.transitionPeriodMilliseconds = transitionPeriodMilliseconds;
            return this;
        }

        public TransitionRuleCreationRequest build() {
            return new TransitionRuleCreationRequest(actionType,
                approveLowQuality,
                approveHighQuality,
                transitionPeriodMilliseconds);
        }
    }
}
