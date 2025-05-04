package com.extole.client.rest.campaign.incentive;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.incentive.quality.rule.QualityRuleResponse;
import com.extole.client.rest.campaign.incentive.reward.rule.RewardRuleResponse;
import com.extole.client.rest.campaign.incentive.transition.rule.TransitionRuleResponse;
import com.extole.common.lang.ToString;

public class IncentiveResponse {

    private static final String ID = "id";
    private static final String QUALITY_RULES = "quality_rules";
    private static final String REWARD_RULES = "reward_rules";
    private static final String TRANSITION_RULES = "transition_rules";

    private final String id;
    private final List<QualityRuleResponse> qualityRuleResponses;
    private final List<RewardRuleResponse> rewardRuleResponses;
    private final List<TransitionRuleResponse> transitionRuleResponses;

    public IncentiveResponse(@JsonProperty(ID) String id,
        @JsonProperty(QUALITY_RULES) List<QualityRuleResponse> qualityRuleResponses,
        @JsonProperty(REWARD_RULES) List<RewardRuleResponse> rewardRuleResponses,
        @JsonProperty(TRANSITION_RULES) List<TransitionRuleResponse> transitionRuleResponses) {
        this.id = id;
        this.qualityRuleResponses = qualityRuleResponses;
        this.rewardRuleResponses = rewardRuleResponses;
        this.transitionRuleResponses = transitionRuleResponses;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(QUALITY_RULES)
    public List<QualityRuleResponse> getQualityRules() {
        return qualityRuleResponses;
    }

    @JsonProperty(REWARD_RULES)
    public List<RewardRuleResponse> getRewardRules() {
        return rewardRuleResponses;
    }

    @JsonProperty(TRANSITION_RULES)
    public List<TransitionRuleResponse> getTransitionRules() {
        return transitionRuleResponses;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
