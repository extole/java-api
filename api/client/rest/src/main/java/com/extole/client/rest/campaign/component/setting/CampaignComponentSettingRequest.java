package com.extole.client.rest.campaign.component.setting;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    defaultImpl = CampaignComponentVariableRequest.class,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CampaignComponentClientKeyFlowVariableRequest.class,
        name = CampaignComponentClientKeyFlowVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumListVariableRequest.class,
        name = CampaignComponentPartnerEnumListVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumVariableRequest.class,
        name = CampaignComponentPartnerEnumVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumVariableRequest.class,
        name = CampaignComponentEnumVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumListVariableRequest.class,
        name = CampaignComponentEnumListVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketRequest.class,
        name = CampaignComponentSocketRequest.MULTI_SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketRequest.class,
        name = CampaignComponentSocketRequest.SOCKET_SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentRewardSupplierIdListVariableRequest.class,
        name = CampaignComponentRewardSupplierIdListVariableRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentComponentIdVariableRequest.class,
        name = CampaignComponentComponentIdVariableRequest.SETTING_TYPE),
})
public class CampaignComponentSettingRequest {

    protected static final String JSON_COMPONENT_SETTING_NAME = "name";
    protected static final String JSON_COMPONENT_SETTING_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_SETTING_TYPE = "type";
    protected static final String JSON_COMPONENT_SETTING_TAGS = "tags";
    protected static final String JSON_COMPONENT_SETTING_PRIORITY = "priority";

    private final String name;
    private final Omissible<String> displayName;
    private final SettingType type;
    private final Omissible<Set<String>> tags;
    private final Omissible<DeweyDecimal> priority;

    @JsonCreator
    protected CampaignComponentSettingRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) String name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority) {
        this.name = name;
        this.displayName = displayName;
        this.type = type != null ? type : SettingType.STRING;
        this.tags = tags;
        this.priority = priority;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME)
    public Omissible<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_TYPE)
    public SettingType getType() {
        return type;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY)
    public Omissible<DeweyDecimal> getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder<?, ?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static class Builder<CALLER, RESULT extends CampaignComponentSettingRequest, BUILDER_TYPE extends Builder<
        CALLER, RESULT, BUILDER_TYPE>> {

        protected final CALLER caller;
        protected String name;
        protected Omissible<String> displayName = Omissible.omitted();
        protected SettingType type;
        protected Omissible<Set<String>> tags = Omissible.omitted();
        protected Omissible<DeweyDecimal> priority = Omissible.omitted();

        protected Builder() {
            this.caller = null;
        }

        protected Builder(CALLER caller) {
            this.caller = caller;
        }

        public BUILDER_TYPE withName(String name) {
            this.name = name;
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withDisplayName(String displayName) {
            this.displayName = Omissible.of(displayName);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withSettingType(SettingType type) {
            this.type = type;
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withTags(Set<String> tags) {
            this.tags = Omissible.of(Objects.requireNonNull(tags));
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withPriority(DeweyDecimal priority) {
            this.priority = Omissible.of(priority);
            return (BUILDER_TYPE) this;
        }

        public CALLER done() {
            return caller;
        }

        public RESULT build() {
            return (RESULT) new CampaignComponentSettingRequest(
                name,
                displayName,
                type,
                tags,
                priority);
        }

    }

}
