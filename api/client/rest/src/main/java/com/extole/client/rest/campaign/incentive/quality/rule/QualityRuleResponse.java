package com.extole.client.rest.campaign.incentive.quality.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.common.lang.ToString;

public class QualityRuleResponse {
    private final String id;
    private final Boolean enabled;
    private final QualityRuleType ruleType;
    private final Set<RuleActionType> actionTypes;
    private Map<String, List<String>> properties = new HashMap<>();

    public QualityRuleResponse(@JsonProperty("id") String id,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("rule_type") QualityRuleType ruleType,
        @JsonProperty("action_types") Set<RuleActionType> actionTypes,
        @JsonProperty("properties") Map<String, List<String>> properties) {
        this.id = id;
        this.ruleType = ruleType;
        this.actionTypes = actionTypes;
        this.enabled = enabled;
        this.properties = properties;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("rule_type")
    public QualityRuleType getRuleType() {
        return ruleType;
    }

    @JsonProperty("action_types")
    public Set<RuleActionType> getActionTypes() {
        return actionTypes;
    }

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty("properties")
    public Map<String, List<String>> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
