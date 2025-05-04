package com.extole.client.rest.campaign.component;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.client.rest.campaign.component.anchor.AnchorRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class ComponentDuplicateRequest extends ComponentElementRequest {
    private static final String TARGET_CAMPAIGN_ID = "target_campaign_id";
    private static final String TARGET_COMPONENT_ABSOLUTE_NAME = "target_component_absolute_name";
    private static final String TARGET_SOCKET_NAME = "target_socket_name";
    private static final String COMPONENT_NAME = "component_name";
    private static final String COMPONENT_DISPLAY_NAME = "component_display_name";
    private static final String COMPONENT_DESCRIPTION = "description";
    private static final String COMPONENT_TAGS = "tags";
    private static final String COMPONENT_VARIABLES = "variables";
    private static final String ANCHORS = "anchors";
    private static final String TYPE = "type";

    private final Omissible<String> targetCampaignId;
    private final Omissible<String> targetComponentAbsoluteName;
    private final Omissible<String> targetSocketName;
    private final Omissible<String> componentName;
    private final Omissible<String> componentDisplayName;
    private final Omissible<Optional<String>> description;
    private final Omissible<Set<String>> tags;
    private final Omissible<List<CampaignComponentSettingRequest>> settings;
    private final Omissible<List<AnchorRequest>> anchors;
    private final Omissible<Optional<String>> type;

    public ComponentDuplicateRequest(@JsonProperty(TARGET_CAMPAIGN_ID) Omissible<String> targetCampaignId,
        @JsonProperty(TARGET_COMPONENT_ABSOLUTE_NAME) Omissible<String> targetComponentAbsoluteName,
        @JsonProperty(TARGET_SOCKET_NAME) Omissible<String> targetSocketName,
        @JsonProperty(COMPONENT_NAME) Omissible<String> componentName,
        @JsonProperty(COMPONENT_DISPLAY_NAME) Omissible<String> componentDisplayName,
        @JsonProperty(COMPONENT_DESCRIPTION) Omissible<Optional<String>> description,
        @JsonProperty(COMPONENT_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(COMPONENT_VARIABLES) Omissible<List<CampaignComponentSettingRequest>> settings,
        @JsonProperty(ANCHORS) Omissible<List<AnchorRequest>> anchors,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(TYPE) Omissible<Optional<String>> type) {
        super(componentReferences, componentIds);
        this.targetCampaignId = targetCampaignId;
        this.targetComponentAbsoluteName = targetComponentAbsoluteName;
        this.targetSocketName = targetSocketName;
        this.componentName = componentName;
        this.componentDisplayName = componentDisplayName;
        this.description = description;
        this.tags = tags;
        this.settings = settings;
        this.anchors = anchors;
        this.type = type;
    }

    @JsonProperty(TARGET_CAMPAIGN_ID)
    public Omissible<String> getTargetCampaignId() {
        return targetCampaignId;
    }

    @JsonProperty(TARGET_COMPONENT_ABSOLUTE_NAME)
    public Omissible<String> getTargetComponentAbsoluteName() {
        return targetComponentAbsoluteName;
    }

    @JsonProperty(TARGET_SOCKET_NAME)
    public Omissible<String> getTargetSocketName() {
        return targetSocketName;
    }

    @Deprecated // TODO remove when UI starts using componentDisplayName ENG-24284
    @JsonProperty(COMPONENT_NAME)
    public Omissible<String> getComponentName() {
        return componentName;
    }

    @JsonProperty(COMPONENT_DISPLAY_NAME)
    public Omissible<String> getComponentDisplayName() {
        return componentDisplayName;
    }

    @JsonProperty(COMPONENT_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(COMPONENT_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @JsonProperty(COMPONENT_VARIABLES)
    public Omissible<List<CampaignComponentSettingRequest>> getSettings() {
        return settings;
    }

    @JsonProperty(ANCHORS)
    public Omissible<List<AnchorRequest>> getAnchors() {
        return anchors;
    }

    @JsonProperty(TYPE)
    public Omissible<Optional<String>> getType() {
        return type;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<String> targetCampaignId = Omissible.omitted();
        private Omissible<String> targetComponentAbsoluteName = Omissible.omitted();
        private Omissible<String> targetSocketName = Omissible.omitted();
        private Omissible<String> componentName = Omissible.omitted();
        private Omissible<String> componentDisplayName = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private final Omissible<List<CampaignComponentSettingRequest>> settings = Omissible.omitted();
        private List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders;
        private final Omissible<List<AnchorRequest>> anchors = Omissible.omitted();
        private List<AnchorRequest.Builder> anchorBuilders;
        private Omissible<Optional<String>> type = Omissible.omitted();

        private Builder() {

        }

        public Builder withTargetCampaignId(String targetCampaignId) {
            this.targetCampaignId = Omissible.of(targetCampaignId);
            return this;
        }

        public Builder withTargetComponentAbsoluteName(String targetComponentAbsoluteName) {
            this.targetComponentAbsoluteName = Omissible.of(targetComponentAbsoluteName);
            return this;
        }

        public Builder withTargetSocketName(String targetSocketName) {
            this.targetSocketName = Omissible.of(targetSocketName);
            return this;
        }

        public Builder withComponentName(String componentName) {
            this.componentName = Omissible.of(componentName);
            return this;
        }

        public Builder withComponentDisplayName(String componentDisplayName) {
            this.componentDisplayName = Omissible.of(componentDisplayName);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(Optional.of(description));
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignComponentVariableRequest.Builder<Builder, ?, ?> addSetting() {
            CampaignComponentVariableRequest.Builder<Builder, ?, ?> builder =
                CampaignComponentVariableRequest.builder(this);
            builder.withSettingType(SettingType.STRING);
            settingBuilders = Objects.requireNonNullElseGet(settingBuilders, Lists::newArrayList);
            this.settingBuilders.add(builder);
            return builder;
        }

        public <T extends CampaignComponentSettingRequest.Builder> T addSettingBuilder(T builder) {
            settingBuilders = Objects.requireNonNullElseGet(settingBuilders, Lists::newArrayList);
            this.settingBuilders.add(builder);
            return builder;
        }

        public AnchorRequest.Builder addAnchor() {
            AnchorRequest.Builder builder = AnchorRequest.builder();
            if (anchorBuilders == null) {
                anchorBuilders = Lists.newArrayList();
            }
            this.anchorBuilders.add(builder);
            return builder;
        }

        public Builder withType(String type) {
            this.type = Omissible.of(Optional.of(type));
            return this;
        }

        public Builder clearType() {
            this.type = Omissible.nullified();
            return this;
        }

        @Override
        public ComponentDuplicateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new ComponentDuplicateRequest(
                targetCampaignId,
                targetComponentAbsoluteName,
                targetSocketName,
                componentName,
                componentDisplayName,
                description,
                tags,
                settingBuilders == null ? settings
                    : Omissible.of(settingBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(toUnmodifiableList())),
                anchorBuilders == null ? anchors
                    : Omissible.of(anchorBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(toUnmodifiableList())),
                componentIds,
                componentReferences,
                type);
        }
    }

}
