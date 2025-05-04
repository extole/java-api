package com.extole.client.rest.campaign.component.setting;

import java.util.Objects;
import java.util.Optional;
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
    defaultImpl = CampaignComponentVariableUpdateRequest.class,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CampaignComponentClientKeyFlowVariableUpdateRequest.class,
        name = CampaignComponentClientKeyFlowVariableUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentPartnerEnumListVariableUpdateRequest.class,
        name = CampaignComponentPartnerEnumListVariableUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumVariableUpdateRequest.class,
        name = CampaignComponentEnumVariableUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentEnumListVariableUpdateRequest.class,
        name = CampaignComponentEnumListVariableUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentDelayListVariableUpdateRequest.class,
        name = CampaignComponentDelayListVariableUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentSocketUpdateRequest.class,
        name = CampaignComponentSocketUpdateRequest.SETTING_TYPE),
    @JsonSubTypes.Type(value = CampaignComponentRewardSupplierIdListVariableUpdateRequest.class,
        name = CampaignComponentRewardSupplierIdListVariableUpdateRequest.SETTING_TYPE),
})
@SuppressWarnings("rawtypes")
public class CampaignComponentSettingUpdateRequest {

    protected static final String JSON_COMPONENT_SETTING_NAME = "name";
    protected static final String JSON_COMPONENT_SETTING_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_SETTING_TYPE = "type";
    protected static final String JSON_COMPONENT_SETTING_TAGS = "tags";
    protected static final String JSON_COMPONENT_SETTING_PRIORITY = "priority";

    private final SettingType type;
    private final Omissible<String> name;
    private final Omissible<Optional<String>> displayName;
    private final Omissible<Set<String>> tags;
    private final Omissible<DeweyDecimal> priority;

    @JsonCreator
    protected CampaignComponentSettingUpdateRequest(@JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_SETTING_TYPE) SettingType type,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.tags = tags;
        this.priority = priority;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
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

    public static class Builder<CALLER, RESULT extends CampaignComponentSettingUpdateRequest,
        BUILDER_TYPE extends CampaignComponentSettingUpdateRequest.Builder<CALLER, RESULT, BUILDER_TYPE>> {

        protected final CALLER caller;
        protected Omissible<String> name = Omissible.omitted();
        protected Omissible<Optional<String>> displayName = Omissible.omitted();
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
            this.name = Omissible.of(name);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withDisplayName(String displayName) {
            this.displayName = Omissible.of(Optional.of(displayName));
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE clearDisplayName() {
            this.displayName = Omissible.nullified();
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
            return (RESULT) new CampaignComponentSettingUpdateRequest(
                name,
                displayName,
                type,
                tags,
                priority);
        }

    }

}
