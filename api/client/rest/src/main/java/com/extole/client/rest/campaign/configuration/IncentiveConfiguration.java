package com.extole.client.rest.campaign.configuration;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class IncentiveConfiguration {

    private static final String ID = "id";
    private static final String QUALITY_RULES = "quality_rules";
    private static final String REWARD_RULES = "reward_rules";
    private static final String TRANSITION_RULES = "transition_rules";

    private final String id;
    private final List<QualityRuleConfiguration> qualityRuleConfigurations;
    private final List<RewardRuleConfiguration> rewardRuleConfigurations;
    private final List<TransitionRuleConfiguration> transitionRuleConfigurations;

    public IncentiveConfiguration(@JsonProperty(ID) String id,
        @JsonProperty(QUALITY_RULES) List<QualityRuleConfiguration> qualityRuleConfigurations,
        @JsonProperty(REWARD_RULES) List<RewardRuleConfiguration> rewardRuleConfigurations,
        @JsonProperty(TRANSITION_RULES) List<TransitionRuleConfiguration> transitionRuleConfigurations) {
        this.id = id;
        this.qualityRuleConfigurations =
            qualityRuleConfigurations != null ? qualityRuleConfigurations : Collections.emptyList();
        this.rewardRuleConfigurations =
            rewardRuleConfigurations != null ? rewardRuleConfigurations : Collections.emptyList();
        this.transitionRuleConfigurations =
            transitionRuleConfigurations != null ? transitionRuleConfigurations : Collections.emptyList();
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @JsonProperty(QUALITY_RULES)
    public List<QualityRuleConfiguration> getQualityRules() {
        return qualityRuleConfigurations;
    }

    @JsonProperty(REWARD_RULES)
    public List<RewardRuleConfiguration> getRewardRules() {
        return rewardRuleConfigurations;
    }

    @JsonProperty(TRANSITION_RULES)
    public List<TransitionRuleConfiguration> getTransitionRules() {
        return transitionRuleConfigurations;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
