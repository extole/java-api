package com.extole.client.rest.campaign.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class QualityRuleConfiguration {

    private final Omissible<Id<QualityRuleConfiguration>> id;
    private final Boolean enabled;
    private final QualityRuleType ruleType;
    private final Set<RuleActionType> actionTypes;
    private final Map<String, List<String>> properties;

    public QualityRuleConfiguration(@JsonProperty("id") Omissible<Id<QualityRuleConfiguration>> id,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("rule_type") QualityRuleType ruleType,
        @JsonProperty("action_types") Set<RuleActionType> actionTypes,
        @JsonProperty("properties") Map<String, List<String>> properties) {
        this.id = id;
        this.ruleType = ruleType;
        this.actionTypes = actionTypes != null ? actionTypes : Collections.emptySet();
        this.enabled = enabled;
        this.properties = properties != null ? properties : Collections.emptyMap();
    }

    @JsonProperty("id")
    public Omissible<Id<QualityRuleConfiguration>> getId() {
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
