package com.extole.client.rest.campaign.configuration;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionStepSignalConfiguration extends CampaignControllerActionConfiguration {

    private static final String JSON_POLLING_ID = "polling_id";
    private static final String JSON_NAME = "name";

    private final String pollingId;
    private final String name;

    public CampaignControllerActionStepSignalConfiguration(
        @JsonProperty(JSON_ACTION_ID) Omissible<Id<CampaignControllerActionConfiguration>> actionId,
        @JsonProperty(JSON_QUALITY) CampaignControllerActionQuality quality,
        @JsonProperty(JSON_POLLING_ID) String pollingId,
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(actionId, CampaignControllerActionType.STEP_SIGNAL, quality, enabled, componentReferences);
        this.pollingId = pollingId;
        this.name = name;
    }

    @JsonProperty(JSON_POLLING_ID)
    public String getPollingId() {
        return pollingId;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

}
