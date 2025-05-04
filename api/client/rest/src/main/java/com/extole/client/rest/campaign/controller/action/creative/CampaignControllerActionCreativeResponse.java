package com.extole.client.rest.campaign.controller.action.creative;

import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionCreativeResponse extends CampaignControllerActionResponse {
    private static final String JSON_CLASSIFICATION = "classification";
    private static final String JSON_API_VERSION = "api_version";
    private static final String JSON_THEME_VERSION = "theme_version";
    private static final String JSON_HAS_CREATIVE = "has_creative";
    private static final String JSON_LATEST_VERSION = "latest_version";
    private static final String JSON_OUTPUT = "output";

    private final Classification classification;
    private final Integer creativeApiVersion;
    private final String themeVersion;
    private final boolean hasCreative;
    private final Optional<Integer> latestVersion;
    private final Map<String, List<String>> output;

    @JsonCreator
    public CampaignControllerActionCreativeResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_CLASSIFICATION) Classification classification,
        @JsonProperty(JSON_API_VERSION) Integer creativeApiVersion,
        @JsonProperty(JSON_THEME_VERSION) String themeVersion,
        @JsonProperty(JSON_HAS_CREATIVE) boolean hasCreative,
        @JsonProperty(JSON_LATEST_VERSION) Optional<Integer> latestVersion,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences,
        @JsonProperty(JSON_OUTPUT) Map<String, List<String>> output) {
        super(actionId, CampaignControllerActionType.CREATIVE, quality, enabled, componentIds, componentReferences);
        this.classification = classification;
        this.creativeApiVersion = creativeApiVersion;
        this.themeVersion = themeVersion;
        this.hasCreative = hasCreative;
        this.latestVersion = latestVersion;
        this.output = Objects.requireNonNullElse(output, emptyMap());
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

    @JsonProperty(JSON_OUTPUT)
    public Map<String, List<String>> getOutput() {
        return output;
    }
}
