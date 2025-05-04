package com.extole.client.rest.campaign.configuration;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.InstalltimeEvaluatable;
import com.extole.id.Id;

public class CampaignComponentConfiguration {

    private static final String JSON_COMPONENT_ID = "id";
    private static final String JSON_COMPONENT_VERSION = "component_version";
    private static final String JSON_COMPONENT_ABSOLUTE_NAMES = "absolute_names";
    private static final String JSON_COMPONENT_NAME = "name";
    private static final String JSON_COMPONENT_DISPLAY_NAME = "display_name";
    private static final String JSON_COMPONENT_TYPE = "type";
    private static final String JSON_COMPONENT_DESCRIPTION = "description";
    private static final String JSON_COMPONENT_INSTALLED_INTO_SOCKET = "installed_into_socket";
    private static final String JSON_COMPONENT_INSTALL = "install";
    private static final String JSON_COMPONENT_TAGS = "tags";
    private static final String JSON_COMPONENT_SETTINGS = "settings";
    private static final String JSON_COMPONENT_ASSETS = "assets";
    private static final String COMPONENT_REFERENCES = "component_references";
    private static final String JSON_COMPONENT_CREATED_DATE = "created_date";
    private static final String JSON_COMPONENT_UPDATED_DATE = "updated_date";

    private final Omissible<Id<CampaignComponentConfiguration>> id;
    private final String componentVersion;
    private final List<String> absoluteNames;
    private final String name;
    private final Optional<String> displayName;
    private final Optional<String> type;
    private final String description;
    private final Optional<String> installedIntoSocket;
    private final Optional<InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install;
    private final Set<String> tags;
    private final List<CampaignComponentSettingConfiguration> settings;
    private final List<CampaignComponentAssetConfiguration> assets;
    private final List<CampaignComponentReferenceConfiguration> componentReferences;
    private final ZonedDateTime createdDate;
    private final ZonedDateTime updatedDate;

    @JsonCreator
    public CampaignComponentConfiguration(
        @JsonProperty(JSON_COMPONENT_ID) Omissible<Id<CampaignComponentConfiguration>> id,
        @JsonProperty(JSON_COMPONENT_VERSION) String componentVersion,
        @JsonProperty(JSON_COMPONENT_ABSOLUTE_NAMES) List<String> absoluteNames,
        @JsonProperty(JSON_COMPONENT_NAME) String name,
        @JsonProperty(JSON_COMPONENT_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(JSON_COMPONENT_TYPE) Optional<String> type,
        @JsonProperty(JSON_COMPONENT_DESCRIPTION) String description,
        @JsonProperty(JSON_COMPONENT_INSTALLED_INTO_SOCKET) Optional<String> installedIntoSocket,
        @JsonProperty(JSON_COMPONENT_INSTALL) Optional<
            InstalltimeEvaluatable<ComponentInstalltimeContext, Void>> install,
        @JsonProperty(JSON_COMPONENT_TAGS) Set<String> tags,
        @JsonProperty(JSON_COMPONENT_SETTINGS) List<CampaignComponentSettingConfiguration> settings,
        @JsonProperty(JSON_COMPONENT_ASSETS) List<CampaignComponentAssetConfiguration> assets,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences,
        @JsonProperty(JSON_COMPONENT_CREATED_DATE) ZonedDateTime createdDate,
        @JsonProperty(JSON_COMPONENT_UPDATED_DATE) ZonedDateTime updatedDate) {
        this.id = id;
        this.componentVersion = componentVersion;
        this.absoluteNames = absoluteNames == null ? Collections.emptyList() : ImmutableList.copyOf(absoluteNames);
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.installedIntoSocket = installedIntoSocket;
        this.install = install;
        this.tags = tags == null ? Collections.emptySet() : Collections.unmodifiableSet(tags);
        this.settings = settings == null ? Collections.emptyList() : Collections.unmodifiableList(settings);
        this.assets = assets == null ? Collections.emptyList() : Collections.unmodifiableList(assets);
        this.componentReferences = componentReferences != null ? componentReferences : Collections.emptyList();
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    @JsonProperty(JSON_COMPONENT_ID)
    public Omissible<Id<CampaignComponentConfiguration>> getId() {
        return id;
    }

    @JsonProperty(JSON_COMPONENT_VERSION)
    public String getComponentVersion() {
        return componentVersion;
    }

    @JsonProperty(JSON_COMPONENT_ABSOLUTE_NAMES)
    public List<String> getAbsoluteNames() {
        return absoluteNames;
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

    @JsonProperty(JSON_COMPONENT_SETTINGS)
    public List<CampaignComponentSettingConfiguration> getSettings() {
        return settings;
    }

    @JsonProperty(JSON_COMPONENT_ASSETS)
    public List<CampaignComponentAssetConfiguration> getAssets() {
        return assets;
    }

    @JsonProperty(COMPONENT_REFERENCES)
    public List<CampaignComponentReferenceConfiguration> getComponentReferences() {
        return componentReferences;
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
