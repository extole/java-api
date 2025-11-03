package com.extole.client.rest.campaign.component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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

@Deprecated // TODO as soon as UI stops using it ENG-26408, it will be removed, see ENG-26409
public class CampaignComponentCreateRequest extends ComponentElementRequest {

    private static final String JSON_UPLOAD_VERSION = "upload_version";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPE = "type"; // TODO Remove ENG-26014
    private static final String JSON_COMPONENT_TYPES = "types";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_INSTALL = "install";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_VARIABLES = "variables";
    private static final String JSON_COMPONENT_FACETS = "facets";

    private final String uploadVersion;
    private final String name;
    private final String displayName;
    private final List<String> types;
    private final String description;
    private final String installedIntoSocket;
    private final InstalltimeEvaluatable<ComponentInstalltimeContext, Void> install;
    private final Set<String> tags;
    private final List<? extends CampaignComponentSettingRequest> settings;
    private final List<CampaignComponentFacetRequest> facets;

    @JsonCreator
    public CampaignComponentCreateRequest(@Nullable @JsonProperty(JSON_UPLOAD_VERSION) String uploadVersion,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @Nullable @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) String displayName,
        @Nullable @JsonProperty(JSON_COMPONENT_TYPE) String type,
        @Nullable @JsonProperty(JSON_COMPONENT_TYPES) List<String> types,
        @Nullable @JsonProperty(JSON_COMPONENT_DESCRIPTION) String description,
        @Nullable @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) String installedIntoSocket,
        @Nullable @JsonProperty(JSON_COMPONENT_INSTALL) InstalltimeEvaluatable<ComponentInstalltimeContext,
            Void> install,
        @Nullable @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @Nullable @JsonProperty(JSON_COMPONENT_VARIABLES) List<CampaignComponentSettingRequest> settings,
        @JsonProperty(JSON_COMPONENT_FACETS) List<CampaignComponentFacetRequest> facets,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.uploadVersion = uploadVersion;
        this.name = name;
        this.displayName = displayName;
        this.types = types != null ? List.copyOf(types) : (type == null ? List.of() : List.of(type));
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = tags == null ? tags : Collections.unmodifiableSet(tags);
        this.settings = settings == null ? settings : Collections.unmodifiableList(settings);
        this.facets = facets == null ? facets : Collections.unmodifiableList(facets);
    }

    private static <CALLER, BUILDER extends CampaignComponentSettingRequest.Builder>
        Function<CALLER, CampaignComponentSettingRequest.Builder>
        builderFor(Function<CALLER, CampaignComponentSettingRequest.Builder> supplier) {
        return supplier;
    }

    @Nullable
    @JsonProperty(JSON_UPLOAD_VERSION)
    public String getUploadVersion() {
        return uploadVersion;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPE)
    public String getType() {
        return types.isEmpty() ? null : types.get(0);
    }

    @JsonProperty(JSON_COMPONENT_TYPES)
    public List<String> getTypes() {
        return types;
    }

    @Nullable
    @JsonProperty(JSON_COMPONENT_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Nullable
    @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET)
    public String getInstalledIntoSocket() {
        return installedIntoSocket;
    }

    @Nullable
    @JsonProperty(JSON_COMPONENT_INSTALL)
    public InstalltimeEvaluatable<ComponentInstalltimeContext, Void> getInstall() {
        return install;
    }

    @Nullable
    @JsonProperty(JSON_COMPONENT_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    @Nullable
    @JsonProperty(JSON_COMPONENT_VARIABLES)
    public List<? extends CampaignComponentSettingRequest> getSettings() {
        return settings;
    }

    @JsonProperty(JSON_COMPONENT_FACETS)
    public List<CampaignComponentFacetRequest> getFacets() {
        return facets;
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private String uploadVersion;
        private String name;
        private String displayName;
        private final List<String> types = Lists.newArrayList();
        private String description;
        private String installedIntoSocket;
        private InstalltimeEvaluatable<ComponentInstalltimeContext, Void> install;
        private Set<String> tags;
        private final List<CampaignComponentSettingRequest.Builder<Builder, ?, ?>> settingBuilders =
            Lists.newArrayList();
        private final List<CampaignComponentFacetRequest.Builder<Builder>> facetBuilders = Lists.newArrayList();

        private Builder() {

        }

        public Builder withUploadVersion(String uploadVersion) {
            this.uploadVersion = uploadVersion;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withType(String type) {
            this.types.clear();
            this.types.add(type);
            return this;
        }

        public Builder withTypes(List<String> types) {
            this.types.clear();
            this.types.addAll(types);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withInstalledIntoSocket(String installedIntoSocket) {
            this.installedIntoSocket = installedIntoSocket;
            return this;
        }

        public Builder withInstall(InstalltimeEvaluatable<ComponentInstalltimeContext, Void> install) {
            this.install = install;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = tags;
            return this;
        }

        public CampaignComponentVariableRequest.Builder<Builder, ?, ?> addSetting() {
            CampaignComponentVariableRequest.Builder<Builder, ?, ?> builder =
                CampaignComponentVariableRequest.builder(this);
            builder.withSettingType(SettingType.STRING);
            this.settingBuilders.add(builder);
            return builder;
        }

        public <T extends CampaignComponentSettingRequest.Builder> T addSettingBuilder(T builder) {
            this.settingBuilders.add(builder);
            return builder;
        }

        public CampaignComponentFacetRequest.Builder<Builder> addFacet() {
            CampaignComponentFacetRequest.Builder<Builder> builder =
                CampaignComponentFacetRequest.builder(this);
            this.facetBuilders.add(builder);
            return builder;
        }

        public CampaignComponentCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            List<CampaignComponentFacetRequest> facetRequests = facetBuilders.stream().map(builder -> builder.build())
                .collect(Collectors.toUnmodifiableList());
            return new CampaignComponentCreateRequest(uploadVersion,
                name,
                displayName,
                null,
                types,
                description,
                installedIntoSocket,
                install,
                tags,
                settingBuilders.stream().map(builder -> (CampaignComponentSettingRequest) builder.build())
                    .collect(Collectors.toList()),
                facetRequests,
                componentIds,
                componentReferences);
        }
    }

}
