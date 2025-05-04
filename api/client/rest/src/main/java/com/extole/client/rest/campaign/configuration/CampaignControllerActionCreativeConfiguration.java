package com.extole.client.rest.campaign.configuration;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.controller.action.creative.Classification;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionCreativeConfiguration extends CampaignControllerActionConfiguration {
    private static final String JSON_CREATIVE_ARCHIVE_ID = "creative_archive_id";
    private static final String JSON_CREATIVE_ARCHIVE_API_VERSION = "creative_archive_api_version";
    private static final String JSON_CLASSIFICATION = "classification";
    private static final String JSON_THEME_VERSION = "theme_version";
    private static final String JSON_LATEST_VERSION = "latest_version";

    private final Optional<String> creativeArchiveId;
    private final Integer apiVersion;
    private final Classification classification;
    private final String themeVersion;
    private final Optional<Integer> latestVersion;

    public CampaignControllerActionCreativeConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_CREATIVE_ARCHIVE_ID) Optional<String> creativeArchiveId,
        @JsonProperty(JSON_CREATIVE_ARCHIVE_API_VERSION) Integer apiVersion,
        @JsonProperty(JSON_CLASSIFICATION) Classification classification,
        @JsonProperty(JSON_THEME_VERSION) String themeVersion,
        @JsonProperty(JSON_LATEST_VERSION) Optional<Integer> latestVersion,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.CREATIVE, quality, enabled, componentReferences);
        this.creativeArchiveId = creativeArchiveId;
        this.latestVersion = latestVersion;
        this.apiVersion = apiVersion;
        this.classification = classification;
        this.themeVersion = themeVersion;
    }

    @JsonProperty(JSON_CREATIVE_ARCHIVE_ID)
    public Optional<String> getCreativeArchiveId() {
        return creativeArchiveId;
    }

    @JsonProperty(JSON_CREATIVE_ARCHIVE_API_VERSION)
    public Integer getApiVersion() {
        return apiVersion;
    }

    @JsonProperty(JSON_CLASSIFICATION)
    public Classification getClassification() {
        return classification;
    }

    @JsonProperty(JSON_THEME_VERSION)
    public String getThemeVersion() {
        return themeVersion;
    }

    @JsonProperty(JSON_LATEST_VERSION)
    public Optional<Integer> getLatestVersion() {
        return latestVersion;
    }
}
