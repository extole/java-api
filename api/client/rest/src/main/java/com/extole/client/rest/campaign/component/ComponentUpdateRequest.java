package com.extole.client.rest.campaign.component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.client.rest.campaign.component.facet.CampaignComponentFacetRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class ComponentUpdateRequest extends ComponentElementRequest {

    private static final String JSON_UPLOAD_VERSION = "upload_version";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPES = "types";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_INSTALL = "install";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_SETTINGS = "settings";
    private static final String JSON_COMPONENT_FACETS = "facets";

    private final Omissible<Optional<String>> uploadVersion;
    private final Omissible<String> name;
    private final Omissible<Optional<String>> displayName;
    private final Omissible<List<String>> types;
    private final Omissible<Optional<String>> description;
    private final Omissible<Optional<String>> installedIntoSocket;
    private final Omissible<Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install;
    private final Omissible<Set<String>> tags;
    private final Omissible<List<CampaignComponentSettingRequest>> settings;
    private final Omissible<List<CampaignComponentFacetRequest>> facets;

    @JsonCreator
    public ComponentUpdateRequest(
        @JsonProperty(JSON_UPLOAD_VERSION) Omissible<Optional<String>> uploadVersion,
        @JsonProperty(JSON_COMPONENT_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_TYPES) Omissible<List<String>> types,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) Omissible<Optional<String>> description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Omissible<Optional<String>> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Omissible<
            Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTINGS) Omissible<List<CampaignComponentSettingRequest>> settings,
        @JsonProperty(JSON_COMPONENT_FACETS) Omissible<List<CampaignComponentFacetRequest>> facets,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.uploadVersion = uploadVersion;
        this.name = name;
        this.displayName = displayName;
        this.types = types;
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = tags;
        this.settings = settings;
        this.facets = facets;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonProperty(JSON_UPLOAD_VERSION)
    public Omissible<Optional<String>> getUploadVersion() {
        return uploadVersion;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public Omissible<String> getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public Omissible<Optional<String>> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPES)
    public Omissible<List<String>> getTypes() {
        return types;
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

    @JsonProperty(JSON_COMPONENT_SETTINGS)
    public Omissible<List<CampaignComponentSettingRequest>> getSettings() {
        return settings;
    }

    @JsonProperty(JSON_COMPONENT_FACETS)
    public Omissible<List<CampaignComponentFacetRequest>> getFacets() {
        return facets;
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<Optional<String>> uploadVersion = Omissible.omitted();
        private Omissible<String> name = Omissible.omitted();
        private Omissible<Optional<String>> displayName = Omissible.omitted();
        private Omissible<List<String>> types = Omissible.omitted();
        private Omissible<Optional<String>> description = Omissible.omitted();
        private Omissible<Optional<String>> installedIntoSocket = Omissible.omitted();
        private Omissible<Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>>> install =
            Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders;
        private Omissible<List<CampaignComponentSettingRequest>> settings = Omissible.omitted();
        private List<CampaignComponentFacetRequest.Builder<Builder>> facetBuilders;
        private Omissible<List<CampaignComponentFacetRequest>> facets = Omissible.omitted();

        private Builder() {

        }

        public Builder withUploadVersion(String uploadVersion) {
            this.uploadVersion = Omissible.of(Optional.ofNullable(uploadVersion));
            return this;
        }

        public Builder clearUploadVersion() {
            this.uploadVersion = Omissible.nullified();
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

        public Builder withTypes(List<String> types) {
            this.types = Omissible.of(types);
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

        public CampaignComponentFacetRequest.Builder<Builder> addFacet() {
            if (this.facetBuilders == null) {
                this.facetBuilders = Lists.newArrayList();
            }
            CampaignComponentFacetRequest.Builder<Builder> builder =
                CampaignComponentFacetRequest.builder(this);
            this.facetBuilders.add(builder);
            return builder;
        }

        public Builder removeFacets() {
            this.facetBuilders = Lists.newArrayList();
            return this;
        }

        public Builder withNullFacets() {
            this.facets = Omissible.nullified();
            return this;
        }

        public ComponentUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new ComponentUpdateRequest(
                uploadVersion,
                name,
                displayName,
                types,
                description,
                installedIntoSocket,
                install,
                tags,
                settingBuilders == null ? settings
                    : Omissible.of(settingBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(Collectors.toList())),
                facetBuilders == null ? facets
                    : Omissible.of(facetBuilders.stream()
                        .map(builder -> builder.build())
                        .collect(Collectors.toList())),
                componentIds,
                componentReferences);
        }
    }

}
