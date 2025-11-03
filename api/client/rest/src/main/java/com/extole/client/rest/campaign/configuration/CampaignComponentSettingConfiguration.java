package com.extole.client.rest.campaign.configuration;

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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    defaultImpl = CampaignComponentVariableConfiguration.class,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CampaignComponentClientKeyFlowVariableConfiguration.class,
        name = CampaignComponentClientKeyFlowVariableConfiguration.SETTING_TYPE_CLIENT_KEY_FLOW),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumListVariableConfiguration.class,
        name = CampaignComponentPartnerEnumListVariableConfiguration.SETTING_TYPE_PARTNER),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumVariableConfiguration.class,
        name = CampaignComponentPartnerEnumVariableConfiguration.SETTING_TYPE_PARTNER),
    @JsonSubTypes.Type(value = CampaignComponentEnumVariableConfiguration.class,
        name = CampaignComponentEnumVariableConfiguration.SETTING_TYPE_ENUM),
    @JsonSubTypes.Type(value = CampaignComponentEnumListVariableConfiguration.class,
        name = CampaignComponentEnumListVariableConfiguration.SETTING_TYPE_ENUM_LIST),
    @JsonSubTypes.Type(value = CampaignComponentSocketConfiguration.class,
        name = CampaignComponentSocketConfiguration.MULTI_SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketConfiguration.class,
        name = CampaignComponentSocketConfiguration.SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentRewardSupplierIdListVariableConfiguration.class,
        name = CampaignComponentRewardSupplierIdListVariableConfiguration.SETTING_TYPE_REWARD_SUPPLIER_ID_LIST),
    @JsonSubTypes.Type(value = CampaignComponentComponentIdVariableConfiguration.class,
        name = CampaignComponentComponentIdVariableConfiguration.SETTING_TYPE_COMPONENT_REFERENCE),
})
public class CampaignComponentSettingConfiguration {

    protected static final String JSON_COMPONENT_SETTING_NAME = "name";
    protected static final String JSON_COMPONENT_SETTING_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_SETTING_TYPE = "type";
    protected static final String JSON_COMPONENT_SETTING_TAGS = "tags";
    protected static final String JSON_COMPONENT_SETTING_PRIORITY = "priority";

    private final String name;
    private final Optional<String> displayName;
    private final SettingType type;
    private final Set<String> tags;
    private final DeweyDecimal priority;

    @JsonCreator
    public CampaignComponentSettingConfiguration(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) DeweyDecimal priority) {
        this.name = name;
        this.displayName = displayName;
        this.type = type == null ? SettingType.STRING : type;
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
