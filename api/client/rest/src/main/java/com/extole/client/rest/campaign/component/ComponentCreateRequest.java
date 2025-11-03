package com.extole.client.rest.campaign.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.client.rest.campaign.component.facet.CampaignComponentFacetRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingRequest;
import com.extole.client.rest.campaign.component.setting.CampaignComponentVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class ComponentCreateRequest extends ComponentElementRequest {

    private static final String JSON_CAMPAIGN_ID = "campaign_id";
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

    private final Omissible<String> campaignId;
    private final Omissible<String> uploadVersion;
    private final String name;
    private final Omissible<String> displayName;
    private final Omissible<List<String>> types;
    private final Omissible<String> description;
    private final Omissible<String> installedIntoSocket;
    private final Omissible<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install;
    private final Omissible<Set<String>> tags;
    private final Omissible<List<CampaignComponentSettingRequest>> settings;
    private final Omissible<List<CampaignComponentFacetRequest>> facets;

    @JsonCreator
    public ComponentCreateRequest(
        @JsonProperty(JSON_CAMPAIGN_ID) Omissible<String> campaignId,
        @JsonProperty(JSON_UPLOAD_VERSION) Omissible<String> uploadVersion,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Omissible<String> displayName,
        @JsonProperty(JSON_COMPONENT_TYPES) Omissible<List<String>> types,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) Omissible<String> description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Omissible<String> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Omissible<InstalltimeEvaluatable<ComponentInstalltimeContext,
            Void>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTINGS) Omissible<List<CampaignComponentSettingRequest>> settings,
        @JsonProperty(JSON_COMPONENT_FACETS) Omissible<List<CampaignComponentFacetRequest>> facets,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.campaignId = campaignId;
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

    @JsonProperty(JSON_CAMPAIGN_ID)
    public Omissible<String> getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_UPLOAD_VERSION)
    public Omissible<String> getUploadVersion() {
        return uploadVersion;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public Omissible<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPES)
    public Omissible<List<String>> getTypes() {
        return types;
    }

    @JsonProperty(JSON_COMPONENT_DESCRIPTION)
    public Omissible<String> getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET)
    public Omissible<String> getInstalledIntoSocket() {
        return installedIntoSocket;
    }

    @JsonProperty(JSON_COMPONENT_INSTALL)
    public Omissible<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> getInstall() {
        return install;
    }

    @JsonProperty(JSON_COMPONENT_TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    public static Builder builder() {
        return new Builder();
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
        private Omissible<String> campaignId = Omissible.omitted();
        private Omissible<String> uploadVersion = Omissible.omitted();
        private String name;
        private Omissible<String> displayName = Omissible.omitted();
        private List<String> types;
        private Omissible<String> description = Omissible.omitted();
        private Omissible<String> installedIntoSocket = Omissible.omitted();
        private Omissible<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();
        private List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders;
        private List<CampaignComponentFacetRequest.Builder<Builder>> facetBuilders;

        private Builder() {
        }

        public Builder withCampaignId(String campaignId) {
            this.campaignId = Omissible.of(campaignId);
            return this;
        }

        public Builder withUploadVersion(String uploadVersion) {
            this.uploadVersion = Omissible.of(uploadVersion);
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = Omissible.of(displayName);
            return this;
        }

        public Builder withTypes(List<String> types) {
            if (this.types == null) {
                this.types = new ArrayList<>();
            }
            this.types.clear();
            this.types.addAll(types);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withInstalledIntoSocket(String installedIntoSocket) {
            this.installedIntoSocket = Omissible.of(installedIntoSocket);
            return this;
        }

        public Builder withInstall(InstalltimeEvaluatable<ComponentInstalltimeContext, Void> install) {
            this.install = Omissible.of(install);
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
            if (this.settingBuilders == null) {
                this.settingBuilders = new ArrayList<>();
            }
            this.settingBuilders.add(builder);
            return builder;
        }

        public <T extends CampaignComponentSettingRequest.Builder> T addSettingBuilder(T builder) {
            if (this.settingBuilders == null) {
                this.settingBuilders = new ArrayList<>();
            }
            this.settingBuilders.add(builder);
            return builder;
        }

        public CampaignComponentFacetRequest.Builder<Builder> addFacet() {
            CampaignComponentFacetRequest.Builder<Builder> builder =
                CampaignComponentFacetRequest.builder(this);
            if (this.facetBuilders == null) {
                this.facetBuilders = new ArrayList<>();
            }
            this.facetBuilders.add(builder);
            return builder;
        }

        public ComponentCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .toList());
            }

            Omissible<List<CampaignComponentFacetRequest>> facetRequests = facetBuilders == null
                ? Omissible.omitted()
                : Omissible.of(facetBuilders.stream().map(builder -> builder.build()).toList());
            Omissible<List<CampaignComponentSettingRequest>> settings = settingBuilders == null
                ? Omissible.omitted()
                : Omissible.of(settingBuilders.stream()
                    .map(builder -> (CampaignComponentSettingRequest) builder.build())
                    .toList());

            return new ComponentCreateRequest(
                campaignId,
                uploadVersion,
                name,
                displayName,
                types == null ? Omissible.omitted() : Omissible.of(types),
                description,
                installedIntoSocket,
                install,
                tags,
                settings,
                facetRequests,
                componentIds,
                componentReferences);
        }
    }

}
