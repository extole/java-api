package com.extole.client.rest.campaign.built.controller.action.creative;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.creative.Classification;
import com.extole.id.Id;

public class BuiltCampaignControllerActionCreativeResponse extends BuiltCampaignControllerActionResponse {
    private static final String JSON_BUILD_VERSION = "build_version";
    private static final String JSON_CLASSIFICATION = "classification";
    private static final String JSON_API_VERSION = "api_version";
    private static final String JSON_THEME_VERSION = "theme_version";
    private static final String JSON_HAS_CREATIVE = "has_creative";
    private static final String JSON_LATEST_VERSION = "latest_version";

    private final Integer buildVersion;
    private final Classification classification;
    private final Integer creativeApiVersion;
    private final String themeVersion;
    private final boolean hasCreative;
    private final Optional<Integer> latestVersion;

    @JsonCreator
    public BuiltCampaignControllerActionCreativeResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_BUILD_VERSION) Integer buildVersion,
        @JsonProperty(JSON_CLASSIFICATION) Classification classification,
        @JsonProperty(JSON_API_VERSION) Integer creativeApiVersion,
        @JsonProperty(JSON_THEME_VERSION) String themeVersion,
        @JsonProperty(JSON_HAS_CREATIVE) boolean hasCreative,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_LATEST_VERSION) Optional<Integer> latestVersion) {
        super(actionId, CampaignControllerActionType.CREATIVE, quality, enabled, componentIds, componentReferences);
        this.buildVersion = buildVersion;
        this.classification = classification;
        this.creativeApiVersion = creativeApiVersion;
        this.themeVersion = themeVersion;
        this.hasCreative = hasCreative;
        this.latestVersion = latestVersion;
    }

    @JsonProperty(JSON_BUILD_VERSION)
    public Integer getBuildVersion() {
        return buildVersion;
    }

    @JsonProperty(JSON_CLASSIFICATION)
    public Classification getClassification() {
        return classification;
    }

    @JsonProperty(JSON_API_VERSION)
    public Integer getCreativeApiVersion() {
        return creativeApiVersion;
    }

    @JsonProperty(JSON_THEME_VERSION)
    public String getThemeVersion() {
        return themeVersion;
    }

    @JsonProperty(JSON_HAS_CREATIVE)
    public boolean getHasCreative() {
        return hasCreative;
    }

    @JsonProperty(JSON_LATEST_VERSION)
    public Optional<Integer> getLatestVersion() {
        return latestVersion;
    }

}
