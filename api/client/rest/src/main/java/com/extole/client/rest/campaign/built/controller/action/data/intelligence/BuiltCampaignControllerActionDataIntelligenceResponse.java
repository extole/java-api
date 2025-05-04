package com.extole.client.rest.campaign.built.controller.action.data.intelligence;

import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionType;
import com.extole.client.rest.campaign.controller.action.data.intelligence.DataIntelligenceProviderType;
import com.extole.id.Id;

public class BuiltCampaignControllerActionDataIntelligenceResponse extends BuiltCampaignControllerActionResponse {

    private static final String JSON_INTELLIGENCE_PROVIDER = "intelligence_provider";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_PROFILE_RISK_UPDATE_INTEVAL = "profile_risk_update_interval";

    private final DataIntelligenceProviderType intelligenceProvider;
    private final String eventName;
    private final Duration profileRiskUpdateInterval;

    @JsonCreator
    public BuiltCampaignControllerActionDataIntelligenceResponse(
        @JsonProperty(JSON_ACTION_ID) String actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_INTELLIGENCE_PROVIDER) DataIntelligenceProviderType intelligenceProvider,
        @JsonProperty(JSON_EVENT_NAME) String eventName,
        @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTEVAL) Duration profileRiskUpdateInterval,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(actionId, CampaignControllerActionType.DATA_INTELLIGENCE, quality, enabled, componentIds,
            componentReferences);
        this.intelligenceProvider = intelligenceProvider;
        this.eventName = eventName;
        this.profileRiskUpdateInterval = profileRiskUpdateInterval;
    }

    @JsonProperty(JSON_INTELLIGENCE_PROVIDER)
    public DataIntelligenceProviderType getIntelligenceProvider() {
        return intelligenceProvider;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTEVAL)
    public Duration getProfileRiskUpdateInterval() {
        return profileRiskUpdateInterval;
    }

}
