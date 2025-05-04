package com.extole.reporting.rest.posthandler;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.action.ReportPostHandlerActionRequest;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionRequest;

public class ReportPostHandlerRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_ACTIONS = "actions";
    private static final String JSON_CONDITIONS = "conditions";

    private final String name;
    private final List<ReportPostHandlerActionRequest> actions;
    private final List<ReportPostHandlerConditionRequest> conditions;
    private Boolean enabled;

    @JsonCreator
    public ReportPostHandlerRequest(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_ACTIONS) List<ReportPostHandlerActionRequest> actions,
        @JsonProperty(JSON_CONDITIONS) List<ReportPostHandlerConditionRequest> conditions) {
        this.name = name;
        this.enabled = enabled;
        this.actions = actions != null ? ImmutableList.copyOf(actions) : null;
        this.conditions = conditions != null ? ImmutableList.copyOf(conditions) : null;
    }

    public List<ReportPostHandlerActionRequest> getActions() {
        return actions;
    }

    public List<ReportPostHandlerConditionRequest> getConditions() {
        return conditions;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_ENABLED)
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private Boolean enabled;
        private List<ReportPostHandlerActionRequest> actions;
        private List<ReportPostHandlerConditionRequest> conditions;

        private Builder() {
        }

        public Builder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withActions(List<ReportPostHandlerActionRequest> actions) {
            this.actions = actions == null ? null : ImmutableList.copyOf(actions);
            return this;
        }

        public Builder withConditions(List<ReportPostHandlerConditionRequest> conditions) {
            this.conditions = conditions == null ? null : ImmutableList.copyOf(conditions);
            return this;
        }

        public ReportPostHandlerRequest build() {
            return new ReportPostHandlerRequest(name, enabled, actions, conditions);
        }
    }
}
