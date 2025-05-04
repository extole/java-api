package com.extole.client.rest.campaign.controller.action.data.intelligence;

import java.time.Duration;
import java.util.List;

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

public class CampaignControllerActionDataIntelligenceResponse extends CampaignControllerActionResponse {

    private static final String JSON_INTELLIGENCE_PROVIDER = "intelligence_provider";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_PROFILE_RISK_UPDATE_INTEVAL = "profile_risk_update_interval";

    private final BuildtimeEvaluatable<ControllerBuildtimeContext, DataIntelligenceProviderType> intelligenceProvider;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName;
    private final BuildtimeEvaluatable<ControllerBuildtimeContext, Duration> profileRiskUpdateInterval;

    @JsonCreator
    public CampaignControllerActionDataIntelligenceResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_INTELLIGENCE_PROVIDER) BuildtimeEvaluatable<ControllerBuildtimeContext,
            DataIntelligenceProviderType> intelligenceProvider,
        @JsonProperty(JSON_EVENT_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName,
        @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTEVAL) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Duration> profileRiskUpdateInterval,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.DATA_INTELLIGENCE, quality, enabled, componentIds,
            componentReferences);
        this.intelligenceProvider = intelligenceProvider;
        this.eventName = eventName;
        this.profileRiskUpdateInterval = profileRiskUpdateInterval;
    }

    @JsonProperty(JSON_INTELLIGENCE_PROVIDER)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, DataIntelligenceProviderType> getIntelligenceProvider() {
        return intelligenceProvider;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTEVAL)
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Duration> getProfileRiskUpdateInterval() {
        return profileRiskUpdateInterval;
    }

}
