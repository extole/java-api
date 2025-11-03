package com.extole.client.rest.campaign.component.setting;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    defaultImpl = CampaignComponentVariableResponse.class,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CampaignComponentClientKeyFlowVariableResponse.class,
        name = CampaignComponentClientKeyFlowVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumListVariableResponse.class,
        name = CampaignComponentPartnerEnumListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumVariableResponse.class,
        name = CampaignComponentPartnerEnumVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumVariableResponse.class,
        name = CampaignComponentEnumVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumListVariableResponse.class,
        name = CampaignComponentEnumListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketResponse.class,
        name = CampaignComponentSocketResponse.MULTI_SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketResponse.class,
        name = CampaignComponentSocketResponse.SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentRewardSupplierIdListVariableResponse.class,
        name = CampaignComponentRewardSupplierIdListVariableResponse.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentComponentIdVariableResponse.class,
        name = CampaignComponentComponentIdVariableResponse.SETTING_TYPE),
})
public class CampaignComponentSettingResponse {

    protected static final String JSON_COMPONENT_SETTING_NAME = "name";
    protected static final String JSON_COMPONENT_SETTING_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_SETTING_TYPE = "type";
    protected static final String JSON_COMPONENT_SETTING_DESCRIPTION = "description";
    protected static final String JSON_COMPONENT_SETTING_TAGS = "tags";
    protected static final String JSON_COMPONENT_SETTING_PRIORITY = "priority";

    private final String name;
    private final Optional<String> displayName;
    private final SettingType type;
    private final BuildtimeEvaluatable<? extends CampaignBuildtimeContext, Optional<String>> description;
    private final Set<String> tags;
    private final DeweyDecimal priority;

    @JsonCreator
    public CampaignComponentSettingResponse(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION) BuildtimeEvaluatable<? extends CampaignBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
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

    @JsonProperty(JSON_COMPONENT_SETTING_DESCRIPTION)
    public BuildtimeEvaluatable<? extends CampaignBuildtimeContext, Optional<String>> getDescription() {
        return description;
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
