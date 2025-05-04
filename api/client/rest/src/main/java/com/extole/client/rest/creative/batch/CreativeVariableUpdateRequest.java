package com.extole.client.rest.creative.batch;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.creative.CreativeVariableScope;
import com.extole.common.rest.omissible.Omissible;

public final class CreativeVariableUpdateRequest {

    private static final String JSON_CREATIVE_ARCHIVE_ID = "creative_archive_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_VALUES = "values";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_VISIBLE = "visible";
    private static final String JSON_CREATIVE_ACTION_ID = "creative_action_id";

    private final Omissible<String> creativeArchiveId;
    private final String name;
    private final Omissible<Map<String, String>> values;
    private final Omissible<CreativeVariableScope> scope;
    private final Omissible<Boolean> visible;
    private final Omissible<String> creativeActionId;

    /**
     * Either creativeActionId or creativeArchiveId is required
     */
    @JsonCreator
    private CreativeVariableUpdateRequest(
        @JsonProperty(JSON_CREATIVE_ARCHIVE_ID) Omissible<String> creativeArchiveId,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_SCOPE) Omissible<CreativeVariableScope> scope,
        @JsonProperty(JSON_VISIBLE) Omissible<Boolean> visible,
        @JsonProperty(JSON_VALUES) Omissible<Map<String, String>> values,
        @JsonProperty(JSON_CREATIVE_ACTION_ID) Omissible<String> creativeActionId) {
        this.creativeArchiveId = creativeArchiveId;
        this.name = name;
        this.scope = scope;
        this.visible = visible;
        this.values = values;
        this.creativeActionId = creativeActionId;
    }

    @JsonProperty(JSON_CREATIVE_ARCHIVE_ID)
    public Omissible<String> getCreativeArchiveId() {
        return creativeArchiveId;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
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

        private Omissible<String> creativeArchiveId = Omissible.omitted();
        private String name;
        private Omissible<CreativeVariableScope> scope = Omissible.omitted();
        private Omissible<Map<String, String>> values = Omissible.omitted();
        private Omissible<Boolean> visible = Omissible.omitted();
        private Omissible<String> creativeActionId = Omissible.omitted();

        private Builder() {
        }

        public Builder withCreativeArchiveId(String creativeArchiveId) {
            this.creativeArchiveId = Omissible.of(creativeArchiveId);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
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

        public CreativeVariableUpdateRequest build() {
            return new CreativeVariableUpdateRequest(
                creativeArchiveId,
                name,
                scope,
                visible,
                values,
                creativeActionId);
        }

    }

}
