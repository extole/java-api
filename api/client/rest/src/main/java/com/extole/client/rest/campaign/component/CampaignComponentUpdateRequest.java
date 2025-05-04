package com.extole.client.rest.campaign.component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentUpdateRequest extends ComponentElementRequest {

    private static final String JSON_COMPONENT_VERSION = "component_version";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPE = "type";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_INSTALL = "install";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_VARIABLES = "variables";

    private final Omissible<String> componentVersion;
    private final Omissible<String> name;
    private final Omissible<Optional<String>> displayName;
    private final Omissible<Optional<String>> type;
    private final Omissible<Optional<String>> description;
    private final Omissible<Optional<String>> installedIntoSocket;
    private final Omissible<Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install;
    private final Omissible<Set<String>> tags;
    private final Omissible<List<CampaignComponentSettingRequest>> settings;

    @JsonCreator
    public CampaignComponentUpdateRequest(@JsonProperty(JSON_COMPONENT_VERSION) Omissible<String> componentVersion,
        @JsonProperty(JSON_COMPONENT_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_TYPE) Omissible<Optional<String>> type,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) Omissible<Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Omissible<Optional<String>> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Omissible<
            Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLES) Omissible<List<CampaignComponentSettingRequest>> settings,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.componentVersion = componentVersion;
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = tags;
        this.settings = settings;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(JSON_COMPONENT_VERSION)
    public Omissible<String> getComponentVersion() {
        return componentVersion;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPE)
    public Omissible<Optional<String>> getType() {
        return type;
    }

    @JsonProperty(JSON_COMPONENT_DESCRIPTION)
    public Omissible<Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET)
    public Omissible<Optional<String>> getInstalledIntoSocket() {
        return installedIntoSocket;
    }

    @JsonProperty(JSON_COMPONENT_INSTALL)
    public Omissible<Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> getInstall() {
        return install;
    }

    @JsonProperty(JSON_COMPONENT_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @JsonProperty(JSON_COMPONENT_VARIABLES)
    public Omissible<List<CampaignComponentSettingRequest>> getSettings() {
        return settings;
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<String> componentVersion = Omissible.omitted();
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Optional<String>> displayName = Omissible.omitted();
        private Omissible<Optional<String>> type = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Optional<String>> installedIntoSocket = Omissible.omitted();
        private Omissible<Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders;
        private Omissible<List<CampaignComponentSettingRequest>> settings = Omissible.omitted();

        private Builder() {

        }

        public Builder withComponentVersion(String componentVersion) {
            this.componentVersion = Omissible.of(componentVersion);
            return this;
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(Optional.of(displayName));
            return this;
        }

        public Builder withType(String type) {
            this.type = Omissible.of(Optional.of(type));
            return this;
        }

        public Builder clearType() {
            this.type = Omissible.nullified();
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(Optional.ofNullable(description));
            return this;
        }

        public Builder clearDescription() {
            this.description = Omissible.nullified();
            return this;
        }

        public Builder withInstalledIntoSocket(String installedIntoSocket) {
            this.installedIntoSocket = Omissible.of(Optional.of(installedIntoSocket));
            return this;
        }

        public Builder withInstall(InstalltimeEvaluatable<ComponentInstalltimeContext, Void> install) {
            this.install = Omissible.of(Optional.of(install));
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public CampaignComponentVariableRequest.Builder<Builder, ?, ?> addSetting() {
            if (this.settingBuilders == null) {
                this.settingBuilders = Lists.newArrayList();
            }
            CampaignComponentVariableRequest.Builder<Builder, ?, ?> builder =
                CampaignComponentVariableRequest.builder(this);
            builder.withSettingType(SettingType.STRING);
            this.settingBuilders.add(builder);
            return builder;
        }

        public <T extends CampaignComponentSettingRequest.Builder> T addSettingBuilder(T builder) {
            if (this.settingBuilders == null) {
                this.settingBuilders = Lists.newArrayList();
            }
            this.settingBuilders.add(builder);
            return builder;
        }

        public Builder removeSettings() {
            this.settingBuilders = Lists.newArrayList();
            return this;
        }

        public Builder withNullSettings() {
            this.settings = Omissible.nullified();
            return this;
        }

        public CampaignComponentUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignComponentUpdateRequest(componentVersion,
                name,
                displayName,
                type,
                description,
                installedIntoSocket,
                install,
                tags,
                settingBuilders == null ? settings
                    : Omissible.of(settingBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(Collectors.toList())),
                componentIds,
                componentReferences);
        }
    }

}
