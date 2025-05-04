package com.extole.client.rest.campaign.incentive.quality.rule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.incentive.RuleActionType;
import com.extole.common.lang.ToString;

public class QualityRuleRequest {
    private final Boolean enabled;
    private final Set<RuleActionType> actionTypes;
    private final Map<String, List<String>> properties;

    public QualityRuleRequest(@Nullable @JsonProperty("enabled") Boolean enabled,
        @Nullable @JsonProperty("action_types") Set<RuleActionType> actionTypes,
        @Nullable @JsonProperty("properties") Map<String, List<String>> properties) {
        this.enabled = enabled;
        this.actionTypes = actionTypes;
        this.properties = properties;
    }

    @JsonProperty("enabled")
    public Boolean getEnabled() {
        return enabled;
    }

    @JsonProperty("action_types")
    public Set<RuleActionType> getActionTypes() {
        return actionTypes;
    }

    @JsonProperty("properties")
    public Map<String, List<String>> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Boolean enabled;
        private Set<RuleActionType> actionTypes;
        private Map<String, List<String>> properties;

        private Builder() {
        }

        public Builder withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withActionTypes(Set<RuleActionType> actionTypes) {
            this.actionTypes = ImmutableSet.copyOf(actionTypes);
            return this;
        }

        public Builder withProperties(Map<String, List<String>> properties) {
            this.properties = ImmutableMap.copyOf(properties);
            return this;
        }

        public QualityRuleRequest build() {
            return new QualityRuleRequest(enabled, actionTypes, properties);
        }
    }
}
