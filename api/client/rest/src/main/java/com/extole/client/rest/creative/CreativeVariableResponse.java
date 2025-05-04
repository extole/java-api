package com.extole.client.rest.creative;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.common.lang.ToString;

public final class CreativeVariableResponse {
    private static final String JSON_NAME = "name";
    private static final String JSON_LABEL = "label";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_DEFAULT_SCOPE = "default_scope";
    private static final String JSON_TYPE = "type";
    private static final String JSON_TAGS = "tags";
    private static final String JSON_VALUES = "values";
    private static final String JSON_VISIBLE = "visible";
    private static final String JSON_CREATIVE_ACTION_ID = "creative_action_id";

    @Schema
    public enum Type {
        TEXT, SHORT_TEXT, COLOR, IMAGE, SWITCH, RUNTIME
    }

    private final String name;
    private final String label;
    private final CreativeVariableScope scope;
    private final CreativeVariableScope defaultScope;
    private final Type type;
    private final String[] tags;
    private final Map<String, String> values;
    private final Boolean visible;
    private final String creativeActionId;

    @JsonCreator
    public CreativeVariableResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_LABEL) String label,
        @JsonProperty(JSON_SCOPE) CreativeVariableScope scope,
        @JsonProperty(JSON_DEFAULT_SCOPE) CreativeVariableScope defaultScope,
        @JsonProperty(JSON_TYPE) Type type,
        @JsonProperty(JSON_TAGS) String[] tags,
        @JsonProperty(JSON_VALUES) Map<String, String> values,
        @JsonProperty(JSON_VISIBLE) Boolean visible,
        @JsonProperty(JSON_CREATIVE_ACTION_ID) String creativeActionId) {
        this.name = name;
        this.label = label;
        this.scope = scope;
        this.defaultScope = defaultScope;
        this.type = type;
        this.tags = tags;
        this.values = immutableOrEmptyMap(values);
        this.visible = visible;
        this.creativeActionId = creativeActionId;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_SCOPE)
    public CreativeVariableScope getScope() {
        return scope;
    }

    @JsonProperty(JSON_DEFAULT_SCOPE)
    public CreativeVariableScope getDefaultScope() {
        return defaultScope;
    }

    @JsonProperty(JSON_TYPE)
    public Type getType() {
        return type;
    }

    @JsonProperty(JSON_TAGS)
    public String[] getTags() {
        return tags;
    }

    @JsonProperty(JSON_VALUES)
    public Map<String, String> getValues() {
        return values;
    }

    @JsonProperty(JSON_VISIBLE)
    public Boolean isVisible() {
        return visible;
    }

    @JsonProperty(JSON_CREATIVE_ACTION_ID)
    public String getCreativeActionId() {
        return creativeActionId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    private static ImmutableMap<String, String> immutableOrEmptyMap(Map<String, String> values) {
        return values != null ? ImmutableMap.copyOf(values) : ImmutableMap.of();
    }
}
