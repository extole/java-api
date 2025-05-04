package com.extole.client.rest.creative.batch;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.creative.CreativeVariableScope;
import com.extole.common.rest.omissible.Omissible;

public final class ZoneCreativeVariableUpdateRequest {

    private static final String JSON_ZONE_NAME = "zone_name";
    private static final String JSON_NAME = "name";
    private static final String JSON_JOURNEY_NAMES = "journey_names";
    private static final String JSON_VALUES = "values";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VISIBLE = "visible";
    private static final String JSON_CREATIVE_ACTION_ID = "creative_action_id";

    private final Omissible<String> zoneName;
    private final String name;
    private final Omissible<Set<String>> journeyNames;
    private final Omissible<Map<String, String>> values;
    private final Omissible<CreativeVariableScope> scope;
    private final Omissible<Boolean> visible;
    private final Omissible<String> creativeActionId;

    /**
     * Either creativeActionId or zoneName is required
     */
    @JsonCreator
    private ZoneCreativeVariableUpdateRequest(
        @JsonProperty(JSON_ZONE_NAME) Omissible<String> zoneName,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_JOURNEY_NAMES) Omissible<Set<String>> journeyNames,
        @JsonProperty(JSON_SCOPE) Omissible<CreativeVariableScope> scope,
        @JsonProperty(JSON_VISIBLE) Omissible<Boolean> visible,
        @JsonProperty(JSON_VALUES) Omissible<Map<String, String>> values,
        @JsonProperty(JSON_CREATIVE_ACTION_ID) Omissible<String> creativeActionId) {
        this.zoneName = zoneName;
        this.name = name;
        this.journeyNames = journeyNames;
        this.scope = scope;
        this.visible = visible;
        this.values = values;
        this.creativeActionId = creativeActionId;
    }

    @JsonProperty(JSON_ZONE_NAME)
    public Omissible<String> getZoneName() {
        return zoneName;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_JOURNEY_NAMES)
    public Omissible<Set<String>> getJourneyNames() {
        return journeyNames;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<CreativeVariableScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_VALUES)
    public Omissible<Map<String, String>> getValues() {
        return values;
    }

    @JsonProperty(JSON_VISIBLE)
    public Omissible<Boolean> getVisible() {
        return visible;
    }

    @JsonProperty(JSON_CREATIVE_ACTION_ID)
    public Omissible<String> getCreativeActionId() {
        return creativeActionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<String> zoneName = Omissible.omitted();
        private String name;
        private Omissible<Set<String>> journeyNames = Omissible.omitted();
        private Omissible<CreativeVariableScope> scope = Omissible.omitted();
        private Omissible<Map<String, String>> values = Omissible.omitted();
        private Omissible<Boolean> visible = Omissible.omitted();
        private Omissible<String> creativeActionId = Omissible.omitted();

        private Builder() {
        }

        public Builder withZoneName(String zoneName) {
            this.zoneName = Omissible.of(zoneName);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withJourneyNames(Set<String> journeyNames) {
            this.journeyNames = Omissible.of(journeyNames);
            return this;
        }

        public Builder withValues(Map<String, String> values) {
            this.values = Omissible.of(values);
            return this;
        }

        public Builder withScope(CreativeVariableScope scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder withVisible(boolean visible) {
            this.visible = Omissible.of(Boolean.valueOf(visible));
            return this;
        }

        public Builder withCreativeActionId(String creativeActionId) {
            this.creativeActionId = Omissible.of(creativeActionId);
            return this;
        }

        public ZoneCreativeVariableUpdateRequest build() {
            return new ZoneCreativeVariableUpdateRequest(
                zoneName,
                name,
                journeyNames,
                scope,
                visible,
                values,
                creativeActionId);
        }

    }

}
