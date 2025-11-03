package com.extole.client.rest.campaign.built.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.RuntimeEvaluatable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = BuiltCampaignComponentVariableResponse.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BuiltComponentClientKeyFlowVariableResponse.class,
        name = BuiltComponentClientKeyFlowVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentPartnerEnumListVariableResponse.class,
        name = BuiltComponentPartnerEnumListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentPartnerEnumVariableResponse.class,
        name = BuiltComponentPartnerEnumVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentEnumVariableResponse.class,
        name = BuiltComponentEnumVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentEnumListVariableResponse.class,
        name = BuiltComponentEnumListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentSocketResponse.class,
        name = BuiltComponentSocketResponse.MULTI_SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentSocketResponse.class,
        name = BuiltComponentSocketResponse.SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentRewardSupplierIdListVariableResponse.class,
        name = BuiltComponentRewardSupplierIdListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = BuiltComponentComponentIdVariableResponse.class,
        name = BuiltComponentComponentIdVariableResponse.SETTING_TYPE),
})
public class BuiltCampaignComponentSettingResponse {

    protected static final String JSON_COMPONENT_SETTING_NAME = "name";
    protected static final String JSON_COMPONENT_SETTING_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_SETTING_TYPE = "type";
    protected static final String JSON_COMPONENT_SETTING_VALUES = "values";
    protected static final String JSON_COMPONENT_SETTING_TAGS = "tags";
    protected static final String JSON_COMPONENT_SETTING_PRIORITY = "priority";

    private final String name;
    private final Optional<String> displayName;
    private final SettingType type;
    private final Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values;
    private final Set<String> tags;
    private final DeweyDecimal priority;

    @JsonCreator
    public BuiltCampaignComponentSettingResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_VALUES) Map<String, RuntimeEvaluatable<Object, Optional<Object>>> values,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.values = values;
        this.tags = tags == null ? ImmutableSet.of() : ImmutableSet.copyOf(tags);
        this.priority = priority;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_TYPE)
    public SettingType getType() {
        return type;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_VALUES)
    public Map<String, RuntimeEvaluatable<Object, Optional<Object>>> getValues() {
        return values;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY)
    public DeweyDecimal getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
