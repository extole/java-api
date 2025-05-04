package com.extole.client.rest.campaign.component;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.client.rest.campaign.component.asset.CampaignComponentAssetResponse;
import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentResponse extends ComponentElementResponse {

    private static final String JSON_COMPONENT_ID = "id";
    private static final String JSON_COMPONENT_VERSION = "component_version";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPE = "type";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_INSTALL = "install";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_VARIABLES = "variables";
    private static final String JSON_COMPONENT_ASSETS = "assets";
    private static final String JSON_COMPONENT_CREATED_DATE = "created_date";
    private static final String JSON_COMPONENT_UPDATED_DATE = "updated_date";

    private final String id;
    private final String componentVersion;
    private final String name;
    private final Optional<String> displayName;
    private final Optional<String> type;
    private final String description;
    private final Optional<String> installedIntoSocket;
    private final Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install;
    private final Set<String> tags;
    private final List<CampaignComponentSettingResponse> settings;
    private final List<CampaignComponentAssetResponse> assets;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public CampaignComponentResponse(
        @JsonProperty(JSON_COMPONENT_ID) String id,
        @JsonProperty(JSON_COMPONENT_VERSION) String componentVersion,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_TYPE) Optional<String> type,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) String description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Optional<String> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Optional<
            InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_VARIABLES) List<CampaignComponentSettingResponse> settings,
        @JsonProperty(JSON_COMPONENT_ASSETS) List<CampaignComponentAssetResponse> assets,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_COMPONENT_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_COMPONENT_UPDATED_DATE) ZonedDateTime updatedDate) {
        super(componentReferences, componentIds);
        this.id = id;
        this.componentVersion = componentVersion;
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = tags == null ? Collections.emptySet() : Collections.unmodifiableSet(tags);
        this.settings = settings == null ? Collections.emptyList() : Collections.unmodifiableList(settings);
        this.assets = assets == null ? Collections.emptyList() : Collections.unmodifiableList(assets);
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_COMPONENT_ID)
    public String getId() {
        return id;
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

    @JsonProperty(JSON_COMPONENT_INSTALL)
    public Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> getInstall() {
        return install;
    }

    @JsonProperty(JSON_COMPONENT_TAGS)
    public Set<String> getTags() {
        return tags;
    }

    @JsonProperty(JSON_COMPONENT_VARIABLES)
    public List<CampaignComponentSettingResponse> getSettings() {
        return settings;
    }

    @JsonProperty(JSON_COMPONENT_ASSETS)
    public List<CampaignComponentAssetResponse> getAssets() {
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
