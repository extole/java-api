package com.extole.client.rest.campaign.built.component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentOwner;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltComponentResponse extends ComponentElementResponse {

    private static final String JSON_COMPONENT_ID = "id";
    private static final String JSON_CAMPAIGN_ID = "campaign_id";
    private static final String JSON_CAMPAIGN_STATE = "campaign_state";
    private static final String JSON_COMPONENT_OWNER = "owner";
    private static final String JSON_COMPONENT_VERSION = "component_version";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPE = "type";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_VARIABLES = "variables";
    private static final String JSON_COMPONENT_ASSETS = "assets";
    private static final String JSON_COMPONENT_CREATED_DATE = "created_date";
    private static final String JSON_COMPONENT_UPDATED_DATE = "updated_date";

    private final String id;
    private final String campaignId;
    private final String campaignState;
    private final ComponentOwner componentOwner;
    private final String componentVersion;
    private final String name;
    private final Optional<String> displayName;
    private final Optional<String> type;
    private final String description;
    private final Optional<String> installedIntoSocket;
    private final Set<String> tags;
    private final List<BuiltCampaignComponentSettingResponse> settings;
    private final List<BuiltCampaignComponentAssetResponse> assets;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public BuiltComponentResponse(
        @JsonProperty(JSON_COMPONENT_ID) String id,
        @JsonProperty(JSON_CAMPAIGN_ID) String campaignId,
        @JsonProperty(JSON_CAMPAIGN_STATE) String campaignState,
        @JsonProperty(JSON_COMPONENT_OWNER) ComponentOwner componentOwner,
        @JsonProperty(JSON_COMPONENT_VERSION) String componentVersion,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_TYPE) Optional<String> type,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) String description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Optional<String> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLES) List<BuiltCampaignComponentSettingResponse> settings,
        @JsonProperty(JSON_COMPONENT_ASSETS) List<BuiltCampaignComponentAssetResponse> assets,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_COMPONENT_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_COMPONENT_UPDATED_DATE) ZonedDateTime updatedDate) {
        super(componentReferences, componentIds);
        this.id = id;
        this.campaignId = campaignId;
        this.campaignState = campaignState;
        this.componentOwner = componentOwner;
        this.componentVersion = componentVersion;
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.tags = Collections.unmodifiableSet(tags);
        this.settings = Collections.unmodifiableList(settings);
        this.assets = Collections.unmodifiableList(assets);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_COMPONENT_ID)
    public String getId() {
        return id;
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

    @JsonProperty(JSON_COMPONENT_VERSION)
    public String getComponentVersion() {
        return componentVersion;
    }

    @JsonProperty(JSON_COMPONENT_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_COMPONENT_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_COMPONENT_TYPE)
    public Optional<String> getType() {
        return type;
    }

    @JsonProperty(JSON_COMPONENT_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET)
    public Optional<String> getInstalledIntoSocket() {
        return installedIntoSocket;
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
