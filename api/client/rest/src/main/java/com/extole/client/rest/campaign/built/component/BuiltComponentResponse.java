package com.extole.client.rest.campaign.built.component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentOriginResponse;
import com.extole.client.rest.campaign.component.ComponentOwner;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.component.facet.CampaignComponentFacetResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class BuiltComponentResponse extends ComponentElementResponse {

    protected static final String JSON_COMPONENT_ID = "id";
    protected static final String JSON_SOURCE_CLIENT_ID = "source_client_id";
    protected static final String JSON_CAMPAIGN_ID = "campaign_id";
    protected static final String JSON_CAMPAIGN_STATE = "campaign_state";
    protected static final String JSON_COMPONENT_OWNER = "owner";
    protected static final String JSON_VERSION = "version";
    protected static final String JSON_UPLOAD_VERSION = "upload_version";
    protected static final String JSON_ORIGIN = "origin";
    protected static final String JSON_COMPONENT_NAME = "name";
    protected static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    protected static final String JSON_COMPONENT_TYPE = "type";
    protected static final String JSON_COMPONENT_TYPES = "types";
    protected static final String JSON_COMPONENT_DESCRIPTION = "description";
    protected static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    protected static final String JSON_COMPONENT_INSTALL = "install";
    protected static final String JSON_COMPONENT_TAGS = "tags";
    protected static final String JSON_COMPONENT_VARIABLES = "variables";
    protected static final String JSON_COMPONENT_ASSETS = "assets";
    protected static final String JSON_COMPONENT_FACETS = "facets";
    protected static final String JSON_COMPONENT_CREATED_DATE = "created_date";
    protected static final String JSON_COMPONENT_UPDATED_DATE = "updated_date";

    private final String id;
    private final String sourceClientId;
    private final String campaignId;
    private final String campaignState;
    private final ComponentOwner componentOwner;
    private final Integer version;
    private final Optional<String> uploadVersion;
    private final Optional<ComponentOriginResponse> origin;
    private final String name;
    private final Optional<String> displayName;
    private final List<String> types;
    private final String description;
    private final Optional<String> installedIntoSocket;
    private final Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install;
    private final Set<String> tags;
    private final List<BuiltCampaignComponentSettingResponse> settings;
    private final List<BuiltCampaignComponentAssetResponse> assets;
    private final List<CampaignComponentFacetResponse> facets;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public BuiltComponentResponse(
        @JsonProperty(JSON_COMPONENT_ID) String id,
        @JsonProperty(JSON_SOURCE_CLIENT_ID) String sourceClientId,
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CAMPAIGN_STATE) String campaignState,
        @JsonProperty(JSON_COMPONENT_OWNER) ComponentOwner componentOwner,
        @JsonProperty(JSON_VERSION) Integer version,
        @JsonProperty(JSON_UPLOAD_VERSION) Optional<String> uploadVersion,
        @JsonProperty(JSON_ORIGIN) Optional<ComponentOriginResponse> origin,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_TYPES) List<String> types,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) String description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Optional<String> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Optional<
            InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLES) List<BuiltCampaignComponentSettingResponse> settings,
        @JsonProperty(JSON_COMPONENT_ASSETS) List<BuiltCampaignComponentAssetResponse> assets,
        @JsonProperty(JSON_COMPONENT_FACETS) List<CampaignComponentFacetResponse> facets,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_COMPONENT_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_COMPONENT_UPDATED_DATE) ZonedDateTime updatedDate) {
        super(componentReferences, componentIds);
        this.id = id;
        this.sourceClientId = sourceClientId;
        this.campaignId = campaignId;
        this.campaignState = campaignState;
        this.componentOwner = componentOwner;
        this.version = version;
        this.uploadVersion = uploadVersion;
        this.origin = origin;
        this.name = name;
        this.displayName = displayName;
        this.types = types == null ? List.of() : List.copyOf(types);
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = Collections.unmodifiableSet(tags);
        this.settings = Collections.unmodifiableList(settings);
        this.assets = Collections.unmodifiableList(assets);
        this.facets = Collections.unmodifiableList(facets);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_COMPONENT_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_SOURCE_CLIENT_ID)
    public String getSourceClientId() {
        return sourceClientId;
    }

    @JsonProperty(JSON_CAMPAIGN_ID)
    public String getCampaignId() {
        return campaignId;
    }

    @JsonProperty(JSON_CAMPAIGN_STATE)
    public String getCampaignState() {
        return campaignState;
    }

    @JsonProperty(JSON_COMPONENT_OWNER)
    public ComponentOwner getComponentOwner() {
        return componentOwner;
    }

    @JsonProperty(JSON_VERSION)
    public Integer getVersion() {
        return version;
    }

    @JsonProperty(JSON_UPLOAD_VERSION)
    public Optional<String> getUploadVersion() {
        return uploadVersion;
    }

    @JsonProperty(JSON_ORIGIN)
    public Optional<ComponentOriginResponse> getOrigin() {
        return origin;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPES)
    public List<String> getTypes() {
        return types;
    }

    @JsonProperty(JSON_COMPONENT_TYPE)
    public Optional<String> getType() {
        return types.stream().findFirst();
    }

    @JsonProperty(JSON_COMPONENT_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET)
    public Optional<String> getInstalledIntoSocket() {
        return installedIntoSocket;
    }

    @JsonProperty(JSON_COMPONENT_INSTALL)
    public Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> getInstall() {
        return install;
    }

    @JsonProperty(JSON_COMPONENT_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLES)
    public List<BuiltCampaignComponentSettingResponse> getSettings() {
        return settings;
    }

    @JsonProperty(JSON_COMPONENT_ASSETS)
    public List<BuiltCampaignComponentAssetResponse> getAssets() {
        return assets;
    }

    @JsonProperty(JSON_COMPONENT_FACETS)
    public List<CampaignComponentFacetResponse> getFacets() {
        return facets;
    }

    @JsonProperty(JSON_COMPONENT_CREATED_DATE)
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    @JsonProperty(JSON_COMPONENT_UPDATED_DATE)
    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
