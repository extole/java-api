package com.extole.client.rest.campaign.component.setting;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;

public class CampaignComponentSocketAddComponentRequest {

    private static final String SOURCE_COMPONENT_ID = "source_component_id";
    private static final String COMPONENT_DISPLAY_NAME = "component_display_name";
    private static final String SETTINGS = "settings";

    private final Id<ComponentResponse> sourceComponentId;
    private final Omissible<String> componentDisplayName;
    private final Omissible<List<CampaignComponentSettingRequest>> settings;

    @JsonCreator
    public CampaignComponentSocketAddComponentRequest(
        @JsonProperty(SOURCE_COMPONENT_ID) Id<ComponentResponse> sourceComponentId,
        @JsonProperty(COMPONENT_DISPLAY_NAME) Omissible<String> componentDisplayName,
        @JsonProperty(SETTINGS) Omissible<List<CampaignComponentSettingRequest>> settings) {
        this.sourceComponentId = sourceComponentId;
        this.componentDisplayName = componentDisplayName;
        this.settings = settings;
    }

    @JsonProperty(SOURCE_COMPONENT_ID)
    public Id<ComponentResponse> getSourceComponentId() {
        return sourceComponentId;
    }

    @JsonProperty(COMPONENT_DISPLAY_NAME)
    public Omissible<String> getComponentDisplayName() {
        return componentDisplayName;
    }

    @JsonProperty(SETTINGS)
    public Omissible<List<CampaignComponentSettingRequest>> getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Id<ComponentResponse> sourceComponentId;
        private Omissible<String> componentDisplayName = Omissible.omitted();
        private final Omissible<List<CampaignComponentSettingRequest>> settings = Omissible.omitted();
        private List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders;

        public Builder withSourceComponentId(Id<ComponentResponse> sourceComponentId) {
            this.sourceComponentId = sourceComponentId;
            return this;
        }

        public Builder withComponentDisplayName(String componentDisplayName) {
            this.componentDisplayName = Omissible.of(componentDisplayName);
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

        public CampaignComponentSocketAddComponentRequest build() {
            return new CampaignComponentSocketAddComponentRequest(sourceComponentId, componentDisplayName,
                settingBuilders == null ? settings
                    : Omissible.of(settingBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(toUnmodifiableList())));
        }

    }

}
